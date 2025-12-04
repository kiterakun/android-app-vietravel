package com.group6.vietravel.utils;

import android.util.Log;

import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.ai.client.generativeai.type.GenerationConfig;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.gson.Gson;
import com.group6.vietravel.data.models.AiResponse;
import com.group6.vietravel.data.models.Place;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class GeminiUtils {

    private static final String TAG = "GeminiUtils";
    private static final String API_KEY = "AIzaSyDEtE45fTSCCNZNRo2-PG3WiRa9evA_jUU";

    private final GenerativeModelFutures model;
    private final Gson gson;
    private final Executor executor;

    public interface AiCallback {
        void onSuccess(AiResponse response);
        void onError(Throwable t);
    }

    public GeminiUtils() {
        // Cấu hình để AI trả về JSON
        GenerationConfig.Builder configBuilder = new GenerationConfig.Builder();
        configBuilder.responseMimeType = "application/json";

        GenerativeModel gm = new GenerativeModel(
                "gemini-2.5-flash-lite",
                API_KEY,
                configBuilder.build()
        );

        this.model = GenerativeModelFutures.from(gm);
        this.gson = new Gson();
        this.executor = Executors.newSingleThreadExecutor();
    }


    public void getRecommendation(String userQuery, List<Place> allPlaces, AiCallback callback) {

        StringBuilder placesContext = new StringBuilder();
        placesContext.append("Danh sách các địa điểm du lịch có trong hệ thống:\n");

        for (Place p : allPlaces) {
            placesContext.append("- ")
                    .append(p.getName())
                    .append(" (")
                    .append(p.getCategoryId())
                    .append(", ")
                    .append(p.getAddress())
                    .append(")")
                    .append("\n");
        }

        // Tạo Prompt
        String prompt = String.format(
                "Bạn là một trợ lý du lịch ảo thông minh của VieTravel.\n" +
                        "%s\n" +
                        "Người dùng hỏi: \"%s\"\n\n" +
                        "Yêu cầu:\n" +
                        "1. Trả lời thân thiện, ngắn gọn bằng tiếng Việt.\n" +
                        "2. Dựa vào câu hỏi, hãy chọn ra các địa điểm phù hợp NHẤT từ danh sách trên.\n" +
                        "3. CHỈ TRẢ VỀ JSON theo định dạng sau (không thêm markdown):\n" +
                        "{\n" +
                        "  \"message\": \"Câu trả lời của bạn cho người dùng...\",\n" +
                        "  \"recommended_places\": [\"Tên chính xác địa điểm 1\", \"Tên chính xác địa điểm 2\"]\n" +
                        "}",
                placesContext.toString(),
                userQuery
        );

        // Gửi yêu cầu
        Content content = new Content.Builder().addText(prompt).build();
        ListenableFuture<GenerateContentResponse> response = model.generateContent(content);

        Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(GenerateContentResponse result) {
                String jsonText = result.getText();
                Log.d(TAG, "AI Raw Response: " + jsonText);

                try {
                    // Parse chuỗi JSON thành Object Java
                    AiResponse aiResponse = gson.fromJson(jsonText, AiResponse.class);
                    callback.onSuccess(aiResponse);
                } catch (Exception e) {
                    Log.e(TAG, "Lỗi Parse JSON", e);
                    callback.onError(e);
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e(TAG, "Lỗi gọi API", t);
                callback.onError(t);
            }
        }, executor);
    }
}