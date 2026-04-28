package com.example.cookingapp.models;

import com.google.firebase.Timestamp;

import java.io.Serializable;

public class Comment implements Serializable {
    private String id;
    private String userId;
    private String userName;
    private String text;
    private int rating;
    private Timestamp createdAt;

    public Comment() {}

    public Comment(String id, String userId, String userName, String text, int rating, Timestamp createdAt) {
        this.id = id;
        this.userId = userId;
        this.userName = userName;
        this.text = text;
        this.rating = rating;
        this.createdAt = createdAt;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}

