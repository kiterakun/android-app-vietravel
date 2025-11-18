package com.group6.vietravel.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.group6.vietravel.R;
import com.group6.vietravel.data.models.Place;
import com.group6.vietravel.data.models.Review;
import com.group6.vietravel.utils.PlaceUtils;

import java.text.SimpleDateFormat;
import java.util.List;

public class ReviewAdapter extends BaseAdapter {

    private Context context;
    private List<Review> reviewList;
    private LayoutInflater inflater;

    @Override
    public int getCount() {
        return reviewList.size();
    }

    @Override
    public Object getItem(int position) {
        return reviewList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public ReviewAdapter(Context context, List<Review> reviewList) {
        this.context = context;
        this.reviewList = reviewList;
        this.inflater = LayoutInflater.from(context);
    }

    private static class ViewHolder {
        TextView namePlaceTextView;
        TextView rating;
        TextView date;
        TextView comment;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ReviewAdapter.ViewHolder holder;
        // 'convertView' là View cũ được tái sử dụng để tiết kiệm bộ nhớ
        if (convertView == null) {
            // Nếu không có View cũ, ta "inflate" (tạo) layout mới
            convertView = inflater.inflate(R.layout.list_item_review, parent, false);

            // Tạo ViewHolder để lưu các View con (chỉ làm 1 lần)
            holder = new ReviewAdapter.ViewHolder();
            holder.namePlaceTextView = convertView.findViewById(R.id.namePlaceTextView);
            holder.rating = convertView.findViewById(R.id.rating);
            holder.date = convertView.findViewById(R.id.date);
            holder.comment = convertView.findViewById(R.id.comment);

            // Gắn 'holder' vào 'convertView' để dùng lại lần sau
            convertView.setTag(holder);
        } else {
            // Nếu có View cũ, ta lấy 'holder' đã lưu
            holder = (ReviewAdapter.ViewHolder) convertView.getTag();
        }

        Review review = reviewList.get(position);
        PlaceUtils.getPlaceById(review.getPlaceId(),new PlaceUtils.OnPlaceLoadedCallback(){
            @Override
            public void onPlaceLoaded(Place place) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String date = sdf.format(review.getCreated_at());

                holder.comment.setText(review.getComment());
                holder.namePlaceTextView.setText(place.getName());
                holder.date.setText(date);
                holder.rating.setText(String.valueOf(review.getRating()));
            }
            @Override
            public void onError(Exception e) {
                Log.e("ReviewAdapter","Can not load data");
            }
        });



        return convertView;
    }

    public void updateData(List<Review> newReviews) {
        // Xóa sạch dữ liệu cũ
        reviewList.clear();

        reviewList.addAll(newReviews);

        notifyDataSetChanged();
    }
}
