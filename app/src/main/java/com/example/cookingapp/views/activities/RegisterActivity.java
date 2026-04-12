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

public class RegisterActivity extends AppCompatActivity {

    private EditText edtName, edtEmail, edtPassword, edtConfirmPassword;
    private Button btnRegister;
    private TextView txtBackLogin;
    private PreferencesHelper preferencesHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        preferencesHelper = new PreferencesHelper(this);
        initViews();
        setupListeners();
    }

    private void initViews() {
        edtName = findViewById(R.id.edt_reg_name);
        edtEmail = findViewById(R.id.edt_reg_email);
        edtPassword = findViewById(R.id.edt_reg_password);
        edtConfirmPassword = findViewById(R.id.edt_reg_confirm_password);
        btnRegister = findViewById(R.id.btn_register_submit);
        txtBackLogin = findViewById(R.id.txt_back_to_login);
    }

    private void setupListeners() {
        btnRegister.setOnClickListener(v -> handleRegister());
        txtBackLogin.setOnClickListener(v -> finish());
    }

    private void handleRegister() {
        String name = edtName.getText() != null ? edtName.getText().toString().trim() : "";
        String email = edtEmail.getText() != null ? edtEmail.getText().toString().trim() : "";
        String password = edtPassword.getText() != null ? edtPassword.getText().toString().trim() : "";
        String confirmPassword = edtConfirmPassword.getText() != null ? edtConfirmPassword.getText().toString().trim() : "";

        edtName.setError(null);
        edtEmail.setError(null);
        edtPassword.setError(null);
        edtConfirmPassword.setError(null);

        if (!ValidationUtil.isValidName(name)) {
            edtName.setError("Tên phải có ít nhất 3 ký tự");
            return;
        }
        if (!ValidationUtil.isValidEmail(email)) {
            edtEmail.setError("Email không hợp lệ");
            return;
        }
        if (!ValidationUtil.isValidPassword(password)) {
            edtPassword.setError("Mật khẩu phải có ít nhất 6 ký tự");
            return;
        }
        if (!password.equals(confirmPassword)) {
            edtConfirmPassword.setError("Mật khẩu không khớp");
            return;
        }

        String token = "fake_token_" + System.currentTimeMillis();
        preferencesHelper.saveUserData(token, name, email);

        Toast.makeText(this, "Đăng ký thành công", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(RegisterActivity.this, MainActivity.class));
        finish();
    }
}