package com.group6.vietravel.data.models.ai;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class AiResponse {

    @SerializedName("message")
    private String message;

    @SerializedName("recommended_places")
    private List<String> recommendedPlaces;

    public String getMessage() { return message; }
    public List<String> getRecommendedPlaces() { return recommendedPlaces; }
}
