package com.example.cookingapp.views.fragments;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cookingapp.R;
import com.example.cookingapp.adapters.CommentAdapter;
import com.example.cookingapp.models.Comment;
import com.example.cookingapp.repositories.CommentRepository;
import com.example.cookingapp.utils.PreferencesHelper;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.List;

public class CommentsFragment extends Fragment {
    public static final String ARG_RECIPE_ID = "arg_recipe_id";

    private String recipeId;
    private CommentRepository repository;
    private CommentAdapter adapter;
    private ListenerRegistration listenerRegistration;
    private PreferencesHelper preferencesHelper;

    private RatingBar rbAvg, rbInput;
    private TextView tvAvgText;
    private RecyclerView rvComments;
    private EditText etComment;
    private Button btnPost;

    public static CommentsFragment newInstance(String recipeId) {
        CommentsFragment f = new CommentsFragment();
        Bundle b = new Bundle();
        b.putString(ARG_RECIPE_ID, recipeId);
        f.setArguments(b);
        return f;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_comments, container, false);

        rbAvg = v.findViewById(R.id.rb_avg);
        tvAvgText = v.findViewById(R.id.tv_avg_text);
        rvComments = v.findViewById(R.id.rv_comments);
        rbInput = v.findViewById(R.id.rb_input);
        etComment = v.findViewById(R.id.et_comment);
        btnPost = v.findViewById(R.id.btn_post_comment);

        rvComments.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new CommentAdapter();
        rvComments.setAdapter(adapter);

        repository = new CommentRepository();
        preferencesHelper = new PreferencesHelper(getContext());

        if (getArguments() != null) recipeId = getArguments().getString(ARG_RECIPE_ID);

        btnPost.setOnClickListener(view -> postComment());

        startListening();

        return v;
    }

    private void postComment() {
        if (recipeId == null || recipeId.isEmpty()) {
            Toast.makeText(getContext(), "Không xác định được món ăn để đánh giá", Toast.LENGTH_SHORT).show();
            return;
        }
        String text = etComment.getText().toString().trim();
        int rating = (int) rbInput.getRating();
        if (rating < 1) rating = 1;
        if (TextUtils.isEmpty(text)) {
            Toast.makeText(getContext(), "Vui lòng nhập nhận xét", Toast.LENGTH_SHORT).show();
            return;
        }
        String userId = preferencesHelper.getUserToken();
        String userName = preferencesHelper.getUserName();

        Comment c = new Comment();
        c.setUserId(TextUtils.isEmpty(userId) ? "anonymous" : userId);
        c.setUserName(TextUtils.isEmpty(userName) ? "Người dùng" : userName);
        c.setText(text);
        c.setRating(rating);
        c.setCreatedAt(Timestamp.now());

        btnPost.setEnabled(false);
        repository.postComment(recipeId, c, docRef -> {
            btnPost.setEnabled(true);
            etComment.setText("");
            rbInput.setRating(5);
            Toast.makeText(getContext(), "Đã gửi", Toast.LENGTH_SHORT).show();
        }, e -> {
            btnPost.setEnabled(true);
            Toast.makeText(getContext(), "Gửi thất bại: " + e.getMessage(), Toast.LENGTH_LONG).show();
        });
    }

    private void startListening() {
        if (recipeId == null) return;
        listenerRegistration = repository.listenComments(recipeId, (value, error) -> {
            if (error != null) return;
            List<Comment> list = new ArrayList<>();
            if (value != null) {
                double sum = 0; int cnt = 0;
                for (QueryDocumentSnapshot doc : value) {
                    Comment c = doc.toObject(Comment.class);
                    c.setId(doc.getId());
                    list.add(c);
                    if (c.getRating() > 0) { sum += c.getRating(); cnt++; }
                }
                if (cnt > 0) {
                    double avg = sum / cnt;
                    rbAvg.setRating((float) avg);
                    tvAvgText.setText(String.format("%.1f (%d)", avg, cnt));
                } else {
                    rbAvg.setRating(0);
                    tvAvgText.setText("Chưa có đánh giá");
                }
            }
            adapter.setData(list);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (listenerRegistration != null) listenerRegistration.remove();
    }
}
