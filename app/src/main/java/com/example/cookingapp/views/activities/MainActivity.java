package com.example.cookingapp.views.activities;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.example.cookingapp.R;
import com.example.cookingapp.views.fragments.*; // Import tất cả fragment
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView nav = findViewById(R.id.bottom_nav);

        // 1. Mặc định mới mở app thì hiện HomeFragment
        loadFragment(new HomeFragment());

        // 2. Bắt sự kiện khi bấm vào các icon dưới đáy
        nav.setOnItemSelectedListener(item -> {
            Fragment f = null;
            if (item.getItemId() == R.id.it_home) f = new HomeFragment();
            if (item.getItemId() == R.id.it_search) f = new SearchFragment();
            if (item.getItemId() == R.id.it_fav) f = new FavoriteFragment();
            if (item.getItemId() == R.id.it_pro) f = f = new ProfileFragment();

            return loadFragment(f);
        });
    }

    // Hàm phụ để thay đổi nội dung Fragment (đỡ phải viết đi viết lại)
    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_layout, fragment)
                    .commit();
            return true;
        }
        return false;
    }
}