package com.group6.vietravel.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.group6.vietravel.R;
import com.group6.vietravel.data.models.ChatMessage;
import com.group6.vietravel.data.models.Place;
import com.group6.vietravel.ui.main.MainViewModel;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<ChatMessage> messageList;
    private static final int TYPE_USER = 1;
    private static final int TYPE_BOT = 2;
    private static RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();
    public static RecommendationAdapter recommendationAdapter;

    public ChatAdapter(List<ChatMessage> messageList) {
        this.messageList = messageList;
    }

    @Override
    public int getItemViewType(int position) {
        if (messageList.get(position).isUser()) {
            return TYPE_USER;
        } else {
            return TYPE_BOT;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_USER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_chat_user, parent, false);
            return new UserMessageViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_chat_bot, parent, false);
            return new BotMessageViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatMessage message = messageList.get(position);

        if (getItemViewType(position) == TYPE_USER) {
            ((UserMessageViewHolder) holder).bind(message);
        } else {
            ((BotMessageViewHolder) holder).bind(message);
        }

    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    static class UserMessageViewHolder extends RecyclerView.ViewHolder {
        TextView tvContent, tvTime;
        UserMessageViewHolder(View itemView) {
            super(itemView);
            tvContent = itemView.findViewById(R.id.tv_content);
            tvTime = itemView.findViewById(R.id.tv_time);
        }
        void bind(ChatMessage msg) {
            tvContent.setText(msg.getContent());
            tvTime.setText(msg.getTimestamp());
        }
    }

    static class BotMessageViewHolder extends RecyclerView.ViewHolder {
        private MainViewModel mainViewModel;
        TextView tvContent, tvTime;
        RecyclerView rvRecommendations;
        BotMessageViewHolder(View itemView) {
            super(itemView);

            mainViewModel = new MainViewModel();

            tvContent = itemView.findViewById(R.id.tv_content);
            tvTime = itemView.findViewById(R.id.tv_time);
            rvRecommendations = itemView.findViewById(R.id.rv_recommendations);
        }
        void bind(ChatMessage msg) {
            tvContent.setText(msg.getContent());
            tvTime.setText(msg.getTimestamp());
            rvRecommendations.setRecycledViewPool(viewPool);

            if (msg.getRecommendedPlaces() != null && !msg.getRecommendedPlaces().isEmpty()) {
                rvRecommendations.setVisibility(View.VISIBLE);

                LinearLayoutManager layoutManager = new LinearLayoutManager(itemView.getContext(), LinearLayoutManager.HORIZONTAL, false);
                rvRecommendations.setLayoutManager(layoutManager);

                recommendationAdapter = new RecommendationAdapter(itemView.getContext(),msg.getRecommendedPlaces());
                rvRecommendations.setAdapter(recommendationAdapter);

            } else {
                // TRƯỜNG HỢP KHÔNG CÓ ĐỊA ĐIỂM
                rvRecommendations.setVisibility(View.GONE);
            }
        }
    }
}
