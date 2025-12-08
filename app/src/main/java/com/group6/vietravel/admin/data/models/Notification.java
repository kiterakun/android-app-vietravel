package com.group6.vietravel.admin.data.models; // ⚠️ Package đã sửa

import com.google.firebase.firestore.DocumentId;

public class Notification {

    //Dùng @DocumentId là lúc lấy từ firestore nó tự ánh xạ id qua nha
    @DocumentId
    private String id;
    private String title;
    private String message;
    private long timestamp;

    public Notification() {}

    public Notification(String title, String message, long timestamp) {
        this.title = title;
        this.message = message;
        this.timestamp = timestamp;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}