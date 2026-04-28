package com.example.cookingapp.views.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cookingapp.R;
import com.example.cookingapp.adapters.RecipeAdapter;
import com.example.cookingapp.models.Recipe;
import com.example.cookingapp.utils.PreferencesHelper;
import com.example.cookingapp.views.activities.RecipeDetailActivity;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class FavoriteFragment extends Fragment {

    private RecyclerView rvFavorites;
    private RecipeAdapter recipeAdapter;
    private ProgressBar progressBar;
    private FirebaseFirestore db;
    private List<Recipe> favoriteList = new ArrayList<>();
    private PreferencesHelper preferencesHelper;
    private TextView tvEmptyMessage;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorite, container, false);

        // Khởi tạo Firestore và View
        db = FirebaseFirestore.getInstance();
        preferencesHelper = new PreferencesHelper(getContext());
        rvFavorites = view.findViewById(R.id.rv_favorites);
        progressBar = view.findViewById(R.id.pb_favorite);
        tvEmptyMessage = view.findViewById(R.id.tv_empty_message);

        rvFavorites.setLayoutManager(new LinearLayoutManager(getContext()));

        // Khởi tạo Adapter với list trống
        recipeAdapter = new RecipeAdapter(new ArrayList<>(), recipe -> {
            // Khi nhấn vào món ăn trong danh sách yêu thích, mở màn hình chi tiết
            Intent intent = new Intent(getContext(), RecipeDetailActivity.class);
            intent.putExtra("recipe_data", recipe);
            startActivity(intent);
        });

        // Set favorite listener
        recipeAdapter.setFavoriteListener((recipe, isFavorite) -> {
            // Khi remove favorite, reload danh sách
            loadFavoriteRecipes();
        }, preferencesHelper);

        rvFavorites.setAdapter(recipeAdapter);

        // Tải dữ liệu thật
        loadFavoriteRecipes();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Reload danh sách yêu thích mỗi khi quay lại fragment
        loadFavoriteRecipes();
    }

    private void loadFavoriteRecipes() {
        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);
        if (tvEmptyMessage != null) tvEmptyMessage.setVisibility(View.GONE);

        // Lấy danh sách ID của các món yêu thích
        java.util.Set<String> favoriteIds = preferencesHelper.getFavoriteIds();

        if (favoriteIds.isEmpty()) {
            // Nếu không có yêu thích, hiển thị thông báo
            if (progressBar != null) progressBar.setVisibility(View.GONE);
            if (tvEmptyMessage != null) {
                tvEmptyMessage.setVisibility(View.VISIBLE);
                tvEmptyMessage.setText("Bạn chưa lưu món yêu thích nào!\nHãy thêm các món yêu thích của bạn.");
            }
            recipeAdapter.setData(new ArrayList<>());
            return;
        }

        // Query Firestore để lấy các món yêu thích
        db.collection("recipes")
                .whereIn("id", new ArrayList<>(favoriteIds))
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (progressBar != null) progressBar.setVisibility(View.GONE);

                    favoriteList.clear();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Recipe recipe = document.toObject(Recipe.class);
                        recipe.setId(document.getId());
                        recipe.setFavorite(true);
                        favoriteList.add(recipe);
                    }

                    // Nếu không có kết quả, hiển thị thông báo
                    if (favoriteList.isEmpty()) {
                        if (tvEmptyMessage != null) {
                            tvEmptyMessage.setVisibility(View.VISIBLE);
                            tvEmptyMessage.setText("Không tìm thấy các món yêu thích!");
                        }
                    } else if (tvEmptyMessage != null) {
                        tvEmptyMessage.setVisibility(View.GONE);
                    }

                    // Cập nhật lên Adapter
                    recipeAdapter.setData(favoriteList);
                })
                .addOnFailureListener(e -> {
                    if (progressBar != null) progressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}