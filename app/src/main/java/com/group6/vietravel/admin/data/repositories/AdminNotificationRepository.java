package com.group6.vietravel.admin.data.repositories;

import static android.content.ContentValues.TAG;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.google.firebase.firestore.DocumentSnapshot; // Import mới
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.group6.vietravel.admin.data.models.Notification;
import java.util.ArrayList;
import java.util.List;

public class AdminNotificationRepository {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final MutableLiveData<List<Notification>> notificationsLiveData = new MutableLiveData<>();
    private static AdminNotificationRepository instance;

    public static synchronized AdminNotificationRepository getInstance() {
        if (instance == null) instance = new AdminNotificationRepository();
        return instance;
    }

    public LiveData<List<Notification>> getNotifications() {
        db.collection("notifications")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null) return;

                    if (value != null) {
                        List<Notification> list = value.toObjects(Notification.class);
                        notificationsLiveData.postValue(list);
                        Log.d(TAG, "Đã tải " + list.size() + " thông báo.");
                    }
                    else notificationsLiveData.postValue(new ArrayList<>());
                });
        return notificationsLiveData;
    }

    public void sendNotification(Notification notification, OnComplete callback) {
        db.collection("notifications").add(notification)
                .addOnSuccessListener(doc -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    // --- THÊM MỚI: Hàm xóa thông báo ---
    public void deleteNotification(String notificationId, OnComplete callback) {
        db.collection("notifications").document(notificationId)
                .delete()
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    public interface OnComplete {
        void onSuccess();
        void onFailure(String error);
    }
}