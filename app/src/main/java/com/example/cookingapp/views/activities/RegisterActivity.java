package com.example.cookingapp.views.activities;

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
import com.google.firebase.auth.UserProfileChangeRequest;

public class RegisterActivity extends AppCompatActivity {

    private EditText edtName, edtEmail, edtPassword, edtConfirmPassword;
    private Button btnRegister;
    private PreferencesHelper preferencesHelper;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        preferencesHelper = new PreferencesHelper(this);

        edtName = findViewById(R.id.edt_reg_name);
        edtEmail = findViewById(R.id.edt_reg_email);
        edtPassword = findViewById(R.id.edt_reg_password);
        edtConfirmPassword = findViewById(R.id.edt_reg_confirm_password);
        btnRegister = findViewById(R.id.btn_register_submit);

        btnRegister.setOnClickListener(v -> {
            String name = edtName.getText().toString().trim();
            String email = edtEmail.getText().toString().trim();
            String password = edtPassword.getText().toString().trim();
            String confirmPassword = edtConfirmPassword.getText().toString().trim();

            if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || password.length() < 6) {
                Toast.makeText(this, "Vui long nhap ten, email va mat khau tu 6 ky tu", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!password.equals(confirmPassword)) {
                Toast.makeText(this, "Mat khau xac nhan khong khop", Toast.LENGTH_SHORT).show();
                return;
            }

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user == null) {
                                Toast.makeText(RegisterActivity.this, "Dang ky thanh cong!", Toast.LENGTH_SHORT).show();
                                finish();
                                return;
                            }

                            UserProfileChangeRequest profileUpdates =
                                    new UserProfileChangeRequest.Builder()
                                            .setDisplayName(name)
                                            .build();

                            user.updateProfile(profileUpdates)
                                    .addOnCompleteListener(updateTask -> {
                                        String token = user.getUid();
                                        preferencesHelper.saveUserData(token, name, email);

                                        if (updateTask.isSuccessful()) {
                                            Toast.makeText(RegisterActivity.this, "Dang ky thanh cong!", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(RegisterActivity.this, "Tai khoan da tao, nhung chua cap nhat ten", Toast.LENGTH_SHORT).show();
                                        }
                                        finish();
                                    });
                        } else {
                            String message = task.getException() != null
                                    ? task.getException().getMessage()
                                    : "Dang ky that bai";
                            Toast.makeText(RegisterActivity.this, "Loi: " + message, Toast.LENGTH_LONG).show();
                        }
                    });
        });

        TextView txtBackToLogin = findViewById(R.id.txt_back_to_login);
        txtBackToLogin.setOnClickListener(v -> finish());
    }
}