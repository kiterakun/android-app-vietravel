package com.group6.vietravel.admin.ui.users;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.group6.vietravel.R;
import com.group6.vietravel.data.models.user.User;

import java.util.ArrayList;
import java.util.List;

public class AdminUserAdapter extends RecyclerView.Adapter<AdminUserAdapter.UserViewHolder> {

    private List<User> users = new ArrayList<>();
    private final OnUserActionListener listener;

    public interface OnUserActionListener {
        void onUserAction(User user, View anchor);
    }

    public AdminUserAdapter(OnUserActionListener listener) {
        this.listener = listener;
    }

    public void setUsers(List<User> users) {
        this.users = users;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_admin_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = users.get(position);
        holder.bind(user);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    class UserViewHolder extends RecyclerView.ViewHolder {
        ImageView ivAvatar;
        TextView tvUsername;
        TextView tvEmail;
        TextView tvStatus;
        TextView tvReviewCount;
        TextView tvCheckinCount;
        TextView tvPoints;
        ImageButton btnMore;

        UserViewHolder(@NonNull View itemView) {
            super(itemView);
            ivAvatar = itemView.findViewById(R.id.ivAvatar);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvEmail = itemView.findViewById(R.id.tvEmail);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvReviewCount = itemView.findViewById(R.id.tvReviewCount);
            tvCheckinCount = itemView.findViewById(R.id.tvCheckinCount);
            tvPoints = itemView.findViewById(R.id.tvPoints);
            btnMore = itemView.findViewById(R.id.btnMore);
        }

        void bind(User user) {
            tvUsername.setText(user.getUsername());
            tvEmail.setText(user.getEmail());
            
            if (user.getAvatar_url() != null && !user.getAvatar_url().isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(user.getAvatar_url())
                        .placeholder(R.drawable.ic_launcher_background)
                        .into(ivAvatar);
            } else {
                ivAvatar.setImageResource(R.drawable.ic_launcher_background);
            }

            tvPoints.setText(String.valueOf(user.getPoints()));
            // Placeholders for now as they are not in User model
            tvReviewCount.setText("0"); 
            tvCheckinCount.setText("0");

            String status = user.getStatus();
            if (status == null) status = "active";
            tvStatus.setText(status);

            int statusColor;
            switch (status.toLowerCase()) {
                case "active":
                    statusColor = 0xFF10B981; // Green
                    break;
                case "locked":
                    statusColor = 0xFFEF4444; // Red
                    break;
                case "hidden":
                    statusColor = 0xFF6B7280; // Gray
                    break;
                default:
                    statusColor = 0xFF10B981;
                    break;
            }
            tvStatus.setTextColor(statusColor);

            btnMore.setOnClickListener(v -> listener.onUserAction(user, v));
        }
    }
}
