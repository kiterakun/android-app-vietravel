package com.group6.vietravel.admin.ui.users;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.group6.vietravel.R;

/**
 * NGƯỜI 2: QUẢN LÝ NGƯỜI DÙNG (ADF03)
 * 
 * TODO List:
 * 1. Tạo UserManagementViewModel
 * 2. Tạo layout fragment_user_management.xml với:
 *    - SearchView (tìm theo email/username)
 *    - Filter chips (All, Active, Locked, Hidden)
 *    - RecyclerView
 * 3. Tạo AdminUserAdapter với:
 *    - Display: avatar, username, email, role, status, points
 *    - Status badge với màu sắc khác nhau
 *    - Action button (3 dots menu)
 * 4. Tạo layout item_admin_user.xml
 * 5. Tạo dialog_user_actions.xml với các options:
 *    - View Details
 *    - Lock/Unlock
 *    - Hide/Restore
 *    - Update Points
 *    - Delete
 * 
 * Chức năng cần implement:
 * - Hiển thị danh sách users
 * - Tìm kiếm theo email/username
 * - Filter theo status (All/Active/Locked/Hidden)
 * - Click item để xem chi tiết user (stats, reviews, check-ins)
 * - Long click hoặc menu button để hiện actions
 * - Lock/Unlock account
 * - Hide/Restore account
 * - Update points manually
 * - Delete user (có confirm dialog)
 * 
 * Code tham khảo:
 * - app/src/main/java/com/group6/vietravel/ui/main/journey/JourneyFragment.java (user profile)
 * - app/src/main/java/com/group6/vietravel/data/models/User.java
 */
public class UserManagementFragment extends Fragment {

    private UserManagementViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_placeholder, container, false);
        
        TextView textView = view.findViewById(R.id.text_placeholder);
        textView.setText("User Management Fragment\n\nNGƯỜI 2: Implement quản lý user ở đây");
        
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        viewModel = new ViewModelProvider(this).get(UserManagementViewModel.class);
        
        // TODO: Initialize views
        // TODO: Setup RecyclerView with adapter
        // TODO: Setup SearchView
        // TODO: Setup filter chips
        // TODO: Observe ViewModel LiveData
    }
}
