package com.group6.vietravel.admin.data.models;

import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.PropertyName;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class Notification {
    
    @DocumentId
    private String notificationId;
    
    private String title;
    private String message;
    
    @PropertyName("target_type")
    private String targetType; // "all", "specific"
    
    @PropertyName("target_user_ids")
    private String targetUserIds; // comma-separated user IDs for specific
    
    @PropertyName("sent_by")
    private String sentBy; // admin user ID
    
    @PropertyName("sent_at")
    @ServerTimestamp
    private Date sentAt;
    
    private String status; // "sent", "scheduled", "failed"

    public Notification() {
    }

    public Notification(String title, String message, String targetType, String targetUserIds, String sentBy) {
        this.title = title;
        this.message = message;
        this.targetType = targetType;
        this.targetUserIds = targetUserIds;
        this.sentBy = sentBy;
        this.status = "sent";
    }

    // Getters and Setters
    public String getNotificationId() { return notificationId; }
    public void setNotificationId(String notificationId) { this.notificationId = notificationId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getTargetType() { return targetType; }
    public void setTargetType(String targetType) { this.targetType = targetType; }

    public String getTargetUserIds() { return targetUserIds; }
    public void setTargetUserIds(String targetUserIds) { this.targetUserIds = targetUserIds; }

    public String getSentBy() { return sentBy; }
    public void setSentBy(String sentBy) { this.sentBy = sentBy; }

    public Date getSentAt() { return sentAt; }
    public void setSentAt(Date sentAt) { this.sentAt = sentAt; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
