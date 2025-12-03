package com.group6.vietravel.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;
import com.group6.vietravel.R;
import com.group6.vietravel.data.models.Place;

import java.util.List;

public class RankingAdapter extends RecyclerView.Adapter<RankingAdapter.ViewHolder> {

    private Context context;
    private List<Place> placeList;
    private LayoutInflater inflater;

    private RankingAdapter.OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(Place place);
    }

    public void setOnItemClickListener(RankingAdapter.OnItemClickListener listener) {
        mListener = listener;
    }

    public RankingAdapter(Context context, List<Place> placeList) {
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
    public RankingAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.recycler_item_ranking, parent, false);
        return new RankingAdapter.ViewHolder(view, mListener, placeList);
    }

    @Override
    public void onBindViewHolder(@NonNull RankingAdapter.ViewHolder holder, int position) {
        Place place = placeList.get(position);

        int rank = position + 1;
        holder.tv_rank_number.setText("#" + rank);

        MaterialCardView cardView = holder.card_ranking_item;

        if (rank == 1) {
            cardView.setStrokeColor(Color.parseColor("#FFD700"));
            cardView.setStrokeWidth(8);

            holder.tv_rank_number.setTextColor(Color.parseColor("#FFD700"));

        } else if (rank == 2) {
            // --- TOP 2 ---
            // Ví dụ: Màu Bạc (Silver)
            cardView.setStrokeColor(Color.parseColor("#C0C0C0"));
            cardView.setStrokeWidth(6);

            holder.tv_rank_number.setTextColor(Color.parseColor("#C0C0C0"));

        } else if (rank == 3) {

            cardView.setStrokeColor(Color.parseColor("#CD7F32"));
            cardView.setStrokeWidth(6);

            holder.tv_rank_number.setTextColor(Color.parseColor("#CD7F32"));

        } else {
            cardView.setStrokeColor(Color.parseColor("#E0E0E0"));
            cardView.setStrokeWidth(3);
            holder.tv_rank_number.setTextColor(Color.GRAY);
        }

        String imageUrl = place.getThumbnailUrl();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(context)
                    .load(imageUrl)
                    .into(holder.imagePlace);
        }

        holder.tv_rank_name.setText(place.getName());
        holder.tv_description.setText(place.getDescription());
        holder.tv_rank_score.setText(String.valueOf(place.getRatingAvg()));
        holder.tv_rating_count.setText(String.valueOf(place.getRatingCount()));

    }

        @Override
        public int getItemCount () {
            if (placeList == null) return 0;
            return placeList.size();
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            MaterialCardView card_ranking_item;
            ImageView imagePlace;
            TextView tv_rank_number;
            TextView tv_rank_name;
            TextView tv_rank_score;
            TextView tv_rating_count;
            TextView tv_description;
            

            public ViewHolder(@NonNull View itemView, final RankingAdapter.OnItemClickListener listener, final List<Place> data) {
                super(itemView);
                imagePlace = itemView.findViewById(R.id.imagePlace);
                tv_rank_number = itemView.findViewById(R.id.tv_rank_number);
                tv_rank_name = itemView.findViewById(R.id.tv_rank_name);
                tv_rank_score = itemView.findViewById(R.id.tv_rank_score);
                tv_rating_count = itemView.findViewById(R.id.tv_rating_count);
                tv_description = itemView.findViewById(R.id.tv_description);
                card_ranking_item = itemView.findViewById(R.id.card_ranking_item);


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
