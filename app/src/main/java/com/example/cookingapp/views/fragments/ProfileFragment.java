package com.example.cookingapp.views.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.cookingapp.R;
import com.example.cookingapp.utils.PreferencesHelper;
import com.example.cookingapp.views.activities.LoginActivity;
import com.example.cookingapp.views.activities.MealPlannerActivity;
import com.google.firebase.auth.FirebaseAuth;

public class ProfileFragment extends Fragment {

    private TextView tvUserName, tvUserEmail;
    private Button btnLogout, btnMealPlanner;
    private PreferencesHelper preferencesHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        preferencesHelper = new PreferencesHelper(requireContext());
        tvUserName = view.findViewById(R.id.tv_user_name);
        tvUserEmail = view.findViewById(R.id.tv_user_email);
        btnMealPlanner = view.findViewById(R.id.btn_meal_planner);
        btnLogout = view.findViewById(R.id.btn_logout);

        bindUserInfo();

        btnMealPlanner.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), MealPlannerActivity.class);
            startActivity(intent);
        });

        btnLogout.setOnClickListener(v -> {
            preferencesHelper.clearUserData();
            FirebaseAuth.getInstance().signOut();

            Intent intent = new Intent(requireContext(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            requireActivity().finish();
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        bindUserInfo();
    }

    private void bindUserInfo() {
        preferencesHelper.syncUserFromFirebase();

        String userName = preferencesHelper.getUserName();
        String userEmail = preferencesHelper.getUserEmail();

        tvUserName.setText(TextUtils.isEmpty(userName) ? "Người dùng" : userName);
        tvUserEmail.setText(TextUtils.isEmpty(userEmail) ? "user@example.com" : userEmail);
    }
}