package com.example.cookingapp.views.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cookingapp.R;
import com.example.cookingapp.adapters.CategoryAdapter;
import com.example.cookingapp.adapters.RecipeAdapter;
import com.example.cookingapp.models.Recipe;
import com.example.cookingapp.utils.PreferencesHelper;
import com.example.cookingapp.views.activities.RecipeDetailActivity;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class HomeFragment extends Fragment {

    private EditText edtHomeSearch;
    private RecyclerView rvRecipes, rvCategories;
    private RecipeAdapter recipeAdapter;
    private ProgressBar progressBar; // Thêm ProgressBar để thông báo đang tải

    private FirebaseFirestore db; // Biến Firestore
    private final List<Recipe> allRecipes = new ArrayList<>();
    private PreferencesHelper preferencesHelper;

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Khởi tạo Views
        edtHomeSearch = view.findViewById(R.id.edt_home_search);
        rvRecipes = view.findViewById(R.id.rv_recipes);
        rvCategories = view.findViewById(R.id.rv_categories);
        progressBar = view.findViewById(R.id.progress_bar); // Đảm bảo bạn có ID này trong XML

        // Khởi tạo Firestore và PreferencesHelper
        db = FirebaseFirestore.getInstance();
        preferencesHelper = new PreferencesHelper(getContext());

        // Cấu hình LayoutManager
        rvRecipes.setLayoutManager(new LinearLayoutManager(getContext()));
        rvCategories.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        setupCategories();

        // Khởi tạo Adapter với list trống ban đầu
        recipeAdapter = new RecipeAdapter(new ArrayList<>(), recipe -> {
            // Chuyển sang màn hình Chi tiết
            Intent intent = new Intent(getContext(), RecipeDetailActivity.class);
            intent.putExtra("recipe_data", recipe); // Gửi cả object Recipe đi
            startActivity(intent);
        });

        // Set favorite listener
        recipeAdapter.setFavoriteListener((recipe, isFavorite) -> {
            Toast.makeText(getContext(),
                isFavorite ? "Đã thêm vào yêu thích" : "Đã xóa khỏi yêu thích",
                Toast.LENGTH_SHORT).show();
        }, preferencesHelper);

        rvRecipes.setAdapter(recipeAdapter);

        // Tải dữ liệu thật từ Firestore
        loadRecipesFromFirestore();

        // Lắng nghe sự kiện Search
        edtHomeSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterRecipes(s == null ? "" : s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        return view;
    }

    private void setupCategories() {
        List<String> categories = new ArrayList<>();
        categories.add("Tất cả");
        categories.add("Món chay");
        categories.add("Ăn sáng");
        categories.add("Món mặn");
        categories.add("Tráng miệng");
        rvCategories.setAdapter(new CategoryAdapter(categories));
    }

    private void loadRecipesFromFirestore() {
        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);

        db.collection("recipes")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (progressBar != null) progressBar.setVisibility(View.GONE);

                    allRecipes.clear();
                    // Firebase tự động ép kiểu dữ liệu từ JSON sang Object Recipe
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Recipe recipe = document.toObject(Recipe.class);
                        // Ensure ID from Firestore document is stored in model
                        recipe.setId(document.getId());
                        allRecipes.add(recipe);
                    }

                    // Cập nhật lên UI
                    recipeAdapter.setData(new ArrayList<>(allRecipes));
                })
                .addOnFailureListener(e -> {
                    if (progressBar != null) progressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Lỗi tải dữ liệu: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("FirestoreError", e.getMessage());
                });
    }

    private void filterRecipes(String query) {
        String normalized = query.trim().toLowerCase(Locale.ROOT);
        if (normalized.isEmpty()) {
            recipeAdapter.setData(new ArrayList<>(allRecipes));
            return;
        }

        List<Recipe> filtered = new ArrayList<>();
        for (Recipe recipe : allRecipes) {
            // Sửa từ getTitle() thành getName() cho khớp với Model mới của Huy
            String name = recipe.getName();
            if (name != null && name.toLowerCase(Locale.ROOT).contains(normalized)) {
                filtered.add(recipe);
            }
        }
        recipeAdapter.setData(filtered);
    }
}