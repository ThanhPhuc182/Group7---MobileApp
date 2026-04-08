package com.example.cookingapp.models;

public class Recipe {
    private String title;
    private String imageUrl;
    private String time;

    public Recipe(String title, String imageUrl, String time) {
        this.title = title;
        this.imageUrl = imageUrl;
        this.time = time;
    }

    // Getters (Để lấy dữ liệu ra)
    public String getTitle() { return title; }
    public String getImageUrl() { return imageUrl; }
    public String getTime() { return time; }
}