package com.group6.vietravel.admin.data.repositories;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.group6.vietravel.admin.data.models.Notification;

import java.util.List;

public class AdminNotificationRepository {
    
    private static final String TAG = "AdminNotificationRepo";
    private static AdminNotificationRepository instance;
    
    private final FirebaseFirestore db;
    private final FirebaseAuth auth;
    private final MutableLiveData<List<Notification>> notificationHistoryLiveData;
    private final MutableLiveData<Boolean> sendSuccessLiveData;
    private final MutableLiveData<String> errorLiveData;
    
    private AdminNotificationRepository() {
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        notificationHistoryLiveData = new MutableLiveData<>();
        sendSuccessLiveData = new MutableLiveData<>();
        errorLiveData = new MutableLiveData<>();
    }
    
    public static synchronized AdminNotificationRepository getInstance() {
        if (instance == null) {
            instance = new AdminNotificationRepository();
        }
        return instance;
    }
    
    public LiveData<List<Notification>> getNotificationHistory() {
        return notificationHistoryLiveData;
    }
    
    public LiveData<Boolean> getSendSuccess() {
        return sendSuccessLiveData;
    }
    
    public LiveData<String> getError() {
        return errorLiveData;
    }
    
    // Fetch notification history
    public void fetchNotificationHistory() {
        db.collection("notifications")
            .orderBy("sent_at", Query.Direction.DESCENDING)
            .addSnapshotListener((value, error) -> {
                if (error != null) {
                    Log.w(TAG, "Error fetching notifications", error);
                    return;
                }
                if (value != null) {
                    List<Notification> notifications = value.toObjects(Notification.class);
                    notificationHistoryLiveData.postValue(notifications);
                }
            });
    }
    
    // Send notification
    public void sendNotification(Notification notification) {
        if (auth.getCurrentUser() == null) {
            errorLiveData.postValue("Không xác định được admin");
            return;
        }
        
        notification.setSentBy(auth.getCurrentUser().getUid());
        notification.setStatus("sent");
        
        db.collection("notifications")
            .add(notification)
            .addOnSuccessListener(documentReference -> {
                Log.d(TAG, "Notification sent successfully");
                
                // TODO: Integrate Firebase Cloud Messaging (FCM) here
                // Send push notification to target users
                
                sendSuccessLiveData.postValue(true);
            })
            .addOnFailureListener(e -> {
                Log.e(TAG, "Error sending notification", e);
                errorLiveData.postValue("Lỗi gửi thông báo: " + e.getMessage());
            });
    }
    
    // Delete notification from history
    public void deleteNotification(String notificationId) {
        db.collection("notifications").document(notificationId)
            .delete()
            .addOnSuccessListener(aVoid -> Log.d(TAG, "Notification deleted"))
            .addOnFailureListener(e -> Log.e(TAG, "Error deleting notification", e));
    }
}
