package com.example.cookingapp.views.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Khởi tạo Firebase và Preferences
        mAuth = FirebaseAuth.getInstance();
        preferencesHelper = new PreferencesHelper(this);

        // Kiểm tra chuyển hướng ngay lập tức nếu đã login
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null && preferencesHelper.isLoggedIn()) {
            preferencesHelper.syncUserFromFirebase();
            navigateToMain();
            return;
        }

        // Đã xóa try-catch để nếu có lỗi layout sẽ báo trực tiếp trong Logcat
        setContentView(R.layout.activity_login);

        edtEmail = findViewById(R.id.edt_email);
        edtPassword = findViewById(R.id.edt_password);
        btnLogin = findViewById(R.id.btn_login);

        if (btnLogin != null) {
            btnLogin.setOnClickListener(v -> {
                String email = edtEmail.getText().toString().trim();
                String password = edtPassword.getText().toString().trim();

                if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                    Toast.makeText(this, "Vui lòng nhập đầy đủ email và mật khẩu!", Toast.LENGTH_SHORT).show();
                    return;
                }

                btnLogin.setEnabled(false);
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, task -> {
                            btnLogin.setEnabled(true);
                            if (task.isSuccessful()) {
                                FirebaseUser user = mAuth.getCurrentUser();
                                if (user != null) {
                                    preferencesHelper.syncUserFromFirebase();
                                    String savedEmail = user.getEmail() != null ? user.getEmail() : email;
                                    String savedName = resolveName(user, savedEmail);
                                    String token = user.getUid();
                                    preferencesHelper.saveUserData(token, savedName, savedEmail);
                                    navigateToMain();
                                }
                            } else {
                                String errorMsg = task.getException() != null ? task.getException().getMessage() : "Lỗi không xác định";
                                Toast.makeText(LoginActivity.this, "Đăng nhập thất bại: " + errorMsg, Toast.LENGTH_LONG).show();
                            }
                        });
            });
        }

        TextView txtRegister = findViewById(R.id.txt_register);
        if (txtRegister != null) {
            txtRegister.setOnClickListener(v -> {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            });
        }

        TextView txtForgotPassword = findViewById(R.id.txt_forgot_password);
        if (txtForgotPassword != null) {
            txtForgotPassword.setOnClickListener(v -> {
                Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                startActivity(intent);
            });
        }
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
        return "Người dùng";
    }
}