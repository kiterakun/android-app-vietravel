package com.group6.vietravel.features.user.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.group6.vietravel.R;
import com.group6.vietravel.data.models.place.Place;
import com.bumptech.glide.Glide;

import java.util.List;

public class PlaceAdapter extends RecyclerView.Adapter<PlaceAdapter.ViewHolder> {

    private Context context;
    private List<Place> placeList;
    private LayoutInflater inflater;

    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(Place place);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public PlaceAdapter(Context context, List<Place> placeList) {
        this.context = context;
        this.placeList = placeList;
        this.inflater = LayoutInflater.from(context);
    }

    public void updateData(List<Place> newPlaces) {
        this.placeList = newPlaces;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.recycler_item_place, parent, false);
        return new ViewHolder(view, mListener, placeList);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Place place = placeList.get(position);

        String imageUrl = place.getThumbnailUrl();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(context)
                    .load(imageUrl)
                    .into(holder.imagePlace);
        }

        holder.namePlaceTextView.setText(place.getName());
        holder.descriptionTextView.setText(place.getDescription());
        holder.ratingAvg.setRating(place.getRatingAvg());
    }

    @Override
    public int getItemCount() {
        if (placeList == null) return 0;
        return placeList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imagePlace;
        TextView namePlaceTextView;
        TextView descriptionTextView;
        RatingBar ratingAvg;

        public ViewHolder(@NonNull View itemView, final OnItemClickListener listener, final List<Place> data) {
            super(itemView);
            imagePlace = itemView.findViewById(R.id.imagePlace);
            namePlaceTextView = itemView.findViewById(R.id.namePlaceTextView);
            descriptionTextView = itemView.findViewById(R.id.descriptionTextView);
            ratingAvg = itemView.findViewById(R.id.ratingAvg);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(data.get(position));
                        }
                    }
                }
            });
        }
    }
}