package com.example.cookingapp.views.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class FavoriteFragment extends Fragment {

    private RecyclerView rvFavorites;
    private RecipeAdapter recipeAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorite, container, false);

        rvFavorites = view.findViewById(R.id.rv_favorites);
        rvFavorites.setLayoutManager(new LinearLayoutManager(getContext()));

        List<Recipe> favoriteRecipes = new ArrayList<>();
        favoriteRecipes.add(new Recipe("Gà nướng mật ong", "", "60 phút"));
        favoriteRecipes.add(new Recipe("Bún chả Hà Nội", "", "40 phút"));
        favoriteRecipes.add(new Recipe("Bánh flan", "", "35 phút"));

        recipeAdapter = new RecipeAdapter(favoriteRecipes);
        rvFavorites.setAdapter(recipeAdapter);

        return view;
    }
}