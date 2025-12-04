package com.group6.vietravel.admin.ui.reviews;

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
 * NGƯỜI 2: KIỂM DUYỆT REVIEW (ADF02)
 * 
 * TODO List:
 * 1. Tạo ReviewModerationViewModel
 * 2. Tạo layout fragment_review_moderation.xml với:
 *    - Filter chips (All, Pending, Approved, Rejected)
 *    - Checkbox để select all (cho bulk actions)
 *    - RecyclerView
 *    - Bottom bar với Approve/Reject buttons (hiện khi có items selected)
 * 3. Tạo AdminReviewAdapter với:
 *    - Checkbox để chọn multiple items
 *    - Display: user avatar, username, rating, comment, place name
 *    - Status badge (pending/approved/rejected)
 *    - Action buttons (Approve/Reject/Delete)
 * 4. Tạo layout item_admin_review.xml
 * 
 * Chức năng cần implement:
 * - Hiển thị danh sách reviews
 * - Filter theo status (All/Pending/Approved/Rejected)
 * - Single approve/reject/delete
 * - Bulk approve/reject (chọn nhiều)
 * - Click item để xem chi tiết (user + place info)
 * - Hiển thị dialog confirm trước khi approve/reject
 * 
 * Code tham khảo:
 * - app/src/main/java/com/group6/vietravel/adapters/ReviewAdapter.java
 * - app/src/main/java/com/group6/vietravel/adapters/ReviewPlaceAdapter.java
 */
public class ReviewModerationFragment extends Fragment {

    private ReviewModerationViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_placeholder, container, false);
        
        TextView textView = view.findViewById(R.id.text_placeholder);
        textView.setText("Review Moderation Fragment\n\nNGƯỜI 2: Implement kiểm duyệt review ở đây");
        
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        viewModel = new ViewModelProvider(this).get(ReviewModerationViewModel.class);
        
        // TODO: Initialize views
        // TODO: Setup RecyclerView with adapter
        // TODO: Setup filter chips
        // TODO: Setup bulk action buttons
        // TODO: Observe ViewModel LiveData
    }
}
