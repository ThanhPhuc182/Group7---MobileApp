package com.example.cookingapp.views.activities;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.cookingapp.R;
import com.example.cookingapp.views.fragments.MealPlannerFragment;

public class MealPlannerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meal_planner);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container_meal_planner, new MealPlannerFragment())
                    .commit();
        }
    }
}
