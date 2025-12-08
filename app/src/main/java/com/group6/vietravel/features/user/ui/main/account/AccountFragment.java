package com.group6.vietravel.features.user.ui.main.account;


import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.imageview.ShapeableImageView;
import com.group6.vietravel.R;
import com.group6.vietravel.data.models.notification.Notification;
import com.group6.vietravel.data.models.user.User;
import com.group6.vietravel.features.ui.auth.AuthViewModel;
import com.group6.vietravel.features.ui.auth.LoginActivity;
import com.group6.vietravel.features.user.ui.adapters.NotificationAdapter;

import com.group6.vietravel.features.user.ui.main.dialog.ChangePasswordBottomSheet;
import com.group6.vietravel.features.user.ui.main.dialog.EditProfileBottomSheet;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AccountFragment extends Fragment {

    private AccountViewModel accountViewModel;
    private AuthViewModel authViewModel;

    private NotificationAdapter adapter;
    private ShapeableImageView img_avatar;
    private TextView tv_username, tv_email, tv_join_date;
    private Chip chip_points, chip_role;
    private MaterialButton btn_edit_profile, btn_change_password, btn_logout;
    private RecyclerView rv_notifications;
    public static AccountFragment newInstance() {
        return new AccountFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_account, container, false);
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        accountViewModel = new ViewModelProvider(this).get(AccountViewModel.class);

        img_avatar = view.findViewById(R.id.img_avatar);
        tv_join_date = view.findViewById(R.id.tv_join_date);
        tv_email = view.findViewById(R.id.tv_email);
        tv_username = view.findViewById(R.id.tv_username);
        chip_role = view.findViewById(R.id.chip_role);
        chip_points = view.findViewById(R.id.chip_points);
        btn_logout = view.findViewById(R.id.btn_logout);
        btn_change_password = view.findViewById(R.id.btn_change_password);
        btn_edit_profile = view.findViewById(R.id.btn_edit_profile);
        rv_notifications = view.findViewById(R.id.rv_notifications);

        adapter = new NotificationAdapter(getContext(),new ArrayList<>());
        rv_notifications.setAdapter(adapter);
        rv_notifications.setLayoutManager(new LinearLayoutManager(getContext()));

        accountViewModel.getNotifications().observe(getViewLifecycleOwner(), new Observer<List<Notification>>() {
            @Override
            public void onChanged(List<Notification> notifications) {
                adapter.updateData(notifications);
            }
        });

        accountViewModel.getNotifications();

        accountViewModel.getUserProfile().observe(getViewLifecycleOwner(), new Observer<User>() {
            @Override
            public void onChanged(User user) {
                loadData(user);
            }
        });

        btn_edit_profile.setOnClickListener(v -> {
            EditProfileBottomSheet editDialog = new EditProfileBottomSheet();
            editDialog.show(getParentFragmentManager(), "EditProfileDialog");

            editDialog.setOnUpdateProfileListener(new EditProfileBottomSheet.OnUpdateProfileListener() {
                @Override
                public void onUpdateProfile(Uri uri, String username, String email) {
                    accountViewModel.setUserProfile(uri, username, email);
                }
            });
        });

        btn_change_password.setOnClickListener(v -> {
            ChangePasswordBottomSheet passDialog = new ChangePasswordBottomSheet();
            passDialog.show(getParentFragmentManager(), "ChangePassDialog");

            passDialog.setOnChangePasswordListener(new ChangePasswordBottomSheet.OnChangePasswordListener() {
                @Override
                public void onChangePassword(String currPassword, String newPassword) {
                    accountViewModel.changePass(currPassword, newPassword);
                }
            });

        });

        btn_logout.setOnClickListener(v->{
            accountViewModel.logout();
            Toast.makeText(getActivity(), "Đã đăng xuất", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(requireActivity(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

    }

    private void loadData(User user){
        String imageUrl = user.getAvatar_url();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(this)
                    .load(imageUrl)
                    .into(img_avatar);
        }

        tv_username.setText(user.getUsername());
        tv_email.setText(user.getEmail());

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String dateStr = (user.getCreated_at() != null) ? sdf.format(user.getCreated_at()) : "";
        tv_join_date.setText("Tham gia lúc: "+dateStr);

        chip_points.setText(String.valueOf(user.getPoints()));
        chip_role.setText(user.getRole());
    }

}