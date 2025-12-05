package com.group6.vietravel.admin.ui.notifications;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.group6.vietravel.R;
import com.group6.vietravel.admin.data.models.Notification;
import com.group6.vietravel.admin.data.repositories.AdminNotificationRepository;

public class SendNotificationActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_notification);

        EditText etTitle = findViewById(R.id.etTitle);
        EditText etMessage = findViewById(R.id.etMessage);
        Button btnSend = findViewById(R.id.btnSend);

        btnSend.setOnClickListener(v -> {
            String title = etTitle.getText().toString();
            String message = etMessage.getText().toString();

            if (TextUtils.isEmpty(title) || TextUtils.isEmpty(message)) {
                Toast.makeText(this, "Vui lòng nhập đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            Notification notif = new Notification(title, message, System.currentTimeMillis());
            AdminNotificationRepository.getInstance().sendNotification(notif, new AdminNotificationRepository.OnComplete() {
                @Override
                public void onSuccess() {
                    Toast.makeText(SendNotificationActivity.this, "Đã gửi!", Toast.LENGTH_SHORT).show();
                    finish();
                }
                @Override
                public void onFailure(String error) {
                    Toast.makeText(SendNotificationActivity.this, "Lỗi: " + error, Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}