package com.group6.vietravel.core.utils;

import android.util.Log;

import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.ai.client.generativeai.type.GenerationConfig;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.gson.Gson;
import com.group6.vietravel.data.models.ai.AiResponse;
import com.group6.vietravel.data.models.place.Place;
import com.group6.vietravel.data.models.review.Review;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class GeminiUtils {

    private static final String TAG = "GeminiUtils";
    private static final String API_KEY = "your_api_key_here";

    private final GenerativeModelFutures model;
    private final Gson gson;
    private final Executor executor;

    public interface AiCallback {
        void onSuccess(AiResponse response);
        void onError(Throwable t);
    }

    public interface AiCallbackReview {
        void onSuccess(String response);
        void onError(Throwable t);
    }

    public GeminiUtils() {
        // Cấu hình để AI trả về JSON
        GenerationConfig.Builder configBuilder = new GenerationConfig.Builder();
        configBuilder.responseMimeType = "application/json";

        GenerativeModel gm = new GenerativeModel(
                "gemini-2.5-flash",
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
                        "1. Trả lời thân thiện, đầy đủ và chi tiết bằng tiếng Việt.\n" +
                        "2. Dựa vào câu hỏi, hãy chọn ra các địa điểm phù hợp NHẤT từ danh sách trên điền vào recommended_places theo thứ tự ưu tiên và hãy tìm kiếm thông tin để trả lời cho người dùng biết về đặc trưng của 3 địa điểm đầu.\n" +
                        "3. Nếu không tìm thấy địa điểm phù hợp hãy xin lỗi và tự tìm kiếm địa điểm để trả lời người dùng lúc này recommended_places sẽ để rỗng.\n" +
                        "4. CHỈ TRẢ VỀ JSON theo định dạng sau (không thêm markdown):\n" +
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



    public void getReview(Place place,AiCallbackReview callback) {
        FirebaseFirestore.getInstance().collection("reviews").whereEqualTo("status", "approved")
                .whereEqualTo("place_id",place.getPlaceId())
                .orderBy("created_at", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener((value) -> {
                    List<Review> reviewList = new ArrayList<>();
                    if(value!=null){
                        reviewList = value.toObjects(Review.class);

                        StringBuilder reviewsContext = new StringBuilder();
                        reviewsContext.append("Danh sách các bình luận của "+place.getName()+":\n");

                        if(!reviewList.isEmpty()) {
                            for (Review r : reviewList) {
                                reviewsContext.append("- ")
                                        .append(r.getComment())
                                        .append("\n");
                            }
                        }
                        else {
                            reviewsContext.append("Không có bình luận nào");
                        }

                        // Tạo Prompt
                        String prompt = String.format(
                                "Bạn là một trợ lý đánh giá các địa điểm của VieTravel.\n" +
                                        "%s\n" +
                                        "Yêu cầu:\n" +
                                        "1. Trả lời thân thiện và ngắn gọn khoảng 30 chữ, bằng tiếng Việt.\n" +
                                        "2. Hãy tìm kiếm thông tin về địa điểm và tổng hợp từ danh sách trên đưa ra đánh giá phù hợp NHẤT cho địa điểm.\n" +
                                        "3. CHỈ TRẢ VỀ JSON theo định dạng sau (không thêm markdown).\n" +
                                        "{\n" +
                                        "  \"message\": \"Câu trả lời của bạn cho người dùng...\",\n" +
                                        "}",
                                reviewsContext.toString()
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
                                    AiResponse aiResponse = gson.fromJson(jsonText, AiResponse.class);
                                    callback.onSuccess(aiResponse.getMessage());
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

                }).addOnFailureListener(e -> {
                   Log.v(TAG,"Khong fetch dc",e);
                });
    }
}
