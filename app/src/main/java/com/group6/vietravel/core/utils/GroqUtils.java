package com.group6.vietravel.core.utils;

import android.util.Log;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.gson.Gson;
import com.group6.vietravel.data.models.ai.AiResponse;
import com.group6.vietravel.data.models.place.Place;
import com.group6.vietravel.data.models.review.Review;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class GroqUtils {

    private static final String TAG = "GroqUtils";
    private static final String API_KEY = "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";
    private static final String BASE_URL = "https://api.groq.com/openai/v1/chat/completions";

    //llama-3.1-8b-instant nhanh hơn =))
    private static final String MODEL_ID = "openai/gpt-oss-120b";

    private final OkHttpClient client;
    private final Gson gson;

    public interface AiCallback {
        void onSuccess(AiResponse response);
        void onError(Throwable t);
    }

    public interface AiCallbackReview {
        void onSuccess(String response);
        void onError(Throwable t);
    }

    public GroqUtils() {
        this.gson = new Gson();
        this.client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();
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

        String systemPrompt = String.format("Bạn là trợ lý du lịch ảo của VieTravel. " +
                "QUAN TRỌNG: Bạn BẮT BUỘC phải trả về định dạng JSON thuần túy, không có markdown (```json). " +
                "Cấu trúc JSON: { \"message\": \"Câu trả lời của bạn cho người dùng...\", \"recommended_places\": [\"Tên chính xác địa điểm 1\", \"Tên chính xác địa điểm 1\"] }"
        );

        String userContent = String.format(
                "\n%s\nNgười dùng hỏi: \"%s\"\n\n"+
                    "YÊU CẦU:\n" +
                    "1. Trả lời thân thiện, đầy đủ và chi tiết bằng tiếng Việt.\n" +
                    "2. Dựa vào câu hỏi, hãy chọn ra các địa điểm phù hợp NHẤT từ danh sách trên điền vào recommended_places theo thứ tự ưu tiên và hãy tìm kiếm thông tin để trả lời cho người dùng biết về đặc trưng của 3 địa điểm đầu.\n" +
                    "3. Nếu không tìm thấy địa điểm phù hợp hãy xin lỗi và tự tìm kiếm địa điểm để trả lời người dùng lúc này recommended_places sẽ để rỗng.\n"
                ,placesContext.toString(),userQuery
        );

        callGroqApi(systemPrompt, userContent, new ApiInternalCallback() {
            @Override
            public void onResponse(String jsonResponseString) {
                try {
                    // Parse chuỗi JSON từ AI thành Object AiResponse
                    AiResponse aiResponse = gson.fromJson(jsonResponseString, AiResponse.class);
                    callback.onSuccess(aiResponse);
                } catch (Exception e) {
                    Log.e(TAG, "Lỗi Parse JSON từ Groq: " + jsonResponseString, e);
                    callback.onError(e);
                }
            }

            @Override
            public void onFailure(Throwable t) {
                callback.onError(t);
            }
        });
    }

    public void getReview(Place place, AiCallbackReview callback) {
        FirebaseFirestore.getInstance().collection("reviews")
                .whereEqualTo("status", "approved")
                .whereEqualTo("place_id", place.getPlaceId())
                .orderBy("created_at", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener((value) -> {
                    List<Review> reviewList = new ArrayList<>();
                    if (value != null) {
                        reviewList = value.toObjects(Review.class);

                        StringBuilder reviewsContext = new StringBuilder();
                        reviewsContext.append("Danh sách các bình luận của ").append(place.getName()).append(":\n");

                        if (!reviewList.isEmpty()) {
                            for (Review r : reviewList) {
                                reviewsContext.append("- ").append(r.getComment()).append("\n");
                            }
                        } else {
                            reviewsContext.append("Không có bình luận nào");
                        }

                        // System Prompt
                        String systemPrompt = "Bạn là một trợ lý đánh giá các địa điểm của VieTravel.\n" +
                                "%s\n" +
                                "YÊU CẦU:\n" +
                                "1. Trả lời thân thiện và khoảng 30 chữ, bằng tiếng Việt.\n" +
                                "2. Hãy tổng hợp từ danh sách trên và tìm kiếm thông tin về địa điểm" + place.getName() + "sau đó đưa ra đánh giá phù hợp NHẤT cho địa điểm và một chút thông tin nổi bật của địa điểm này.\n" +
                                "QUAN TRỌNG: Trả về JSON thuần túy: { \"message\": \"...\" }";

                        callGroqApi(systemPrompt, reviewsContext.toString(), new ApiInternalCallback() {
                            @Override
                            public void onResponse(String jsonResponseString) {
                                try {
                                    AiResponse aiResponse = gson.fromJson(jsonResponseString, AiResponse.class);
                                    callback.onSuccess(aiResponse.getMessage());
                                } catch (Exception e) {
                                    Log.e(TAG, "Lỗi Parse JSON Review", e);
                                    callback.onError(e);
                                }
                            }

                            @Override
                            public void onFailure(Throwable t) {
                                callback.onError(t);
                            }
                        });
                    }
                })
                .addOnFailureListener(e -> {
                    Log.v(TAG, "Không fetch được review", e);
                    callback.onError(e);
                });
    }

    // Helper Methods để gọi API

    private interface ApiInternalCallback {
        void onResponse(String content);
        void onFailure(Throwable t);
    }

    private void callGroqApi(String systemPrompt, String userContent, ApiInternalCallback internalCallback) {
        try {
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("model", MODEL_ID);

            JSONObject responseFormat = new JSONObject();
            responseFormat.put("type", "json_object");
            jsonBody.put("response_format", responseFormat);

            JSONArray messages = new JSONArray();

            // Message System
            JSONObject sysMsg = new JSONObject();
            sysMsg.put("role", "system");
            sysMsg.put("content", systemPrompt);
            messages.put(sysMsg);

            // Message User
            JSONObject userMsg = new JSONObject();
            userMsg.put("role", "user");
            userMsg.put("content", userContent);
            messages.put(userMsg);

            jsonBody.put("messages", messages);

            RequestBody body = RequestBody.create(
                    jsonBody.toString(),
                    MediaType.get("application/json; charset=utf-8")
            );

            Request request = new Request.Builder()
                    .url(BASE_URL)
                    .addHeader("Authorization", "Bearer " + API_KEY)
                    .addHeader("Content-Type", "application/json")
                    .post(body)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e(TAG, "Lỗi gọi Groq API", e);
                    internalCallback.onFailure(e);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful() && response.body() != null) {
                        String responseBody = response.body().string();
                        Log.d(TAG, "Groq Raw Response: " + responseBody);
                        try {
                            // Parse format của OpenAI/Groq để lấy nội dung text thực sự
                            JSONObject jsonResponse = new JSONObject(responseBody);
                            JSONArray choices = jsonResponse.getJSONArray("choices");
                            if (choices.length() > 0) {
                                String content = choices.getJSONObject(0)
                                        .getJSONObject("message")
                                        .getString("content");
                                internalCallback.onResponse(content);
                            } else {
                                internalCallback.onFailure(new Exception("Empty choices from API"));
                            }
                        } catch (Exception e) {
                            internalCallback.onFailure(e);
                        }
                    } else {
                        String errorBody = response.body() != null ? response.body().string() : "No error body";
                        Log.e(TAG, "Groq Error Code: " + response.code());
                        Log.e(TAG, "Groq Error Body: " + errorBody); // <--- Quan trọng: Xem Logcat dòng này

                        internalCallback.onFailure(new IOException("API Error: " + response.code() + " | " + errorBody));
                    }
                }
            });

        } catch (Exception e) {
            internalCallback.onFailure(e);
        }
    }
}