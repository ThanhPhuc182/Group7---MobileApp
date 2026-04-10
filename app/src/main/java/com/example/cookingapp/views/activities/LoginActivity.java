package com.example.cookingapp.views.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.cookingapp.R;
import com.example.cookingapp.utils.PreferencesHelper;
import com.example.cookingapp.utils.ValidationUtil;

public class LoginActivity extends AppCompatActivity {

    // Khai báo các biến tương ứng với ID bên XML
    private EditText edtEmail, edtPassword;
    private Button btnLogin;
    private TextView txtRegister;
    private PreferencesHelper preferencesHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        preferencesHelper = new PreferencesHelper(this);
        if (preferencesHelper.isLoggedIn()) {
            openMain();
            return;
        }

        // Ánh xạ (Kết nối Java với XML)
        initViews();

        // Thiết lập các sự kiện Click
        setupListeners();
    }

    private void initViews() {
        edtEmail = findViewById(R.id.edt_email);
        edtPassword = findViewById(R.id.edt_password);
        btnLogin = findViewById(R.id.btn_login);
        txtRegister = findViewById(R.id.txt_register);
    }

    private void setupListeners() {
        btnLogin.setOnClickListener(v -> handleLogin());
        txtRegister.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, RegisterActivity.class)));
    }

    private void handleLogin() {
        String email = edtEmail.getText() != null ? edtEmail.getText().toString().trim() : "";
        String password = edtPassword.getText() != null ? edtPassword.getText().toString().trim() : "";

        edtEmail.setError(null);
        edtPassword.setError(null);

        if (!ValidationUtil.isValidEmail(email)) {
            edtEmail.setError("Email không hợp lệ");
            return;
        }
        if (!ValidationUtil.isValidPassword(password)) {
            edtPassword.setError("Mật khẩu phải có ít nhất 6 ký tự");
            return;
        }

        String token = "fake_token_" + System.currentTimeMillis();
        String userName = email.contains("@") ? email.substring(0, email.indexOf('@')) : "Người dùng";
        preferencesHelper.saveUserData(token, userName, email);

        Toast.makeText(this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
        openMain();
    }

    private void openMain() {
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
        finish();
    }
}
