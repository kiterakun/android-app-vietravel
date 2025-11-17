package com.group6.vietravel.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.group6.vietravel.R;
import com.group6.vietravel.data.models.Place;
import com.bumptech.glide.Glide;

import java.util.List;

public class PlaceAdapter extends BaseAdapter {

    private Context context;
    private List<Place> placeList;
    private LayoutInflater inflater;

    public PlaceAdapter(Context context, List<Place> placeList) {
        this.context = context;
        this.placeList = placeList;
        this.inflater = LayoutInflater.from(context);
    }
    @Override
    public int getCount() {
        return placeList.size();
    }

    @Override
    public Object getItem(int position) {
        return placeList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private static class ViewHolder {
        ImageView imagePlace;
        TextView namePlaceTextView;
        TextView descriptionTextView;
        TextView ratingAvg;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        // 'convertView' là View cũ được tái sử dụng để tiết kiệm bộ nhớ
        if (convertView == null) {
            // Nếu không có View cũ, ta "inflate" (tạo) layout mới
            convertView = inflater.inflate(R.layout.list_item_place, parent, false);

            // Tạo ViewHolder để lưu các View con (chỉ làm 1 lần)
            holder = new ViewHolder();
            holder.imagePlace =  convertView.findViewById(R.id.imagePlace);
            holder.namePlaceTextView =  convertView.findViewById(R.id.namePlaceTextView);
            holder.descriptionTextView = convertView.findViewById(R.id.descriptionTextView);
            holder.ratingAvg = convertView.findViewById(R.id.ratingAvg);

            // Gắn 'holder' vào 'convertView' để dùng lại lần sau
            convertView.setTag(holder);
        } else {
            // Nếu có View cũ, ta lấy 'holder' đã lưu
            holder = (ViewHolder) convertView.getTag();
        }

        // Lấy dữ liệu cho vị trí này
        Place place = placeList.get(position);

        String imageUrl = place.getCached_image_url();

        // 3. DÙNG GLIDE ĐỂ TẢI ẢNH (CHỈ 1 DÒNG)
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(parent.getContext())
                    .load(imageUrl) // Tải từ URL
                    .into(holder.imagePlace); // Đặt vào ImageView
        }

        // Gán dữ liệu lên các View con thông qua 'holder'
        holder.namePlaceTextView.setText(place.getName());
        holder.descriptionTextView.setText(place.getDescription());
        holder.ratingAvg.setText(String.format("%.1f", place.getRating_avg()));

        return convertView;
    }

    public void updateData(List<Place> newPlaces) {
        // Xóa sạch dữ liệu cũ
        placeList.clear();

        placeList.addAll(newPlaces);

        notifyDataSetChanged();
    }
}
