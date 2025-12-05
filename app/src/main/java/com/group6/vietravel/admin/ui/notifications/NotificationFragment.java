package com.group6.vietravel.admin.ui.notifications;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog; // Import Dialog
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.group6.vietravel.R;
import com.group6.vietravel.admin.data.models.Notification;
import com.group6.vietravel.admin.data.repositories.AdminNotificationRepository;

public class NotificationFragment extends Fragment {

    private NotificationViewModel viewModel;
    private AdminNotificationAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_notification, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerNotifications);
        adapter = new AdminNotificationAdapter();

        // --- CÀI ĐẶT SỰ KIỆN XÓA ---
        adapter.setListener(new AdminNotificationAdapter.OnNotificationListener() {
            @Override
            public void onDeleteClick(Notification notification) {
                showDeleteConfirmDialog(notification);
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        viewModel = new ViewModelProvider(this).get(NotificationViewModel.class);
        viewModel.getNotifications().observe(getViewLifecycleOwner(), list -> {
            adapter.setList(list);
        });

        LinearLayout btnCreate = view.findViewById(R.id.btnCreateNew);
        btnCreate.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), SendNotificationActivity.class);
            startActivity(intent);
        });
    }

    // Hàm hiển thị hộp thoại xác nhận
    private void showDeleteConfirmDialog(Notification notification) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Xóa thông báo")
                .setMessage("Bạn có chắc chắn muốn xóa thông báo: \"" + notification.getTitle() + "\" không?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    // Gọi Repository để xóa
                    AdminNotificationRepository.getInstance().deleteNotification(notification.getId(), new AdminNotificationRepository.OnComplete() {
                        @Override
                        public void onSuccess() {
                            Toast.makeText(getContext(), "Đã xóa thành công!", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure(String error) {
                            Toast.makeText(getContext(), "Lỗi: " + error, Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
}