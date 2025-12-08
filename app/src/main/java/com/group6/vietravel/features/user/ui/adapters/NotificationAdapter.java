package com.group6.vietravel.features.user.ui.adapters;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.group6.vietravel.R;
import com.group6.vietravel.data.models.notification.Notification;


import java.util.Date;
import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    private Context context;
    private List<Notification> notificationList;
    private LayoutInflater inflater;

    public NotificationAdapter(Context context, List<Notification> notificationList) {
        this.context = context;
        this.notificationList = notificationList;
        this.inflater = LayoutInflater.from(context);
    }

    public void updateData(List<Notification> newNotifications) {
        this.notificationList = newNotifications;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public NotificationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.recycler_item_notification, parent, false);
        return new NotificationAdapter.ViewHolder(view, notificationList);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationAdapter.ViewHolder holder, int position) {
        Notification notification = notificationList.get(position);

        holder.tv_notif_title.setText(notification.getTitle());

        holder.tv_notif_body.setText(notification.getMessage());

        Date date = new Date(notification.getTimestamp());
        Date now = new Date();
        long diffMs = now.getTime() - date.getTime();
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

        holder.tv_notif_time.setText(result);
    }

    @Override
    public int getItemCount() {
        if (notificationList == null) return 0;
        return notificationList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_notif_title, tv_notif_body, tv_notif_time;

        public ViewHolder(@NonNull View itemView, final List<Notification> data) {
            super(itemView);

            tv_notif_title = itemView.findViewById(R.id.tv_notif_title);
            tv_notif_body = itemView.findViewById(R.id.tv_notif_body);
            tv_notif_time = itemView.findViewById(R.id.tv_notif_time);

        }
    }
}