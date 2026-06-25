package com.example.cookingapp.views.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler; // ĐÃ THÊM
import android.os.Looper;  // ĐÃ THÊM
import android.text.Editable;
import android.text.TextUtils; // ĐÃ THÊM
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cookingapp.R;
import com.example.cookingapp.adapters.CategoryAdapter;
import com.example.cookingapp.adapters.RecipeAdapter;
import com.example.cookingapp.models.Recipe;
import com.example.cookingapp.utils.PreferencesHelper;
import com.example.cookingapp.views.activities.RecipeDetailActivity;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.Normalizer; // ĐÃ THÊM
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern; // ĐÃ THÊM

public class HomeFragment extends Fragment {

    private EditText edtHomeSearch;
    private RecyclerView rvRecipes, rvCategories;
    private RecipeAdapter recipeAdapter;
    private ProgressBar progressBar;

    private FirebaseFirestore db;
    private final List<Recipe> allRecipes = new ArrayList<>();
    private PreferencesHelper preferencesHelper;

    // KHAI BÁO BỘ ĐẾM CHỐNG LAG KHI GÕ TIẾNG VIỆT
    private final Handler searchHandler = new Handler(Looper.getMainLooper());
    private final Runnable searchRunnable = new Runnable() {
        @Override
        public void run() {
            performSearch();
        }
    };

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Khởi tạo Views
        edtHomeSearch = view.findViewById(R.id.edt_home_search);
        rvRecipes = view.findViewById(R.id.rv_recipes);
        rvCategories = view.findViewById(R.id.rv_categories);
        progressBar = view.findViewById(R.id.progress_bar);

        // Khởi tạo Firestore và PreferencesHelper
        db = FirebaseFirestore.getInstance();
        preferencesHelper = new PreferencesHelper(getContext());

        // Cấu hình LayoutManager
        rvRecipes.setLayoutManager(new LinearLayoutManager(getContext()));
        rvCategories.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        setupCategories();

        // Khởi tạo Adapter
        recipeAdapter = new RecipeAdapter(new ArrayList<>(), recipe -> {
            Intent intent = new Intent(getContext(), RecipeDetailActivity.class);
            intent.putExtra("recipe_data", recipe);
            startActivity(intent);
        });

        // Set favorite listener
        recipeAdapter.setFavoriteListener((recipe, isFavorite) -> {
            Toast.makeText(getContext(),
                    isFavorite ? "Đã thêm vào yêu thích" : "Đã xóa khỏi yêu thích",
                    Toast.LENGTH_SHORT).show();
        }, preferencesHelper);

        rvRecipes.setAdapter(recipeAdapter);

        // Tải dữ liệu thật từ Firestore
        loadRecipesFromFirestore();

        // SỬA LẠI SỰ KIỆN GÕ CHỮ CHO ĐÚNG
        edtHomeSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Hủy lệnh tìm kiếm cũ nếu người dùng đang gõ liên tục
                searchHandler.removeCallbacks(searchRunnable);
                // Đợi 300ms sau khi người dùng dừng tay mới gọi hàm performSearch()
                searchHandler.postDelayed(searchRunnable, 300);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        return view;
    }

    private void setupCategories() {
        List<String> categories = new ArrayList<>();
        categories.add("Tất cả");
        categories.add("Món chay");
        categories.add("Ăn sáng");
        categories.add("Món mặn");
        categories.add("Tráng miệng");
        rvCategories.setAdapter(new CategoryAdapter(categories));
    }

    private void loadRecipesFromFirestore() {
        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);

        db.collection("recipes")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (progressBar != null) progressBar.setVisibility(View.GONE);

                    allRecipes.clear();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Recipe recipe = document.toObject(Recipe.class);
                        recipe.setId(document.getId());
                        allRecipes.add(recipe);
                    }

                    recipeAdapter.setData(new ArrayList<>(allRecipes));
                })
                .addOnFailureListener(e -> {
                    if (progressBar != null) progressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Lỗi tải dữ liệu: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("FirestoreError", e.getMessage());
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
        if (edtHomeSearch == null) return;

        String originalQuery = edtHomeSearch.getText().toString().trim().toLowerCase();

        if (TextUtils.isEmpty(originalQuery)) {
            recipeAdapter.setData(new ArrayList<>(allRecipes));
            return;
        }

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

            if (matchesName) {
                filteredRecipes.add(recipe);
            }
        }

        recipeAdapter.setData(filteredRecipes);
    }
}