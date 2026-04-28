package com.example.cookingapp.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashSet;
import java.util.Set;

public class PreferencesHelper {

    private static final String PREF_NAME = "CookingApp_Prefs";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";
    private static final String KEY_USER_NAME = "user_name";
    private static final String KEY_USER_EMAIL = "user_email";
    private static final String KEY_USER_TOKEN = "user_token";
    private static final String KEY_FAVORITES = "favorites";

    private final SharedPreferences sharedPreferences;

    public PreferencesHelper(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void saveUserData(String token, String name, String email) {
        sharedPreferences.edit()
                .putBoolean(KEY_IS_LOGGED_IN, true)
                .putString(KEY_USER_TOKEN, token)
                .putString(KEY_USER_NAME, name)
                .putString(KEY_USER_EMAIL, email)
                .apply();
    }

    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public String getUserName() {
        return sharedPreferences.getString(KEY_USER_NAME, "");
    }

    public String getUserEmail() {
        return sharedPreferences.getString(KEY_USER_EMAIL, "");
    }

    public String getUserToken() {
        return sharedPreferences.getString(KEY_USER_TOKEN, "");
    }

    public void clearUserData() {
        sharedPreferences.edit().clear().apply();
    }

    // ===== FAVORITE MANAGEMENT =====

    /**
     * Thêm món ăn vào danh sách yêu thích
     */
    public void addToFavorites(String recipeId) {
        Set<String> favorites = new HashSet<>(getFavoriteIds());
        favorites.add(recipeId);
        sharedPreferences.edit()
                .putStringSet(KEY_FAVORITES, favorites)
                .apply();
    }

    /**
     * Xóa món ăn khỏi danh sách yêu thích
     */
    public void removeFromFavorites(String recipeId) {
        Set<String> favorites = new HashSet<>(getFavoriteIds());
        favorites.remove(recipeId);
        sharedPreferences.edit()
                .putStringSet(KEY_FAVORITES, favorites)
                .apply();
    }

    /**
     * Kiểm tra xem một món ăn có trong danh sách yêu thích không
     */
    public boolean isFavorite(String recipeId) {
        return getFavoriteIds().contains(recipeId);
    }

    /**
     * Lấy danh sách ID của tất cả các món yêu thích
     */
    public Set<String> getFavoriteIds() {
        return new HashSet<>(sharedPreferences.getStringSet(KEY_FAVORITES, new HashSet<>()));
    }

    /**
     * Xóa tất cả danh sách yêu thích
     */
    public void clearFavorites() {
        sharedPreferences.edit()
                .remove(KEY_FAVORITES)
                .apply();
    }
}
