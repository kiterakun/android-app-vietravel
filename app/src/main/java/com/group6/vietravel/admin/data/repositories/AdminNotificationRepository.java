package com.group6.vietravel.admin.data.repositories;

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
                    List<Notification> list = new ArrayList<>();
                    if (value != null) {
                        // SỬA ĐỔI: Duyệt từng document để lấy ID
                        for (DocumentSnapshot doc : value.getDocuments()) {
                            Notification n = doc.toObject(Notification.class);
                            if (n != null) {
                                n.setId(doc.getId()); // Lưu ID document vào model
                                list.add(n);
                            }
                        }
                    }
                    notificationsLiveData.setValue(list);
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