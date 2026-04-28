package com.example.cookingapp.models;

import java.util.List;

public class Recipe implements java.io.Serializable {
    private String id;
    private String name;
    private String category;
    private List<String> tags;
    private int time;
    private int calories;
    private String image_url;
    // Bổ sung thêm 2 trường này để khớp với Firestore của bạn
    private List<String> ingredients;
    private List<String> steps;
    // Thêm trường để check nếu là favorite
    private transient boolean isFavorite = false;
    private int matchScore;

    // 1. Bắt buộc phải có Constructor trống cho Firebase
    public Recipe() {}

    // 2. Constructor đầy đủ các trường
    public Recipe(String id, String name, String category, List<String> tags, int time, int calories, String image_url, List<String> ingredients, List<String> steps) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.tags = tags;
        this.time = time;
        this.calories = calories;
        this.image_url = image_url;
        this.ingredients = ingredients;
        this.steps = steps;
    }

    // 3. Getters và Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }

    public int getTime() { return time; }
    public void setTime(int time) { this.time = time; }

    public int getCalories() { return calories; }
    public void setCalories(int calories) { this.calories = calories; }

    public String getImage_url() { return image_url; }
    public void setImage_url(String image_url) { this.image_url = image_url; }

    public List<String> getIngredients() { return ingredients; }
    public void setIngredients(List<String> ingredients) { this.ingredients = ingredients; }

    public List<String> getSteps() { return steps; }
    public void setSteps(List<String> steps) { this.steps = steps; }

    public boolean isFavorite() { return isFavorite; }
    public void setFavorite(boolean favorite) { isFavorite = favorite; }
    public int getMatchScore() {
        return matchScore;
    }
    public void setMatchScore(int matchScore) {
        this.matchScore = matchScore;
    }
}