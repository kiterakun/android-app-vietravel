package com.group6.vietravel.admin.ui.users;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.chip.ChipGroup;
import com.group6.vietravel.R;
import com.group6.vietravel.data.models.user.User;

public class UserManagementFragment extends Fragment implements AdminUserAdapter.OnUserActionListener {

    private UserManagementViewModel viewModel;
    private AdminUserAdapter adapter;
    private EditText etSearch;
    private ChipGroup chipGroupFilter;
    private RecyclerView rvUsers;
    private TextView tvEmpty;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user_management, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(UserManagementViewModel.class);

        initViews(view);
        setupRecyclerView();
        setupListeners();
        setupObservers();

        viewModel.loadAllUsers();
    }

    private void initViews(View view) {
        etSearch = view.findViewById(R.id.etSearch);
        chipGroupFilter = view.findViewById(R.id.chipGroupFilter);
        rvUsers = view.findViewById(R.id.rvUsers);
        tvEmpty = view.findViewById(R.id.tvEmpty);
    }

    private void setupRecyclerView() {
        adapter = new AdminUserAdapter(this);
        rvUsers.setLayoutManager(new LinearLayoutManager(getContext()));
        rvUsers.setAdapter(adapter);
    }

    private void setupListeners() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                viewModel.searchUsers(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        chipGroupFilter.setOnCheckedChangeListener((group, checkedId) -> {
            String filter = "All";
            if (checkedId == R.id.chipActive) {
                filter = "Active";
            } else if (checkedId == R.id.chipLocked) {
                filter = "Locked";
            } else if (checkedId == R.id.chipHidden) {
                filter = "Hidden";
            }
            viewModel.filterUsers(filter);
        });
    }

    private void setupObservers() {
        viewModel.getUsers().observe(getViewLifecycleOwner(), users -> {
            adapter.setUsers(users);
            if (users == null || users.isEmpty()) {
                tvEmpty.setVisibility(View.VISIBLE);
                rvUsers.setVisibility(View.GONE);
            } else {
                tvEmpty.setVisibility(View.GONE);
                rvUsers.setVisibility(View.VISIBLE);
            }
        });

        viewModel.getOperationSuccess().observe(getViewLifecycleOwner(), success -> {
            if (success) {
                Toast.makeText(getContext(), "Thao tác thành công", Toast.LENGTH_SHORT).show();
            }
        });

        viewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onUserAction(User user, View anchor) {
        showActionDialog(user);
    }

    private void showActionDialog(User user) {
        BottomSheetDialog dialog = new BottomSheetDialog(requireContext());
        View view = getLayoutInflater().inflate(R.layout.dialog_user_actions, null);
        dialog.setContentView(view);

        TextView tvTitle = view.findViewById(R.id.tvTitle);
        TextView tvLockUnlock = view.findViewById(R.id.tvLockUnlock);
        TextView tvHideRestore = view.findViewById(R.id.tvHideRestore);
        TextView tvDelete = view.findViewById(R.id.tvDelete);

        tvTitle.setText("Actions for " + user.getUsername());

        // Configure Lock/Unlock
        if ("locked".equalsIgnoreCase(user.getStatus())) {
            tvLockUnlock.setText("Unlock Account");
            tvLockUnlock.setOnClickListener(v -> {
                showConfirmDialog("Unlock this account?", () -> viewModel.unlockUser(user));
                dialog.dismiss();
            });
        } else {
            tvLockUnlock.setText("Lock Account");
            tvLockUnlock.setOnClickListener(v -> {
                showConfirmDialog("Lock this account?", () -> viewModel.lockUser(user));
                dialog.dismiss();
            });
        }

        // Configure Hide/Restore
        if ("hidden".equalsIgnoreCase(user.getStatus())) {
            tvHideRestore.setText("Restore Account");
            tvHideRestore.setOnClickListener(v -> {
                showConfirmDialog("Restore this account?", () -> viewModel.restoreUser(user));
                dialog.dismiss();
            });
        } else {
            tvHideRestore.setText("Hide Account");
            tvHideRestore.setOnClickListener(v -> {
                showConfirmDialog("Hide this account?", () -> viewModel.hideUser(user));
                dialog.dismiss();
            });
        }

        // Configure Delete
        tvDelete.setOnClickListener(v -> {
            showConfirmDialog("Permanently delete this account?", () -> viewModel.deleteUser(user));
            dialog.dismiss();
        });

        dialog.show();
    }

    private void showConfirmDialog(String message, Runnable onConfirm) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Confirm Action")
                .setMessage(message)
                .setPositiveButton("Yes", (dialog, which) -> onConfirm.run())
                .setNegativeButton("No", null)
                .show();
    }
}
