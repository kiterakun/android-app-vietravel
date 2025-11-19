package com.group6.vietravel.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.group6.vietravel.R;
import com.group6.vietravel.data.models.Review;
import com.group6.vietravel.data.models.User;
import com.group6.vietravel.utils.UserUtils;

import java.util.Date;
import java.util.List;

public class ReviewPlaceAdapter extends RecyclerView.Adapter<ReviewPlaceAdapter.ViewHolder> {

    private Context context;
    private List<Review> reviewList;

    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(Review review);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public ReviewPlaceAdapter(Context context, List<Review> reviewList) {
        this.context = context;
        this.reviewList = reviewList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate layout item
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_item_review_place, parent, false);
        return new ViewHolder(view, mListener, reviewList);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Review review = reviewList.get(position);

        holder.tv_comment_content.setText(review.getComment());
        holder.rb_item_rating.setRating(review.getRating());
        holder.tv_username.setText("Loading...");
        holder.img_avatar.setImageResource(R.drawable.ic_launcher_background);

        Date now = new Date();
        long diffMs = now.getTime() - review.getCreated_at().getTime();
        long diffSeconds = diffMs / 1000;
        long diffMinutes = diffSeconds / 60;
        long diffHours   = diffMinutes / 60;
        long diffDays    = diffHours / 24;
        String result;

        if (diffMinutes < 1) {
            result = "Vừa xong";
        } else if (diffHours < 1) {
            result = diffMinutes + " phút trước";
        } else if (diffDays < 1) {
            result = diffHours + " giờ trước";
        } else {
            result = diffDays + " ngày trước";
        }

        holder.tv_time.setText(result);

        UserUtils.getUserById(review.getUserId(), new UserUtils.OnUserLoadedCallback() {
            @Override
            public void onPlaceLoaded(User user) {
                if (user == null) return;

                holder.tv_username.setText(user.getUsername());

                String imageUrl = user.getAvatar_url();
                if (imageUrl != null && !imageUrl.isEmpty()) {
                    Glide.with(context)
                            .load(imageUrl)
                            .into(holder.img_avatar);
                }
            }

            @Override
            public void onError(Exception e) {
                Log.e("ReviewPlaceAdapter", "Can not load user data");
                holder.tv_username.setText("Unknown User");
            }
        });
    }

    @Override
    public int getItemCount() {
        if (reviewList == null) return 0;
        return reviewList.size();
    }

    public void updateData(List<Review> newReviews) {
        if (reviewList != null) {
            reviewList.clear();
            reviewList.addAll(newReviews);
            notifyDataSetChanged();
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView img_avatar;
        TextView tv_username;
        RatingBar rb_item_rating;
        TextView tv_comment_content;
        TextView tv_time;

        public ViewHolder(@NonNull View itemView, final OnItemClickListener listener, final List<Review> dataList) {
            super(itemView);
            img_avatar = itemView.findViewById(R.id.img_avatar);
            tv_username = itemView.findViewById(R.id.tv_username);
            rb_item_rating = itemView.findViewById(R.id.rb_item_rating);
            tv_comment_content = itemView.findViewById(R.id.tv_comment_content);
            tv_time = itemView.findViewById(R.id.tv_time);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(dataList.get(position));
                        }
                    }
                }
            });
        }
    }
}