package com.example.cookingapp.models;

import java.io.Serializable;

public class MealPlan implements Serializable {
    private String id;
    private int calories;
    private String date;
    private String goal;
    private String recipeId;
    private String recipeName;
    private int slot;
    private boolean status;
    private String userId;

    // Constructor rỗng bắt buộc cho Firebase
    public MealPlan() {
    }

    public MealPlan(String id, int calories, String date, String goal, String recipeId, String recipeName, int slot, boolean status, String userId) {
        this.id = id;
        this.calories = calories;
        this.date = date;
        this.goal = goal;
        this.recipeId = recipeId;
        this.recipeName = recipeName;
        this.slot = slot;
        this.status = status;
        this.userId = userId;
    }

    // Getters và Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getCalories() {
        return calories;
    }

    public void setCalories(int calories) {
        this.calories = calories;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getGoal() {
        return goal;
    }

    public void setGoal(String goal) {
        this.goal = goal;
    }

    public String getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(String recipeId) {
        this.recipeId = recipeId;
    }

    public String getRecipeName() {
        return recipeName;
    }

    public void setRecipeName(String recipeName) {
        this.recipeName = recipeName;
    }

    public int getSlot() {
        return slot;
    }

    public void setSlot(int slot) {
        this.slot = slot;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
