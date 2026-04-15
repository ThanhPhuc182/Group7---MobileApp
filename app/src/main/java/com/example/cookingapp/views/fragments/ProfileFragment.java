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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileFragment extends Fragment {

    private TextView tvUserName, tvUserEmail;
    private Button btnLogout;
    private PreferencesHelper preferencesHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        preferencesHelper = new PreferencesHelper(requireContext());
        tvUserName = view.findViewById(R.id.tv_user_name);
        tvUserEmail = view.findViewById(R.id.tv_user_email);
        btnLogout = view.findViewById(R.id.btn_logout);

        bindUserInfo();

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

    private void bindUserInfo() {
        String userName = preferencesHelper.getUserName();
        String userEmail = preferencesHelper.getUserEmail();

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String firebaseName = currentUser.getDisplayName();
            String firebaseEmail = currentUser.getEmail();

            if (!TextUtils.isEmpty(firebaseName)) {
                userName = firebaseName;
            }

            if (!TextUtils.isEmpty(firebaseEmail)) {
                userEmail = firebaseEmail;
            }

            if (isEmailPrefixName(userName, userEmail)) {
                userName = "";
            }

            preferencesHelper.saveUserData(
                    currentUser.getUid(),
                    TextUtils.isEmpty(userName) ? "Nguoi dung" : userName,
                    TextUtils.isEmpty(userEmail) ? "" : userEmail
            );
        }

        tvUserName.setText(TextUtils.isEmpty(userName) ? "Nguoi dung" : userName);
        tvUserEmail.setText(TextUtils.isEmpty(userEmail) ? "user@example.com" : userEmail);
    }

    private boolean isEmailPrefixName(String name, String email) {
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || !email.contains("@")) {
            return false;
        }
        String emailPrefix = email.substring(0, email.indexOf('@'));
        return name.equalsIgnoreCase(emailPrefix);
    }
}