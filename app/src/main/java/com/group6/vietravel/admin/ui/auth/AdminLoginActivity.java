package com.group6.vietravel.admin.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.group6.vietravel.R;
import com.group6.vietravel.admin.ui.main.AdminMainActivity;
import com.group6.vietravel.admin.utils.AdminAuthUtils;
import com.group6.vietravel.ui.auth.AuthViewModel;

public class AdminLoginActivity extends AppCompatActivity {

    private AuthViewModel authViewModel;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private Button buttonAdminLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);

        editTextEmail = findViewById(R.id.editTextAdminEmail);
        editTextPassword = findViewById(R.id.editTextAdminPassword);
        buttonAdminLogin = findViewById(R.id.buttonAdminLogin);

        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        observeViewModel();

        buttonAdminLogin.setOnClickListener(v -> {
            String email = editTextEmail.getText().toString().trim();
            String password = editTextPassword.getText().toString().trim();

            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                Toast.makeText(this, "Vui lòng nhập đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            authViewModel.login(email, password);
        });
    }

    private void observeViewModel() {
        authViewModel.getErrorLiveData().observe(this, error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(this, "Lỗi: " + error, Toast.LENGTH_LONG).show();
            }
        });

        authViewModel.getUserLiveData().observe(this, firebaseUser -> {
            if (firebaseUser != null) {
                // Check if user is admin
                AdminAuthUtils.checkIsAdmin(isAdmin -> {
                    if (isAdmin) {
                        Toast.makeText(this, "Đăng nhập Admin thành công!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(AdminLoginActivity.this, AdminMainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(this, "Bạn không có quyền Admin!", Toast.LENGTH_LONG).show();
//                        authViewModel.logout();
                    }
                });
            }
        });
    }
}
