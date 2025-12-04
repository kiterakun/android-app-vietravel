package com.group6.vietravel.admin.ui.reviews;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.group6.vietravel.R;
import com.group6.vietravel.data.models.place.Place;
import com.group6.vietravel.data.models.review.Review;
import com.group6.vietravel.data.models.user.User;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class AdminReviewAdapter extends RecyclerView.Adapter<AdminReviewAdapter.ReviewViewHolder> {

    private List<Review> reviews = new ArrayList<>();
    private Map<String, User> userMap = new HashMap<>();
    private Map<String, Place> placeMap = new HashMap<>();
    private final Set<String> selectedReviewIds = new HashSet<>();
    private final OnReviewActionListener listener;

    public interface OnReviewActionListener {
        void onApprove(Review review);
        void onReject(Review review);
        void onSelectionChanged(int selectedCount);
    }

    public AdminReviewAdapter(OnReviewActionListener listener) {
        this.listener = listener;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
        notifyDataSetChanged();
    }

    public void setUsers(List<User> users) {
        userMap.clear();
        for (User user : users) {
            userMap.put(user.getUid(), user);
        }
        notifyDataSetChanged();
    }

    public void setPlaces(List<Place> places) {
        placeMap.clear();
        for (Place place : places) {
            placeMap.put(place.getPlaceId(), place);
        }
        notifyDataSetChanged();
    }

    public void selectAll() {
        selectedReviewIds.clear();
        for (Review review : reviews) {
            selectedReviewIds.add(review.getReviewId());
        }
        notifyDataSetChanged();
        listener.onSelectionChanged(selectedReviewIds.size());
    }

    public void clearSelection() {
        selectedReviewIds.clear();
        notifyDataSetChanged();
        listener.onSelectionChanged(0);
    }

    public List<String> getSelectedReviewIds() {
        return new ArrayList<>(selectedReviewIds);
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_admin_review, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        Review review = reviews.get(position);
        holder.bind(review);
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    class ReviewViewHolder extends RecyclerView.ViewHolder {
        CheckBox cbSelect;
        ImageView ivAvatar;
        TextView tvUsername;
        RatingBar ratingBar;
        TextView tvDate;
        TextView tvContent;
        TextView tvPlaceName;
        TextView tvStatus;
        ImageButton btnApprove;
        ImageButton btnReject;

        ReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            cbSelect = itemView.findViewById(R.id.cbSelect);
            ivAvatar = itemView.findViewById(R.id.ivAvatar);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvContent = itemView.findViewById(R.id.tvContent);
            tvPlaceName = itemView.findViewById(R.id.tvPlaceName);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            btnApprove = itemView.findViewById(R.id.btnApprove);
            btnReject = itemView.findViewById(R.id.btnReject);
        }

        void bind(Review review) {
            // Bind User Data
            User user = userMap.get(review.getUserId());
            if (user != null) {
                tvUsername.setText(user.getUsername());
                if (user.getAvatar_url() != null && !user.getAvatar_url().isEmpty()) {
                    Glide.with(itemView.getContext())
                            .load(user.getAvatar_url())
                            .placeholder(R.drawable.ic_launcher_background)
                            .into(ivAvatar);
                } else {
                    ivAvatar.setImageResource(R.drawable.ic_launcher_background);
                }
            } else {
                tvUsername.setText("Unknown User");
                ivAvatar.setImageResource(R.drawable.ic_launcher_background);
            }

            // Bind Place Data
            Place place = placeMap.get(review.getPlaceId());
            if (place != null) {
                tvPlaceName.setText(place.getName());
            } else {
                tvPlaceName.setText("Unknown Place");
            }

            // Bind Review Data
            if (ratingBar != null) {
                ratingBar.setRating(review.getRating());
            }
            tvContent.setText(review.getComment());
            
            if (review.getCreated_at() != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
                tvDate.setText(sdf.format(review.getCreated_at()));
            } else {
                tvDate.setText("");
            }

            // Status
            String status = review.getStatus();
            if (status == null) status = "pending"; // Default to pending if null
            
            tvStatus.setText(status);
            
            // Color coding for status
            int statusColor;
            switch (status.toLowerCase()) {
                case "approved":
                    statusColor = 0xFF10B981; // Green
                    break;
                case "rejected":
                    statusColor = 0xFFEF4444; // Red
                    break;
                default:
                    statusColor = 0xFFF59E0B; // Amber/Orange for pending
                    break;
            }
            tvStatus.setTextColor(statusColor);
            
            // Selection
            cbSelect.setOnCheckedChangeListener(null);
            cbSelect.setChecked(selectedReviewIds.contains(review.getReviewId()));
            cbSelect.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    selectedReviewIds.add(review.getReviewId());
                } else {
                    selectedReviewIds.remove(review.getReviewId());
                }
                listener.onSelectionChanged(selectedReviewIds.size());
            });

            // Actions
            btnApprove.setOnClickListener(v -> listener.onApprove(review));
            btnReject.setOnClickListener(v -> listener.onReject(review));

            // Visibility of actions based on status
            if ("approved".equals(status)) {
                btnApprove.setVisibility(View.GONE);
                btnReject.setVisibility(View.VISIBLE);
            } else if ("rejected".equals(status)) {
                btnApprove.setVisibility(View.VISIBLE);
                btnReject.setVisibility(View.GONE);
            } else { // pending
                btnApprove.setVisibility(View.VISIBLE);
                btnReject.setVisibility(View.VISIBLE);
            }
        }
    }
}
