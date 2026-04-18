package com.example.cookingapp.views.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cookingapp.R;
import com.example.cookingapp.adapters.RecipeAdapter;
import com.example.cookingapp.models.Recipe;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class FavoriteFragment extends Fragment {

    private RecyclerView rvFavorites;
    private RecipeAdapter recipeAdapter;
    private ProgressBar progressBar;
    private FirebaseFirestore db;
    private List<Recipe> favoriteList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorite, container, false);

        // Khởi tạo Firestore và View
        db = FirebaseFirestore.getInstance();
        rvFavorites = view.findViewById(R.id.rv_favorites);
        progressBar = view.findViewById(R.id.pb_favorite);

        rvFavorites.setLayoutManager(new LinearLayoutManager(getContext()));

        // Khởi tạo Adapter với list trống
        recipeAdapter = new RecipeAdapter(new ArrayList<>());
        rvFavorites.setAdapter(recipeAdapter);

        // Tải dữ liệu thật
        loadFavoriteRecipes();

        return view;
    }

    private void loadFavoriteRecipes() {
        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);

        // Tạm thời lấy tất cả món ăn để kiểm tra hiển thị UI
        // Sau này Huy sẽ sửa query này để chỉ lấy món người dùng đã lưu
        db.collection("recipes")
                .limit(5) // Giới hạn 5 món để demo
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (progressBar != null) progressBar.setVisibility(View.GONE);

                    favoriteList.clear();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Recipe recipe = document.toObject(Recipe.class);
                        favoriteList.add(recipe);
                    }

                    // Cập nhật lên Adapter bằng hàm setData chúng ta đã sửa
                    recipeAdapter.setData(favoriteList);
                })
                .addOnFailureListener(e -> {
                    if (progressBar != null) progressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}