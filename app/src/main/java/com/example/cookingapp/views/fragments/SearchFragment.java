package com.example.cookingapp.views.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cookingapp.R;
import com.example.cookingapp.adapters.RecipeAdapter;
import com.example.cookingapp.models.Recipe;
import com.example.cookingapp.utils.PreferencesHelper;
import com.example.cookingapp.views.activities.RecipeDetailActivity;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import java.text.Normalizer;
import java.util.regex.Pattern;

public class SearchFragment extends Fragment {

    private EditText edtSearchQuery;
    private RecyclerView rvSearchResults;
    private RecipeAdapter recipeAdapter;
    private ProgressBar progressBar;

    private ChipGroup chipGroupIngredients;
    private final List<String> selectedIngredients = new ArrayList<>();

    private FirebaseFirestore db;
    private PreferencesHelper preferencesHelper;
    private final List<Recipe> allRecipes = new ArrayList<>();

    // Debounce search to fix Vietnamese typing issue (text disappearing)
    private final Handler searchHandler = new Handler(Looper.getMainLooper());
    private final Runnable searchRunnable = this::performSearch;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        edtSearchQuery = view.findViewById(R.id.edt_search_query);
        rvSearchResults = view.findViewById(R.id.rv_search_results);
        progressBar = view.findViewById(R.id.pb_search);
        chipGroupIngredients = view.findViewById(R.id.chip_group_ingredients);

        rvSearchResults.setLayoutManager(new LinearLayoutManager(getContext()));
        recipeAdapter = new RecipeAdapter(new ArrayList<>(), recipe -> {
            Intent intent = new Intent(getContext(), RecipeDetailActivity.class);
            intent.putExtra("recipe_data", recipe);
            startActivity(intent);
        });

        // Khởi tạo Firestore và PreferencesHelper
        db = FirebaseFirestore.getInstance();
        preferencesHelper = new PreferencesHelper(getContext());

        // Set favorite listener
        recipeAdapter.setFavoriteListener((recipe, isFavorite) -> {
            Toast.makeText(getContext(),
                isFavorite ? "Đã thêm vào yêu thích" : "Đã xóa khỏi yêu thích",
                Toast.LENGTH_SHORT).show();
        }, preferencesHelper);

        rvSearchResults.setAdapter(recipeAdapter);
        fetchAllRecipesFromFirestore();

        setupSearchInput();
        setupSmartFridge();

        return view;
    }

    private void fetchAllRecipesFromFirestore() {
        progressBar.setVisibility(View.VISIBLE);
        db.collection("recipes")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    progressBar.setVisibility(View.GONE);
                    allRecipes.clear();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Recipe recipe = document.toObject(Recipe.class);
                        recipe.setId(document.getId());
                        allRecipes.add(recipe);
                    }
                    recipeAdapter.setData(new ArrayList<>(allRecipes));
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Lỗi tải dữ liệu", Toast.LENGTH_SHORT).show();
                });
    }

    private void setupSmartFridge() {
        for (int i = 0; i < chipGroupIngredients.getChildCount(); i++) {
            View child = chipGroupIngredients.getChildAt(i);
            if (child instanceof Chip) {
                Chip chip = (Chip) child;
                chip.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    String ingredient = buttonView.getText().toString().toLowerCase();
                    if (isChecked) {
                        selectedIngredients.add(ingredient);
                    } else {
                        selectedIngredients.remove(ingredient);
                    }
                    performSearch();
                });
            }
        }
    }

    private void setupSearchInput() {
        // Thêm TextWatcher để tìm kiếm ngay khi gõ, dùng Handler để tránh lỗi gõ tiếng Việt (Telex/VNI)
        edtSearchQuery.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchHandler.removeCallbacks(searchRunnable);
                searchHandler.postDelayed(searchRunnable, 300); // Đợi 300ms sau khi ngừng gõ mới tìm
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        edtSearchQuery.setOnEditorActionListener((TextView v, int actionId, KeyEvent event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                    (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                searchHandler.removeCallbacks(searchRunnable);
                performSearch();
                hideKeyboard(v);
                return true;
            }
            return false;
        });
    }

    public String removeAccent(String s) {
        if (s == null) return "";
        String temp = Normalizer.normalize(s, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(temp).replaceAll("").replace('đ', 'd').replace('Đ', 'D');
    }

    private String normalizeSynonyms(String input) {
        if (input == null) return "";
        String output = input.toLowerCase();

        // 1. Nhóm Thịt
        output = output.replaceAll("\\blon\\b", "heo");
        output = output.replaceAll("\\bba roi\\b", "ba chi");

        // 2. Nhóm Rau củ & Quả
        output = output.replaceAll("\\bmuop dang\\b", "kho qua");
        output = output.replaceAll("\\bsup lo\\b", "bong cai");
        output = output.replaceAll("\\bdau co ve\\b", "dau que");
        output = output.replaceAll("\\bcove\\b", "dau que");
        output = output.replaceAll("\\bcai chip\\b", "cai thia");

        // 3. Nhóm Thơm / Dứa / Khóm (Do DB của bạn có cả "Thơm" và "Dứa", ta quy hết về "dua")
        output = output.replaceAll("\\bthom\\b", "dua");
        output = output.replaceAll("\\bkhom\\b", "dua");

        // 4. Nhóm Đậu phụ & Đồ gia vị
        output = output.replaceAll("\\bdau phu\\b", "dau hu");
        output = output.replaceAll("\\bdau phong\\b", "lac");
        output = output.replaceAll("\\bnuoc tuong\\b", "xi dau");

        // 5. Nhóm Cá
        output = output.replaceAll("\\bca qua\\b", "ca loc");
        output = output.replaceAll("\\bca trau\\b", "ca loc");

        return output;
    }
    //  Thuật toán Levenshtein tính độ lệch ký tự
    private int computeLevenshteinDistance(String x, String y) {
        int[][] dp = new int[x.length() + 1][y.length() + 1];
        for (int i = 0; i <= x.length(); i++) {
            for (int j = 0; j <= y.length(); j++) {
                if (i == 0) {
                    dp[i][j] = j;
                } else if (j == 0) {
                    dp[i][j] = i;
                } else {
                    dp[i][j] = Math.min(Math.min(
                                    dp[i - 1][j] + 1,
                                    dp[i][j - 1] + 1),
                            dp[i - 1][j - 1] + (x.charAt(i - 1) == y.charAt(j - 1) ? 0 : 1)
                    );
                }
            }
        }
        return dp[x.length()][y.length()];
    }

    // Hàm kiểm tra khớp từ
    private boolean isFuzzyMatch(String queryWord, String recipeWord) {
        if (recipeWord.startsWith(queryWord)) {
            return true;
        }

        int len = queryWord.length();
        if (len <= 3) return false;

        int distance = computeLevenshteinDistance(queryWord, recipeWord);

        if (len <= 5) return distance <= 1;
        return distance <= 2;
    }

    private void performSearch() {
        if (edtSearchQuery == null) return;

        String originalQuery = edtSearchQuery.getText().toString().trim().toLowerCase();

        String queryNoAccent = removeAccent(originalQuery);
        String normalizedQuery = normalizeSynonyms(queryNoAccent);

        String[] queryWords = normalizedQuery.split("\\s+");

        List<Recipe> filteredRecipes = new ArrayList<>();

        for (Recipe recipe : allRecipes) {
            String name = recipe.getName();
            if (name == null) continue;

            String recipeNameNoAccent = removeAccent(name.toLowerCase());
            String normalizedRecipeName = normalizeSynonyms(recipeNameNoAccent);

            String[] recipeWords = normalizedRecipeName.split("\\s+");

            boolean matchesName = true;

            if (!TextUtils.isEmpty(originalQuery)) {

                for (String qWord : queryWords) {
                    boolean foundMatchForThisWord = false;

                    for (String rWord : recipeWords) {
                        if (isFuzzyMatch(qWord, rWord)) {
                            foundMatchForThisWord = true;
                            break;
                        }
                    }

                    if (!foundMatchForThisWord) {
                        matchesName = false;
                        break;
                    }
                }
            }

            // SMART FRIDGE
            boolean matchesIngredients = true;
            int currentScore = 0;

            if (!selectedIngredients.isEmpty()) {
                List<String> recipeIngredients = recipe.getIngredients();
                if (recipeIngredients != null) {
                    for (String selected : selectedIngredients) {
                        String selectedNoAccent = normalizeSynonyms(removeAccent(selected));
                        for (String dbIng : recipeIngredients) {
                            if (dbIng != null && normalizeSynonyms(removeAccent(dbIng.toLowerCase())).contains(selectedNoAccent)) {
                                currentScore++;
                                break;
                            }
                        }
                    }
                }
                matchesIngredients = (currentScore > 0);
                recipe.setMatchScore(currentScore);
            }

            if (matchesName && matchesIngredients) {
                filteredRecipes.add(recipe);
            }
        }

        if (!selectedIngredients.isEmpty()) {
            Collections.sort(filteredRecipes, (r1, r2) -> r2.getMatchScore() - r1.getMatchScore());
        }

        recipeAdapter.setData(filteredRecipes);
    }

    private void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}