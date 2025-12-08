package com.group6.vietravel.features.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.auth.FirebaseAuth; // Cần import để signout nếu bị khóa
import com.group6.vietravel.R;
import com.group6.vietravel.admin.ui.main.AdminMainActivity;
import com.group6.vietravel.features.user.ui.main.MainActivity;

public class LoginActivity extends AppCompatActivity {

    private AuthViewModel authViewModel;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private Button buttonLogin;
    private TextView toRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // ... (Giữ nguyên phần ánh xạ View và setOnClickListener) ...
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
        toRegister = findViewById(R.id.toRegister);

        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        observeViewModel();

        buttonLogin.setOnClickListener(v -> {
            String email = editTextEmail.getText().toString().trim();
            String password = editTextPassword.getText().toString().trim();

            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                Toast.makeText(LoginActivity.this, "Vui lòng nhập đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }
            authViewModel.login(email, password);
        });

        toRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void observeViewModel() {
        // 1. Lắng nghe lỗi
        authViewModel.getErrorLiveData().observe(this, error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(this, "Lỗi: " + error, Toast.LENGTH_LONG).show();
            }
        });

        // 2. Lắng nghe Auth thành công
        authViewModel.getUserLiveData().observe(this, firebaseUser -> {
            if (firebaseUser != null) {
                // Đăng nhập Firebase thành công -> Gọi check role và status
                Toast.makeText(this, "Đang kiểm tra trạng thái tài khoản...", Toast.LENGTH_SHORT).show();
                authViewModel.checkUserRole(firebaseUser.getUid());
            }
        });

        // 3. XỬ LÝ PHÂN QUYỀN VÀ KHÓA TÀI KHOẢN (QUAN TRỌNG)
        authViewModel.getRoleLiveData().observe(this, role -> {
            if (role != null) {
                // Kiểm tra nếu bị khóa
                if ("LOCKED".equals(role)) {
                    Toast.makeText(this, "Tài khoản của bạn đã bị khóa! Vui lòng liên hệ Admin.", Toast.LENGTH_LONG).show();

                    // Đăng xuất ngay lập tức để người dùng không kẹt ở trạng thái "đã login"
                    FirebaseAuth.getInstance().signOut();
                    return; // Dừng lại, không chuyển màn hình
                }

                Intent intent;
                if ("admin".equals(role)) {
                    intent = new Intent(LoginActivity.this, AdminMainActivity.class);
                    Toast.makeText(this, "Xin chào Admin!", Toast.LENGTH_SHORT).show();
                } else {
                    intent = new Intent(LoginActivity.this, MainActivity.class);
                    Toast.makeText(this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
                }

                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });
    }
}