package com.example.cookingapp.views.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.cookingapp.R;

public class RegisterActivity extends AppCompatActivity {

    private EditText edtName, edtEmail, edtPassword, edtConfirmPassword;
    private Button btnRegister;
    private TextView txtBackLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initViews();

        btnRegister.setOnClickListener(v -> {
            // TODO: [Bạn Backend] Lấy dữ liệu từ 4 ô EditText
            // TODO: [Bạn Backend] Kiểm tra xem Password và ConfirmPassword có khớp nhau không
            // TODO: [Bạn Backend] Gọi Firebase createUserWithEmailAndPassword
        });

        txtBackLogin.setOnClickListener(v -> {
            finish(); // Đóng màn hình đăng ký để quay lại màn hình trước đó (Login)
        });
    }

    private void initViews() {
        edtName = findViewById(R.id.edt_reg_name);
        edtEmail = findViewById(R.id.edt_reg_email);
        edtPassword = findViewById(R.id.edt_reg_password);
        edtConfirmPassword = findViewById(R.id.edt_reg_confirm_password);
        btnRegister = findViewById(R.id.btn_register_submit);
        txtBackLogin = findViewById(R.id.txt_back_to_login);
    }
}