package com.group6.vietravel.data.models;

import java.util.List;

public class ChatMessage {
    private String content;
    private boolean isUser;
    private List<Place> recommendedPlaces;
    private String timestamp;
    public ChatMessage(String content, boolean isUser) {
        this.content = content;
        this.isUser = isUser;
        this.recommendedPlaces = null;
        timestamp = new java.text.SimpleDateFormat("HH:mm").format(new java.util.Date());
    }

    public ChatMessage(String content, List<Place> places) {
        this.content = content;
        this.isUser = false;
        this.recommendedPlaces = places;
        timestamp = new java.text.SimpleDateFormat("HH:mm").format(new java.util.Date());
    }

    public List<Place> getRecommendedPlaces() { return recommendedPlaces; }

    public String getContent() { return content; }
    public boolean isUser() { return isUser; }
    public String getTimestamp() { return timestamp; }
}
