package com.example.cookingapp.views.fragments;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.cookingapp.R;
import com.example.cookingapp.adapters.RecipeAdapter;
import com.example.cookingapp.models.Recipe;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SearchFragment extends Fragment {

    private EditText edtSearchQuery;
    private RecyclerView rvSearchResults;
    private RecipeAdapter recipeAdapter;
    private final List<Recipe> allRecipes = new ArrayList<>();

    public SearchFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        edtSearchQuery = view.findViewById(R.id.edt_search_query);
        rvSearchResults = view.findViewById(R.id.rv_search_results);

        rvSearchResults.setLayoutManager(new LinearLayoutManager(getContext()));

        seedMockRecipes();
        recipeAdapter = new RecipeAdapter(new ArrayList<>(allRecipes));
        rvSearchResults.setAdapter(recipeAdapter);

        setupSearchInput();
        return view;
    }

    private void setupSearchInput() {
        edtSearchQuery.setOnEditorActionListener((TextView v, int actionId, KeyEvent event) -> {
            boolean isSearchAction = actionId == EditorInfo.IME_ACTION_SEARCH;
            boolean isEnterKey = event != null
                    && event.getAction() == KeyEvent.ACTION_DOWN
                    && event.getKeyCode() == KeyEvent.KEYCODE_ENTER;

            if (isSearchAction || isEnterKey) {
                performSearch();
                hideKeyboard(v);
                return true;
            }
            return false;
        });
    }

    private void performSearch() {
        String query = edtSearchQuery.getText() != null ? edtSearchQuery.getText().toString().trim() : "";
        if (TextUtils.isEmpty(query)) {
            recipeAdapter = new RecipeAdapter(new ArrayList<>(allRecipes));
            rvSearchResults.setAdapter(recipeAdapter);
            return;
        }

        String normalizedQuery = query.toLowerCase(Locale.ROOT);
        List<Recipe> filteredRecipes = new ArrayList<>();

        for (Recipe recipe : allRecipes) {
            if (recipe.getTitle() != null && recipe.getTitle().toLowerCase(Locale.ROOT).contains(normalizedQuery)) {
                filteredRecipes.add(recipe);
            }
        }

        recipeAdapter = new RecipeAdapter(filteredRecipes);
        rvSearchResults.setAdapter(recipeAdapter);
    }

    private void hideKeyboard(View view) {
        Context context = getContext();
        if (context == null) {
            return;
        }

        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void seedMockRecipes() {
        allRecipes.clear();
        allRecipes.add(new Recipe("Cơm chiên Thái", "", "20 phút"));
        allRecipes.add(new Recipe("Mì xào Hải Phòng", "", "25 phút"));
        allRecipes.add(new Recipe("Bún chả Hà Nội", "", "40 phút"));
        allRecipes.add(new Recipe("Phở bò", "", "50 phút"));
        allRecipes.add(new Recipe("Gà nướng mật ong", "", "60 phút"));
    }
}