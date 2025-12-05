package com.group6.vietravel.admin.ui.places;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.group6.vietravel.R;
import com.group6.vietravel.data.models.place.Place;

import java.util.ArrayList;
import java.util.List;

public class AdminPlaceAdapter extends RecyclerView.Adapter<AdminPlaceAdapter.PlaceViewHolder> {

    private List<Place> placeList;
    private Context context;
    private final OnPlaceActionListener listener;

    public interface OnPlaceActionListener {
        void onEdit(Place place);
        void onDelete(Place place);
        void onClick(Place place);
    }

    public AdminPlaceAdapter(Context context, OnPlaceActionListener listener) {
        this.context = context;
        this.listener = listener;
        this.placeList = new ArrayList<>();
    }

    public void setPlaces(List<Place> places) {
        this.placeList = places;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PlaceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_admin_place, parent, false);
        return new PlaceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaceViewHolder holder, int position) {
        Place place = placeList.get(position);

        holder.tvName.setText(place.getName());
        holder.tvAddress.setText(place.getAddress());
        holder.ratingBar.setRating(place.getRatingAvg());
        holder.tvRatingScore.setText(String.valueOf(place.getRatingAvg()));

        // Load ảnh đầu tiên
        if (place.getGalleryUrls() != null && !place.getGalleryUrls().isEmpty()) {
            Glide.with(context)
                    .load(place.getGalleryUrls().get(0))
                    .placeholder(R.drawable.ic_launcher_background) // Thay bằng ảnh placeholder của bạn
                    .into(holder.imgThumb);
        } else {
            holder.imgThumb.setImageResource(R.drawable.ic_launcher_background);
        }

        // Sự kiện click
        holder.btnEdit.setOnClickListener(v -> listener.onEdit(place));
        holder.btnDelete.setOnClickListener(v -> listener.onDelete(place));
        holder.itemView.setOnClickListener(v -> listener.onClick(place));
    }

    @Override
    public int getItemCount() {
        return placeList != null ? placeList.size() : 0;
    }

    public static class PlaceViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvAddress, tvRatingScore, tvDistance;
        ImageView imgThumb;
        RatingBar ratingBar;
        ImageButton btnEdit, btnDelete;

        public PlaceViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_place_name);
            tvAddress = itemView.findViewById(R.id.tv_address);
            tvRatingScore = itemView.findViewById(R.id.tv_rating_score);
            tvDistance = itemView.findViewById(R.id.tv_distance);
            imgThumb = itemView.findViewById(R.id.img_place_thumb);
            ratingBar = itemView.findViewById(R.id.rating_bar_small);
            btnEdit = itemView.findViewById(R.id.btn_edit);
            btnDelete = itemView.findViewById(R.id.btn_delete);
        }
    }
}