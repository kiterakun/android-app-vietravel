package com.group6.vietravel.data.models.notification;

import com.google.firebase.firestore.DocumentId;


public class Notification {

    @DocumentId
    private String notificationId;

    private String title;
    private String message;

    private long timestamp;

    public Notification() {
    }

    public Notification(String title, String message, String target) {
        this.title = title;
        this.message = message;
    }

    public String getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(String notificationId) {
        this.notificationId = notificationId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}