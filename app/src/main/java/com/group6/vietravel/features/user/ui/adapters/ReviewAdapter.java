package com.group6.vietravel.features.user.ui.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.group6.vietravel.R;
import com.group6.vietravel.data.models.place.Place;
import com.group6.vietravel.data.models.review.Review;
import com.group6.vietravel.core.utils.PlaceUtils;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder> {

    private Context context;
    private List<Review> reviewList;

    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(Review review);
    }

    // 2. Hàm để Activity gọi vào
    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public ReviewAdapter(Context context, List<Review> reviewList) {
        this.context = context;
        this.reviewList = reviewList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_item_review, parent, false);
        return new ViewHolder(view, mListener, reviewList);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Review review = reviewList.get(position);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String dateStr = (review.getCreated_at() != null) ? sdf.format(review.getCreated_at()) : "";

        holder.comment.setText(review.getComment());
        holder.date.setText(dateStr);

        holder.namePlaceTextView.setText("Loading...");
        holder.ratingAvg.setRating(0);

        PlaceUtils.getPlaceById(review.getPlaceId(), new PlaceUtils.OnPlaceLoadedCallback() {
            @Override
            public void onPlaceLoaded(Place place) {
                holder.namePlaceTextView.setText(place.getName());
                holder.ratingAvg.setRating(place.getRatingAvg());
            }

            @Override
            public void onError(Exception e) {
                Log.e("ReviewAdapter", "Can not load place data");
                holder.namePlaceTextView.setText("Unknown Place");
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
        TextView namePlaceTextView;
        RatingBar ratingAvg;
        TextView date;
        TextView comment;

        public ViewHolder(@NonNull View itemView, final OnItemClickListener listener, final List<Review> dataList) {
            super(itemView);
            namePlaceTextView = itemView.findViewById(R.id.namePlaceTextView);
            ratingAvg = itemView.findViewById(R.id.ratingAvg);
            date = itemView.findViewById(R.id.date);
            comment = itemView.findViewById(R.id.comment);

            // Bắt sự kiện Click
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