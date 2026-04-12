package com.example.cookingapp.views.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cookingapp.R;
import com.example.cookingapp.adapters.CategoryAdapter;
import com.example.cookingapp.adapters.RecipeAdapter;
import com.example.cookingapp.models.Recipe;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class HomeFragment extends Fragment {

    private EditText edtHomeSearch;
    private RecyclerView rvRecipes;
    private RecyclerView rvCategories;
    private RecipeAdapter recipeAdapter;
    private final List<Recipe> allRecipes = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        edtHomeSearch = view.findViewById(R.id.edt_home_search);
        rvRecipes = view.findViewById(R.id.rv_recipes);
        rvCategories = view.findViewById(R.id.rv_categories);

        rvRecipes.setLayoutManager(new LinearLayoutManager(getContext()));
        rvCategories.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        setupCategories();
        seedMockRecipes();

        recipeAdapter = new RecipeAdapter(new ArrayList<>(allRecipes));
        rvRecipes.setAdapter(recipeAdapter);

        edtHomeSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterRecipes(s == null ? "" : s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
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

    private void seedMockRecipes() {
        allRecipes.clear();
        allRecipes.add(new Recipe("Sườn xào chua ngọt", "", "45 phút"));
        allRecipes.add(new Recipe("Phở bò Hà Nội", "", "120 phút"));
        allRecipes.add(new Recipe("Gỏi cuốn tôm thịt", "", "30 phút"));
        allRecipes.add(new Recipe("Cơm chiên hải sản", "", "25 phút"));
        allRecipes.add(new Recipe("Canh bí đỏ", "", "20 phút"));
        allRecipes.add(new Recipe("Bánh flan", "", "35 phút"));
    }

    private void filterRecipes(String query) {
        String normalized = query.trim().toLowerCase(Locale.ROOT);
        if (normalized.isEmpty()) {
            recipeAdapter.updateRecipes(new ArrayList<>(allRecipes));
            return;
        }

        List<Recipe> filtered = new ArrayList<>();
        for (Recipe recipe : allRecipes) {
            String title = recipe.getTitle();
            if (title != null && title.toLowerCase(Locale.ROOT).contains(normalized)) {
                filtered.add(recipe);
            }
        }
        recipeAdapter.updateRecipes(filtered);
    }
}