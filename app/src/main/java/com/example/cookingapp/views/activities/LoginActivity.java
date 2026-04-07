package com.example.cookingapp.views.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.cookingapp.R;

public class LoginActivity extends AppCompatActivity {

    // Khai báo các biến tương ứng với ID bên XML
    private EditText edtEmail, edtPassword;
    private Button btnLogin;
    private TextView txtRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super. onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Ánh xạ (Kết nối Java với XML)
        initViews();

        // Tạm thời để trống các sự kiện Click cho bạn Backend
        btnLogin.setOnClickListener(v -> {
            // Logic đăng nhập sẽ viết ở đây
        });

        txtRegister.setOnClickListener(v -> {
            // Chuyển sang màn hình Đăng ký
        });
    }

    private void initViews() {
        edtEmail = findViewById(R.id.edt_email);
        edtPassword = findViewById(R.id.edt_password);
        btnLogin = findViewById(R.id.btn_login);
        txtRegister = findViewById(R.id.txt_register);
    }
}
