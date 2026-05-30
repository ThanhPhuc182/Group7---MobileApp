package com.example.cookingapp.views.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cookingapp.R;
import com.example.cookingapp.utils.PreferencesHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private EditText edtEmail, edtPassword;
    private Button btnLogin;
    private PreferencesHelper preferencesHelper;
    private FirebaseAuth mAuth;

    @Override
    protected void onStart() {
        super.onStart();
        // Kiểm tra nếu user đã đăng nhập thì vào thẳng Main
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null && preferencesHelper.isLoggedIn()) {
            navigateToMain();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        preferencesHelper = new PreferencesHelper(this);

        edtEmail = findViewById(R.id.edt_email);
        edtPassword = findViewById(R.id.edt_password);
        btnLogin = findViewById(R.id.btn_login);

        btnLogin.setOnClickListener(v -> {
            String email = edtEmail.getText().toString().trim();
            String password = edtPassword.getText().toString().trim();

            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ email và mật khẩu!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Bắt đầu xác thực với Firebase
            btnLogin.setEnabled(false); // Tránh bấm nhiều lần
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        btnLogin.setEnabled(true);
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                String savedEmail = user.getEmail() != null ? user.getEmail() : email;
                                String savedName = resolveName(user, savedEmail);
                                String token = user.getUid();

                                preferencesHelper.saveUserData(token, savedName, savedEmail);

                                Toast.makeText(LoginActivity.this, "Chào mừng bạn quay lại!", Toast.LENGTH_SHORT).show();
                                navigateToMain();
                            }
                        } else {
                            String errorMsg = task.getException() != null ? task.getException().getMessage() : "Lỗi không xác định";
                            Log.e("LoginActivity", "Login failed: " + errorMsg);
                            Toast.makeText(LoginActivity.this, "Đăng nhập thất bại: " + errorMsg, Toast.LENGTH_LONG).show();
                        }
                    });
        });

        TextView txtRegister = findViewById(R.id.txt_register);
        txtRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        TextView txtForgotPassword = findViewById(R.id.txt_forgot_password);
        txtForgotPassword.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
        });
    }

    private void navigateToMain() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private String resolveName(FirebaseUser user, String email) {
        if (user != null && !TextUtils.isEmpty(user.getDisplayName())) {
            return user.getDisplayName();
        }

        String savedEmail = preferencesHelper.getUserEmail();
        String savedName = preferencesHelper.getUserName();
        if (!TextUtils.isEmpty(savedName)
                && !TextUtils.isEmpty(savedEmail)
                && savedEmail.equalsIgnoreCase(email)
                && !isEmailPrefixName(savedName, savedEmail)) {
            return savedName;
        }

        return "Người dùng";
    }

    private boolean isEmailPrefixName(String name, String email) {
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || !email.contains("@")) {
            return false;
        }
        String emailPrefix = email.substring(0, email.indexOf('@'));
        return name.equalsIgnoreCase(emailPrefix);
    }
}