package com.example.cookingapp.views.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cookingapp.R;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText edtForgotEmail;
    private Button btnSendReset;
    private Button btnBackLogin;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        mAuth = FirebaseAuth.getInstance();

        edtForgotEmail = findViewById(R.id.edt_forgot_email);
        btnSendReset = findViewById(R.id.btn_send_reset);
        btnBackLogin = findViewById(R.id.btn_back_login);

        btnSendReset.setOnClickListener(v -> {
            String email = edtForgotEmail.getText().toString().trim();

            if (TextUtils.isEmpty(email)) {
                Toast.makeText(this, "Vui lòng nhập email!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Gửi email đặt lại mật khẩu
            sendPasswordResetEmail(email);
        });

        btnBackLogin.setOnClickListener(v -> {
            finish();
        });
    }

    private void sendPasswordResetEmail(String email) {
        // Hiển thị một ProgressDialog để tránh người dùng bấm lại
        btnSendReset.setEnabled(false);
        btnSendReset.setText("Đang gửi...");

        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(ForgotPasswordActivity.this,
                                R.string.reset_email_sent,
                                Toast.LENGTH_LONG).show();

                        // Xóa email vừa nhập
                        edtForgotEmail.setText("");

                        // Quay lại màn hình Login sau 2 giây
                        new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> {
                            finish();
                        }, 2000);
                    } else {
                        String errorMessage = task.getException() != null
                                ? task.getException().getMessage()
                                : "Không tìm thấy email này!";

                        Toast.makeText(ForgotPasswordActivity.this,
                                errorMessage,
                                Toast.LENGTH_LONG).show();
                    }

                    // Kích hoạt lại button
                    btnSendReset.setEnabled(true);
                    btnSendReset.setText(R.string.btn_send_reset_link);
                });
    }
}

