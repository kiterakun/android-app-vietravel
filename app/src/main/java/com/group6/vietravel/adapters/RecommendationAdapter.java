package com.group6.vietravel.adapters;

import android.content.Context;
import android.content.Intent;
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
import com.group6.vietravel.data.models.Place;
import com.group6.vietravel.data.models.Review;
import com.group6.vietravel.ui.detail.DetailActivity;
import com.group6.vietravel.utils.PlaceUtils;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class RecommendationAdapter extends RecyclerView.Adapter<RecommendationAdapter.ViewHolder> {

    private Context context;
    private List<Place> placeList;
    private LayoutInflater inflater;

    public RecommendationAdapter(Context context, List<Place> placeList) {
        this.context = context;
        this.placeList = placeList;
        this.inflater = LayoutInflater.from(context);
    }

    public void updateData(List<Place> newPlaces) {
        this.placeList = newPlaces;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (placeList == null) return 0;
        return placeList.size();
    }

    @NonNull
    @Override
    public RecommendationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.recycler_item_chat_recommendation, parent, false);
        return new RecommendationAdapter.ViewHolder(view, placeList);
    }

    @Override
    public void onBindViewHolder(@NonNull RecommendationAdapter.ViewHolder holder, int position) {
        Place place = placeList.get(position);

        String imageUrl = place.getThumbnailUrl();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(context)
                    .load(imageUrl)
                    .into(holder.imagePlace);
        }

        holder.namePlaceTextView.setText(place.getName());
        holder.addressTextView.setText(place.getAddress());
        holder.ratingAvg.setText(String.valueOf(place.getRatingAvg()));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();

                Intent intent = new Intent(context, DetailActivity.class);

                intent.putExtra("PLACE_OBJECT", place);

                context.startActivity(intent);
            }
        });
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imagePlace;
        TextView namePlaceTextView;
        TextView addressTextView;
        TextView ratingAvg;

        public ViewHolder(@NonNull View itemView, final List<Place> data) {
            super(itemView);
            imagePlace = itemView.findViewById(R.id.imagePlace);
            namePlaceTextView = itemView.findViewById(R.id.namePlaceTextView);
            addressTextView = itemView.findViewById(R.id.addressTextView);
            ratingAvg = itemView.findViewById(R.id.ratingAvg);
        }
    }
}

