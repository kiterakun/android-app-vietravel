package com.group6.vietravel.ui.main.chatbot;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import com.group6.vietravel.R;
import com.group6.vietravel.adapters.ChatAdapter;
import com.group6.vietravel.adapters.RecommendationAdapter;
import com.group6.vietravel.data.models.ChatMessage;
import com.group6.vietravel.data.models.Place;
import com.group6.vietravel.ui.main.MainViewModel;

import java.util.ArrayList;
import java.util.List;

public class ChatbotFragment extends Fragment {
    private MainViewModel mainViewModel;
    private ChatbotViewModel chatbotViewModel;
    private List<ChatMessage> chatList = new ArrayList<>();
    private ChatAdapter adapter = new ChatAdapter(chatList);
    private RecyclerView rv_chat;
    private EditText edt_chat_input;
    private ImageButton btn_send;

    public static ChatbotFragment newInstance() {
        return new ChatbotFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chatbot, container, false);
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        chatbotViewModel = new ViewModelProvider(this).get(ChatbotViewModel.class);
        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        rv_chat = view.findViewById(R.id.rv_chat);
        edt_chat_input = view.findViewById(R.id.edt_chat_input);
        btn_send = view.findViewById(R.id.btn_send);

        rv_chat.setAdapter(adapter);
        rv_chat.setLayoutManager(new LinearLayoutManager(getContext()));

        chatList.add(new ChatMessage("Xin chào " +
                chatbotViewModel.getCurrentUser().getValue().getUsername() +
                ", tôi có thể giúp gì cho bạn", false));
        adapter.notifyItemInserted(chatList.size() - 1);

        chatbotViewModel.getBotMessage().observe(getViewLifecycleOwner(), new Observer<Pair<String, List<Place>>>() {
            @Override
            public void onChanged(Pair<String, List<Place>> stringListPair) {
                new Handler().postDelayed(() -> {
                    chatList.add(new ChatMessage(stringListPair.first, stringListPair.second));
                    adapter.notifyItemInserted(chatList.size() - 1);
                    rv_chat.smoothScrollToPosition(chatList.size() - 1);
                }, 500);
            }
        });

        btn_send.setOnClickListener(v -> {
            String text = edt_chat_input.getText().toString().trim();
            if (!text.isEmpty()) {
                // Tin nhắn của User
                chatList.add(new ChatMessage(text, true));
                adapter.notifyItemInserted(chatList.size() - 1);

                rv_chat.scrollToPosition(chatList.size() - 1);
                edt_chat_input.setText("");

                // Bot trả lời
                chatbotViewModel.sendMessageToAi(text);
            }
        });
    }

}