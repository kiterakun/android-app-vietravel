package com.group6.vietravel.admin.ui.reviews;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.ChipGroup;
import com.group6.vietravel.R;
import com.group6.vietravel.data.models.review.Review;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ReviewModerationFragment extends Fragment implements AdminReviewAdapter.OnReviewActionListener {

    private ReviewModerationViewModel viewModel;
    private AdminReviewAdapter adapter;
    private List<Review> allReviews = new ArrayList<>();
    private String currentFilter = "all"; // all, pending, approved, rejected

    private RecyclerView rvReviews;
    private ChipGroup chipGroupFilter;
    private CheckBox cbSelectAll;
    private View layoutBottomBar;
    private Button btnBulkApprove;
    private Button btnBulkReject;
    private TextView tvEmpty;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_review_moderation, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(ReviewModerationViewModel.class);

        initViews(view);
        setupRecyclerView();
        setupListeners();
        setupObservers();

        // Load initial data
        viewModel.loadAllReviews();
        // viewModel.loadAllUsers(); // <-- ĐÃ XÓA DÒNG NÀY
        viewModel.loadAllPlaces();
    }

    private void initViews(View view) {
        rvReviews = view.findViewById(R.id.rvReviews);
        chipGroupFilter = view.findViewById(R.id.chipGroupFilter);
        cbSelectAll = view.findViewById(R.id.cbSelectAll);
        layoutBottomBar = view.findViewById(R.id.layoutBottomBar);
        btnBulkApprove = view.findViewById(R.id.btnBulkApprove);
        btnBulkReject = view.findViewById(R.id.btnBulkReject);
        tvEmpty = view.findViewById(R.id.tvEmpty);
    }

    private void setupRecyclerView() {
        adapter = new AdminReviewAdapter(this);
        rvReviews.setLayoutManager(new LinearLayoutManager(getContext()));
        rvReviews.setAdapter(adapter);
    }

    private void setupListeners() {
        // Filter Chips
        chipGroupFilter.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.chipPending) {
                currentFilter = "pending";
            } else if (checkedId == R.id.chipApproved) {
                currentFilter = "approved";
            } else if (checkedId == R.id.chipRejected) {
                currentFilter = "rejected";
            } else {
                currentFilter = "all";
            }
            filterReviews();
        });

        // Select All
        cbSelectAll.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                adapter.selectAll();
            } else {
                adapter.clearSelection();
            }
        });

        // Bulk Actions
        btnBulkApprove.setOnClickListener(v -> {
            List<String> selectedIds = adapter.getSelectedReviewIds();
            if (!selectedIds.isEmpty()) {
                showConfirmDialog("Duyệt " + selectedIds.size() + " đánh giá?", () -> {
                    viewModel.bulkApprove(selectedIds);
                    adapter.clearSelection();
                    cbSelectAll.setChecked(false);
                });
            }
        });

        btnBulkReject.setOnClickListener(v -> {
            List<String> selectedIds = adapter.getSelectedReviewIds();
            if (!selectedIds.isEmpty()) {
                showConfirmDialog("Từ chối " + selectedIds.size() + " đánh giá?", () -> {
                    viewModel.bulkReject(selectedIds);
                    adapter.clearSelection();
                    cbSelectAll.setChecked(false);
                });
            }
        });
    }

    private void setupObservers() {
        viewModel.getAllReviews().observe(getViewLifecycleOwner(), reviews -> {
            allReviews = reviews;
            filterReviews();
        });

        // ĐÃ XÓA OBSERVER CHO USERS
        /* viewModel.getAllUsers().observe(getViewLifecycleOwner(), users -> {
            adapter.setUsers(users);
        });
        */

        viewModel.getAllPlaces().observe(getViewLifecycleOwner(), places -> {
            adapter.setPlaces(places);
        });

        viewModel.getOperationSuccess().observe(getViewLifecycleOwner(), success -> {
            if (success) {
                Toast.makeText(getContext(), "Thao tác thành công", Toast.LENGTH_SHORT).show();
            }
        });

        viewModel.getError().observe(getViewLifecycleOwner(), error -> {
            Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
        });
    }

    private void filterReviews() {
        List<Review> filteredList;
        if ("all".equals(currentFilter)) {
            filteredList = new ArrayList<>(allReviews);
        } else {
            filteredList = allReviews.stream()
                    .filter(r -> r.getStatus() != null && currentFilter.equalsIgnoreCase(r.getStatus()))
                    .collect(Collectors.toList());
        }

        adapter.setReviews(filteredList);

        if (filteredList.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            rvReviews.setVisibility(View.GONE);
        } else {
            tvEmpty.setVisibility(View.GONE);
            rvReviews.setVisibility(View.VISIBLE);
        }
    }

    private void showConfirmDialog(String message, Runnable onConfirm) {
        new AlertDialog.Builder(getContext())
                .setTitle("Xác nhận")
                .setMessage(message)
                .setPositiveButton("Đồng ý", (dialog, which) -> onConfirm.run())
                .setNegativeButton("Hủy", null)
                .show();
    }

    @Override
    public void onSelectionChanged(int selectedCount) {
        if (selectedCount > 0) {
            layoutBottomBar.setVisibility(View.VISIBLE);
        } else {
            layoutBottomBar.setVisibility(View.GONE);
        }
    }

    // --- CÁC HÀM XỬ LÝ HÀNH ĐỘNG ---

    @Override
    public void onApprove(Review review) {
        // Kiểm tra nếu đã duyệt rồi thì không làm gì cả
        if ("approved".equals(review.getStatus())) {
            Toast.makeText(getContext(), "Đánh giá này đã được duyệt rồi", Toast.LENGTH_SHORT).show();
            return;
        }

        showConfirmDialog("Duyệt đánh giá này và cập nhật điểm địa điểm?", () -> {
            // Truyền nguyên object Review sang ViewModel để tính toán
            viewModel.approveReview(review);
        });
    }

    @Override
    public void onReject(Review review) {
        showConfirmDialog("Từ chối đánh giá này?", () -> {
            viewModel.rejectReview(review.getReviewId());
        });
    }

//    @Override
//    public void onEdit(Review review) {
//        // Nếu bạn muốn giữ tính năng sửa, có thể bỏ comment dòng dưới (nếu đã tạo Dialog)
//        // showEditDialog(review);
//
//        // Nếu không cần sửa nữa thì để trống hoặc thông báo
//        Toast.makeText(getContext(), "Tính năng chỉnh sửa đang tạm khóa", Toast.LENGTH_SHORT).show();
//    }
}