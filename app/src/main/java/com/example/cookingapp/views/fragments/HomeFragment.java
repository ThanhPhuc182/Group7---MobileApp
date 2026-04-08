package com.example.cookingapp.views.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cookingapp.R;
import com.example.cookingapp.adapters.CategoryAdapter;
import com.example.cookingapp.adapters.RecipeAdapter;
import com.example.cookingapp.models.Recipe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HomeFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // 1. Kết nối với file giao diện fragment_home.xml
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // 2. Ánh xạ RecyclerView từ XML
        RecyclerView rv = view.findViewById(R.id.rv_recipes);

        // 3. Thiết lập cách hiển thị (Dạng danh sách dọc)
        rv.setLayoutManager(new LinearLayoutManager(getContext()));

        // 4. Tạo dữ liệu giả để test
        // 1. Ánh xạ RecyclerView danh mục
        RecyclerView rvCategories = view.findViewById(R.id.rv_categories);

// 2. Thiết lập cuộn ngang (Horizontal)
        rvCategories.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

// 3. Tạo dữ liệu giả
        List<String> categories = new ArrayList<>();
        categories.add("Tất cả");
        categories.add("Món chay");
        categories.add("Ăn sáng");
        categories.add("Món mặn");
        categories.add("Tráng miệng");

// 4. Đổ dữ liệu vào Adapter
        CategoryAdapter categoryAdapter = new CategoryAdapter(categories);
        rvCategories.setAdapter(categoryAdapter);

        List<Recipe> list = new ArrayList<>();
        list.add(new Recipe("Sườn xào chua ngọt", "", "45 phút"));
        list.add(new Recipe("Phở bò Hà Nội", "", "120 phút"));
        list.add(new Recipe("Gỏi cuốn tôm thịt", "", "30 phút"));

        // 5. Kết nối Adapter với RecyclerView
        RecipeAdapter adapter = new RecipeAdapter(list);
        rv.setAdapter(adapter);

        return view;
    }
}