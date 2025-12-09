package com.group6.vietravel.features.user.ui.main.chatbot;

import android.app.Application;
import android.util.Log;
import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.group6.vietravel.core.utils.GeminiUtils;
import com.group6.vietravel.core.utils.GroqUtils;
import com.group6.vietravel.data.models.ai.AiResponse;

import com.group6.vietravel.data.models.place.Place;
import com.group6.vietravel.data.models.user.User;
import com.group6.vietravel.data.repositories.auth.AuthRepository;
import com.group6.vietravel.data.repositories.place.PlaceRepository;

import java.util.ArrayList;
import java.util.List;

public class ChatbotViewModel extends AndroidViewModel {

    private final GeminiUtils geminiUtils;
    private final GroqUtils groqUtils;
    private final PlaceRepository placeRepository;
    private final AuthRepository authRepository;
    private List<Place> cachedPlaces = new ArrayList<>();

    private final MutableLiveData<Pair<String,List<Place>>> botMessageLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);

    public ChatbotViewModel(@NonNull Application application) {
        super(application);
        geminiUtils = new GeminiUtils();
        groqUtils = new GroqUtils();
        placeRepository = PlaceRepository.getInstance(application.getApplicationContext());
        authRepository = AuthRepository.getInstance();

        preloadPlaces();
    }

    private void preloadPlaces() {
        placeRepository.getAllPlaces().observeForever(places -> {
            if (places != null) {
                cachedPlaces = places;
            }
        });
    }

    public void sendMessageToAi(String userText) {
        isLoading.postValue(true);

        botMessageLiveData.postValue(new Pair<>("Đang tải, đợi xíu nhé...",new ArrayList<>()));

//        groqUtils.getRecommendation(userText, cachedPlaces, new GroqUtils.AiCallback()
//        geminiUtils.getRecommendation(userText, cachedPlaces, new GeminiUtils.AiCallback()

        groqUtils.getRecommendation(userText, cachedPlaces, new GroqUtils.AiCallback() {
            @Override
            public void onSuccess(AiResponse response) {
                isLoading.postValue(false);

                // AI trả về List<String>, nên cần map ngược lại thành List<Place>
                List<String> suggestedNames = response.getRecommendedPlaces();
                List<Place> finalRecommendations = new ArrayList<>();

                if (suggestedNames != null) {
                    for (String name : suggestedNames) {
                        for (Place p : cachedPlaces) {
                            if (p.getName().equalsIgnoreCase(name)) {
                                finalRecommendations.add(p);
                                break;
                            }
                        }
                    }
                }
                botMessageLiveData.postValue(new Pair<>(response.getMessage(),finalRecommendations));
            }

            @Override
            public void onError(Throwable t) {
                isLoading.postValue(false);

                if (t.getMessage() != null && t.getMessage().contains("429")) {
                    botMessageLiveData.postValue(
                            new Pair<>("Hệ thống đang quá tải, vui lòng thử lại sau 1 phút nhé!",new ArrayList<>()));
                } else {
                    botMessageLiveData.postValue(new Pair<>("Xin lỗi, tôi đang gặp sự cố kết nối: " + t.getMessage(),new ArrayList<>()));
                }

                // In log để debug
                Log.e("ChatViewModel", "AI Error", t);
            }
        });
    }

    public LiveData<Pair<String,List<Place>>> getBotMessage() { return botMessageLiveData; }
    public void setBotMessage(Pair<String,List<Place>> message) { botMessageLiveData.setValue(message); }
    public LiveData<User> getCurrentUser(){return authRepository.getUserProfileLiveData();}
}