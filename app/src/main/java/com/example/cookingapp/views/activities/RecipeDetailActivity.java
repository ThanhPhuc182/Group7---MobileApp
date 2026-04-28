package com.example.cookingapp.views.activities;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.cookingapp.R;
import com.example.cookingapp.models.Recipe;
import com.example.cookingapp.views.fragments.CommentsFragment;

public class RecipeDetailActivity extends AppCompatActivity {
    private ImageView imgRecipe;
    private TextView tvTitle, tvInfo, tvIngredients, tvSteps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        // Ánh xạ View
        imgRecipe = findViewById(R.id.img_detail_recipe);
        tvTitle = findViewById(R.id.tv_detail_title);
        tvInfo = findViewById(R.id.tv_detail_info);
        tvIngredients = findViewById(R.id.tv_detail_ingredients);
        tvSteps = findViewById(R.id.tv_detail_steps);

        // NHẬN DỮ LIỆU TỪ INTENT
        Recipe recipe = (Recipe) getIntent().getSerializableExtra("recipe_data");

        if (recipe != null) {
            displayRecipeDetails(recipe);

            // Attach comments fragment into container
            CommentsFragment f = CommentsFragment.newInstance(recipe.getId());
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.comments_fragment_container, f)
                    .commitAllowingStateLoss();
        }
        findViewById(R.id.btn_back).setOnClickListener(v -> {
            finish(); // Lệnh này sẽ đóng màn hình hiện tại và quay về màn hình trước đó
        });
    }

    private void displayRecipeDetails(Recipe recipe) {
        tvTitle.setText(recipe.getName());
        tvInfo.setText(recipe.getTime() + " phút | " + recipe.getCalories() + " kcal");

        // Load ảnh bằng Glide
        Glide.with(this).load(recipe.getImage_url()).into(imgRecipe);

        // HIỂN THỊ DANH SÁCH NGUYÊN LIỆU (List -> String)
        StringBuilder ingContent = new StringBuilder();
        for (String ing : recipe.getIngredients()) {
            ingContent.append("• ").append(ing).append("\n\n");
        }
        tvIngredients.setText(ingContent.toString());

        // HIỂN THỊ CÁC BƯỚC LÀM (List -> String)
        StringBuilder stepContent = new StringBuilder();
        for (int i = 0; i < recipe.getSteps().size(); i++) {
            stepContent.append(i + 1).append(". ").append(recipe.getSteps().get(i)).append("\n\n");
        }
        tvSteps.setText(stepContent.toString());
    }
}