package com.example.cookingapp.repositories;

import androidx.annotation.NonNull;

import com.example.cookingapp.models.Comment;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.Transaction;

import java.util.HashMap;
import java.util.Map;

public class CommentRepository {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    // Post comment into subcollection recipes/{recipeId}/comments and update recipe avg via transaction
    public void postComment(@NonNull String recipeId, @NonNull Comment comment,
                            @NonNull OnSuccessListener<DocumentReference> onSuccess,
                            @NonNull OnFailureListener onFailure) {
        Map<String, Object> data = new HashMap<>();
        data.put("userId", comment.getUserId());
        data.put("userName", comment.getUserName());
        data.put("text", comment.getText());
        data.put("rating", comment.getRating());
        data.put("createdAt", comment.getCreatedAt() != null ? comment.getCreatedAt() : Timestamp.now());

        DocumentReference recipeRef = db.collection("recipes").document(recipeId);
        // create commentRef ahead so we can return it on success
        DocumentReference commentRef = recipeRef.collection("comments").document();

        db.runTransaction((Transaction.Function<Void>) transaction -> {
            // READ first (required by Firestore transactions)
            DocumentSnapshot snap = transaction.get(recipeRef);
            double oldAvg = 0.0;
            long oldCount = 0;
            if (snap.exists()) {
                Object avgObj = snap.get("averageRating");
                Object cntObj = snap.get("ratingCount");
                if (avgObj instanceof Number) oldAvg = ((Number) avgObj).doubleValue();
                if (cntObj instanceof Number) oldCount = ((Number) cntObj).longValue();
            }

            // compute new aggregates
            double newAvg = (oldAvg * oldCount + comment.getRating()) / (oldCount + 1);
            long newCount = oldCount + 1;

            // then WRITE: add comment and update recipe
            transaction.set(commentRef, data);
            Map<String, Object> updates = new HashMap<>();
            updates.put("averageRating", newAvg);
            updates.put("ratingCount", newCount);
            transaction.set(recipeRef, updates, SetOptions.merge());

            return null;
        }).addOnSuccessListener(aVoid -> {
            // return the created comment reference to caller
            onSuccess.onSuccess(commentRef);
        }).addOnFailureListener(onFailure);
    }

    public ListenerRegistration listenComments(String recipeId, com.google.firebase.firestore.EventListener<QuerySnapshot> listener) {
        return db.collection("recipes")
                .document(recipeId)
                .collection("comments")
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .addSnapshotListener(listener);
    }

}
