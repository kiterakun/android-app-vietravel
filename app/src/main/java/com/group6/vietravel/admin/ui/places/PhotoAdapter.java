package com.group6.vietravel.admin.ui.places;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.group6.vietravel.R;

import java.util.ArrayList;
import java.util.List;

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder> {

    private final Context context;
    // List chứa object: có thể là String (Url) hoặc Uri
    private final List<Object> photos;
    private final OnPhotoRemoveListener listener;

    public interface OnPhotoRemoveListener {
        void onRemove(int position);
    }

    public PhotoAdapter(Context context, OnPhotoRemoveListener listener) {
        this.context = context;
        this.listener = listener;
        this.photos = new ArrayList<>();
    }

    public void addPhoto(Object photo) {
        this.photos.add(photo);
        notifyItemInserted(photos.size() - 1);
    }

    public void setPhotos(List<String> urls) {
        this.photos.clear();
        if (urls != null) {
            this.photos.addAll(urls);
        }
        notifyDataSetChanged();
    }

    public void removePhoto(int position) {
        if (position >= 0 && position < photos.size()) {
            this.photos.remove(position);
            notifyItemRemoved(position);
        }
    }

    public List<Object> getCurrentPhotos() {
        return photos;
    }

    @NonNull
    @Override
    public PhotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_photo_preview, parent, false);
        return new PhotoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoViewHolder holder, int position) {
        Object item = photos.get(position);

        // Glide thông minh: tự hiểu cả String URL và Uri
        Glide.with(context)
                .load(item)
                .into(holder.imgPreview);

        holder.btnRemove.setOnClickListener(v -> listener.onRemove(position));
    }

    @Override
    public int getItemCount() {
        return photos.size();
    }

    static class PhotoViewHolder extends RecyclerView.ViewHolder {
        ImageView imgPreview;
        ImageButton btnRemove;

        public PhotoViewHolder(@NonNull View itemView) {
            super(itemView);
            imgPreview = itemView.findViewById(R.id.img_preview);
            btnRemove = itemView.findViewById(R.id.btn_remove_photo);
        }
    }
}