package com.group6.vietravel.admin.ui.notifications;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.group6.vietravel.R;
import com.group6.vietravel.admin.data.models.Notification;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AdminNotificationAdapter extends RecyclerView.Adapter<AdminNotificationAdapter.NotifViewHolder> {
    private List<Notification> list = new ArrayList<>();

    // 1. Khai báo Listener
    private OnNotificationListener listener;

    public void setList(List<Notification> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    // 2. Hàm set Listener
    public void setListener(OnNotificationListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public NotifViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_admin_notification, parent, false);
        return new NotifViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotifViewHolder holder, int position) {
        Notification item = list.get(position);
        holder.tvTitle.setText(item.getTitle());
        holder.tvBody.setText(item.getMessage());

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        holder.tvDate.setText(sdf.format(new Date(item.getTimestamp())));

        // 3. Bắt sự kiện nhấn giữ (Long Click) để xóa
        holder.itemView.setOnLongClickListener(v -> {
            if (listener != null) {
                listener.onDeleteClick(item);
            }
            return true; // Trả về true để báo là sự kiện đã được xử lý
        });
    }

    @Override
    public int getItemCount() { return list.size(); }

    static class NotifViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvBody, tvDate;
        public NotifViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvBody = itemView.findViewById(R.id.tvBody);
            tvDate = itemView.findViewById(R.id.tvDate);
        }
    }

    // 4. Interface giao tiếp
    public interface OnNotificationListener {
        void onDeleteClick(Notification notification);
    }
}