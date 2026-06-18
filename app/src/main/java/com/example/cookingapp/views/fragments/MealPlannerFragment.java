package com.example.cookingapp.views.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.cookingapp.R;
import com.example.cookingapp.models.MealPlan;
import com.example.cookingapp.models.Recipe;
import com.example.cookingapp.utils.PreferencesHelper;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class MealPlannerFragment extends Fragment {

    private FirebaseFirestore db;
    private PreferencesHelper preferencesHelper;
    private TextView tvDate;
    
    // Slot Views
    private TextView tvBreakfastName, tvBreakfastCalories;
    private TextView tvLunchName, tvLunchCalories;
    private TextView tvDinnerName, tvDinnerCalories;

    // Buttons
    private Button btnGoalLose, btnGoalGain;
    private Button btnChangeBreakfast, btnChangeLunch, btnChangeDinner;

    // Danh sách món ăn có sẵn
    private List<Recipe> allRecipes = new ArrayList<>();
    private String currentDate;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_meal_planner, container, false);
        
        db = FirebaseFirestore.getInstance();
        preferencesHelper = new PreferencesHelper(getContext());
        
        // Ánh xạ các View
        tvDate = view.findViewById(R.id.tv_current_date);
        tvBreakfastName = view.findViewById(R.id.tv_breakfast_name);
        tvBreakfastCalories = view.findViewById(R.id.tv_breakfast_calories);
        tvLunchName = view.findViewById(R.id.tv_lunch_name);
        tvLunchCalories = view.findViewById(R.id.tv_lunch_calories);
        tvDinnerName = view.findViewById(R.id.tv_dinner_name);
        tvDinnerCalories = view.findViewById(R.id.tv_dinner_calories);

        btnGoalLose = view.findViewById(R.id.btn_goal_lose);
        btnGoalGain = view.findViewById(R.id.btn_goal_gain);
        btnChangeBreakfast = view.findViewById(R.id.btn_change_breakfast);
        btnChangeLunch = view.findViewById(R.id.btn_change_lunch);
        btnChangeDinner = view.findViewById(R.id.btn_change_dinner);

        // Lấy ngày hiện tại
        currentDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
        tvDate.setText(currentDate);

        // Tải dữ liệu món ăn
        loadAllRecipes();

        // Load kế hoạch hiện tại
        String userId = preferencesHelper.getUserToken();
        if (userId != null) {
            loadMealPlan(userId, currentDate);
        }

        // Thiết lập sự kiện click
        if (btnGoalLose != null) {
            btnGoalLose.setOnClickListener(v -> generateSmartPlan("Giảm cân", currentDate));
        }
        if (btnGoalGain != null) {
            btnGoalGain.setOnClickListener(v -> generateSmartPlan("Tăng cân", currentDate));
        }

        if (btnChangeBreakfast != null) btnChangeBreakfast.setOnClickListener(v -> changeSingleSlot(1));
        if (btnChangeLunch != null) btnChangeLunch.setOnClickListener(v -> changeSingleSlot(2));
        if (btnChangeDinner != null) btnChangeDinner.setOnClickListener(v -> changeSingleSlot(3));

        return view;
    }

    private void loadAllRecipes() {
        db.collection("recipes")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    allRecipes.clear();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Recipe recipe = document.toObject(Recipe.class);
                        recipe.setId(document.getId());
                        allRecipes.add(recipe);
                    }
                })
                .addOnFailureListener(e -> Log.e("MealPlanner", "Error loading recipes: " + e.getMessage()));
    }

    /**
     * Hàm gợi ý thực đơn thông minh dựa trên mục tiêu (Goal)
     */
    public void generateSmartPlan(String goal, String date) {
        if (allRecipes == null || allRecipes.size() < 3) {
            Toast.makeText(getContext(), "Đang tải dữ liệu, vui lòng thử lại sau!", Toast.LENGTH_SHORT).show();
            loadAllRecipes();
            return;
        }

        String userId = preferencesHelper.getUserToken();
        if (userId == null || userId.isEmpty()) {
            Toast.makeText(getContext(), "Bạn cần đăng nhập để lưu kế hoạch", Toast.LENGTH_SHORT).show();
            return;
        }

        List<Recipe> selectedRecipes = new ArrayList<>();
        int attempts = 0;
        boolean found = false;

        while (attempts < 500 && !found) {
            selectedRecipes.clear();
            int totalCal = 0;
            
            List<Recipe> tempRecipes = new ArrayList<>(allRecipes);
            Collections.shuffle(tempRecipes);

            for (int i = 0; i < 3; i++) {
                Recipe r = tempRecipes.get(i);
                selectedRecipes.add(r);
                totalCal += r.getCalories();
            }

            if (goal.equalsIgnoreCase("Giảm cân")  && totalCal <= 1200) {
                found = true;
            } else if (goal.equalsIgnoreCase("Tăng cân") && totalCal >= 1500) {
                found = true;
            }
            attempts++;
        }

        if (found) {
            savePlanToFirestore(selectedRecipes, goal, userId, date);
        } else {
            Toast.makeText(getContext(), "Không tìm được thực đơn phù hợp mục tiêu, hãy thử lại!", Toast.LENGTH_SHORT).show();
        }
    }

    private void changeSingleSlot(int slot) {
        if (allRecipes.isEmpty()) return;
        String userId = preferencesHelper.getUserToken();
        if (userId == null) return;

        Recipe randomRecipe = allRecipes.get(new Random().nextInt(allRecipes.size()));
        
        db.collection("meal_plans")
                .whereEqualTo("userId", userId)
                .whereEqualTo("date", currentDate)
                .whereEqualTo("slot", slot)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    WriteBatch batch = db.batch();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        batch.delete(doc.getReference());
                    }
                    
                    MealPlan plan = new MealPlan();
                    plan.setUserId(userId);
                    plan.setDate(currentDate);
                    plan.setGoal("Custom");
                    plan.setSlot(slot);
                    plan.setRecipeId(randomRecipe.getId());
                    plan.setRecipeName(randomRecipe.getName());
                    plan.setCalories(randomRecipe.getCalories());
                    plan.setStatus(false);

                    batch.set(db.collection("meal_plans").document(), plan);
                    batch.commit().addOnSuccessListener(aVoid -> loadMealPlan(userId, currentDate));
                });
    }

    private void savePlanToFirestore(List<Recipe> selected, String goal, String userId, String date) {
        db.collection("meal_plans")
                .whereEqualTo("userId", userId)
                .whereEqualTo("date", date)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    WriteBatch batch = db.batch();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        batch.delete(doc.getReference());
                    }

                    for (int i = 0; i < selected.size(); i++) {
                        Recipe r = selected.get(i);
                        MealPlan plan = new MealPlan();
                        plan.setUserId(userId);
                        plan.setDate(date);
                        plan.setGoal(goal);
                        plan.setSlot(i + 1);
                        plan.setRecipeId(r.getId());
                        plan.setRecipeName(r.getName());
                        plan.setCalories(r.getCalories());
                        plan.setStatus(false);

                        batch.set(db.collection("meal_plans").document(), plan);
                    }

                    batch.commit().addOnSuccessListener(aVoid -> {
                        Toast.makeText(getContext(), "Đã lưu thực đơn gợi ý!", Toast.LENGTH_SHORT).show();
                        loadMealPlan(userId, date);
                    }).addOnFailureListener(e -> Log.e("Firestore", "Save failed: " + e.getMessage()));
                });
    }

    public void loadMealPlan(String userId, String date) {
        tvDate.setText(date);
        db.collection("meal_plans")
                .whereEqualTo("userId", userId)
                .whereEqualTo("date", date)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    resetMealUI();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        MealPlan plan = document.toObject(MealPlan.class);
                        updateSlotUI(plan);
                    }
                });
    }

    private void updateSlotUI(MealPlan plan) {
        String name = plan.getRecipeName();
        String calories = plan.getCalories() + " kcal";
        if (plan.getSlot() == 1) {
            tvBreakfastName.setText(name);
            tvBreakfastCalories.setText(calories);
        } else if (plan.getSlot() == 2) {
            tvLunchName.setText(name);
            tvLunchCalories.setText(calories);
        } else if (plan.getSlot() == 3) {
            tvDinnerName.setText(name);
            tvDinnerCalories.setText(calories);
        }
    }

    private void resetMealUI() {
        tvBreakfastName.setText("Chưa chọn");
        tvBreakfastCalories.setText("0 kcal");
        tvLunchName.setText("Chưa chọn");
        tvLunchCalories.setText("0 kcal");
        tvDinnerName.setText("Chưa chọn");
        tvDinnerCalories.setText("0 kcal");
    }
}
