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
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    // Khai báo các biến tương ứng với ID bên XML
    private EditText edtEmail, edtPassword;
    private Button btnLogin;
    private TextView txtRegister;
    private PreferencesHelper preferencesHelper;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        edtEmail = findViewById(R.id.edt_email); // Huy nhớ check ID trong XML cho đúng nhé
        edtPassword = findViewById(R.id.edt_password);
        btnLogin = findViewById(R.id.btn_login);

        btnLogin.setOnClickListener(v -> {
            String email = edtEmail.getText().toString().trim();
            String password = edtPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Bắt đầu xác thực với Firebase
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            // CHỈ KHI NÀO ĐÚNG: Mới được chuyển màn hình
                            Toast.makeText(LoginActivity.this, "Chào mừng bạn quay lại!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            // NẾU SAI: Tuyệt đối không được chuyển màn hình
                            Toast.makeText(LoginActivity.this, "Tài khoản hoặc mật khẩu không đúng", Toast.LENGTH_LONG).show();
                        }
                    });
        });
        TextView txtRegister = findViewById(R.id.txt_register);

        // 2. Thiết lập sự kiện click để chuyển màn hình
        txtRegister.setOnClickListener(v -> {
            // Tạo một Intent để đi từ LoginActivity sang RegisterActivity
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);

            // Lưu ý: KHÔNG gọi finish() ở đây vì mình muốn
            // người dùng có thể bấm "Back" quay lại màn hình Login.
        });
    }
}

