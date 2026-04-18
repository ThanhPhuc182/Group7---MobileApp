package com.example.cookingapp.views.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
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
import com.example.cookingapp.views.activities.RecipeDetailActivity;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SearchFragment extends Fragment {

    private EditText edtSearchQuery;
    private RecyclerView rvSearchResults;
    private RecipeAdapter recipeAdapter;
    private ProgressBar progressBar;

    private FirebaseFirestore db;
    private final List<Recipe> allRecipes = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        // Ánh xạ View
        edtSearchQuery = view.findViewById(R.id.edt_search_query);
        rvSearchResults = view.findViewById(R.id.rv_search_results);
        progressBar = view.findViewById(R.id.pb_search);

        // Cấu hình RecyclerView
        rvSearchResults.setLayoutManager(new LinearLayoutManager(getContext()));
        recipeAdapter = new RecipeAdapter(new ArrayList<>(), recipe -> {
            // Khi nhấn vào món ăn trong danh sách yêu thích, cũng mở màn hình chi tiết
            Intent intent = new Intent(getContext(), RecipeDetailActivity.class);
            intent.putExtra("recipe_data", recipe);
            startActivity(intent);
        });
        rvSearchResults.setAdapter(recipeAdapter);

        // Khởi tạo Firestore và tải dữ liệu
        db = FirebaseFirestore.getInstance();
        fetchAllRecipesFromFirestore();

        setupSearchInput();

        return view;
    }

    private void fetchAllRecipesFromFirestore() {
        progressBar.setVisibility(View.VISIBLE);
        db.collection("recipes")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    progressBar.setVisibility(View.GONE);
                    allRecipes.clear();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Recipe recipe = document.toObject(Recipe.class);
                        allRecipes.add(recipe);
                    }
                    // Ban đầu hiển thị tất cả món ăn
                    recipeAdapter.setData(new ArrayList<>(allRecipes));
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Lỗi tải dữ liệu", Toast.LENGTH_SHORT).show();
                });
    }

    private void setupSearchInput() {
        edtSearchQuery.setOnEditorActionListener((TextView v, int actionId, KeyEvent event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                    (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                performSearch();
                hideKeyboard(v);
                return true;
            }
            return false;
        });
    }

    private void performSearch() {
        String query = edtSearchQuery.getText().toString().trim().toLowerCase(Locale.ROOT);

        if (TextUtils.isEmpty(query)) {
            recipeAdapter.setData(new ArrayList<>(allRecipes));
            return;
        }

        List<Recipe> filteredRecipes = new ArrayList<>();
        for (Recipe recipe : allRecipes) {
            // Sửa từ getTitle() thành getName() để khớp dữ liệu thật
            if (recipe.getName() != null && recipe.getName().toLowerCase(Locale.ROOT).contains(query)) {
                filteredRecipes.add(recipe);
            }
        }

        recipeAdapter.setData(filteredRecipes);
    }

    private void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}