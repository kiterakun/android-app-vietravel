package com.group6.vietravel.admin.ui.notifications;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.group6.vietravel.R;

/**
 * NGƯỜI 3: THÔNG BÁO (ADF04)
 * 
 * TODO List:
 * 1. Tạo NotificationViewModel
 * 2. Tạo layout fragment_notification.xml với:
 *    - FloatingActionButton để gửi thông báo mới
 *    - RecyclerView để hiển thị lịch sử
 * 3. Tạo AdminNotificationAdapter
 * 4. Tạo layout item_admin_notification.xml với:
 *    - Title, message preview
 *    - Target type (all/specific)
 *    - Sent date
 *    - Delete button
 * 5. Tạo SendNotificationActivity với form:
 *    - EditText: Title
 *    - EditText: Message (multiline)
 *    - RadioGroup: Send to (All users / Specific users)
 *    - EditText: User IDs (nếu chọn specific)
 *    - Button: Send
 * 6. Tạo layout activity_send_notification.xml
 * 
 * Chức năng cần implement:
 * - Hiển thị lịch sử thông báo đã gửi
 * - FAB click để mở SendNotificationActivity
 * - Form gửi thông báo với validation
 * - Chọn target: all users hoặc specific users
 * - Gửi thông báo (lưu vào Firestore)
 * - TODO Optional: Integrate FCM để push notification thật
 * 
 * Code tham khảo:
 * - app/src/main/java/com/group6/vietravel/ui/detail/DetailActivity.java (form inputs)
 */
public class NotificationFragment extends Fragment {

    private NotificationViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_placeholder, container, false);
        
        TextView textView = view.findViewById(R.id.text_placeholder);
        textView.setText("Notification Fragment\n\nNGƯỜI 3: Implement gửi thông báo ở đây");
        
        // TODO: Thay bằng layout thật và uncomment code bên dưới
        /*
        FloatingActionButton fab = view.findViewById(R.id.fab_send_notification);
        fab.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), SendNotificationActivity.class);
            startActivity(intent);
        });
        */
        
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        viewModel = new ViewModelProvider(this).get(NotificationViewModel.class);
        
        // TODO: Initialize views
        // TODO: Setup RecyclerView with adapter
        // TODO: Setup FAB click listener
        // TODO: Observe ViewModel LiveData
    }
}
