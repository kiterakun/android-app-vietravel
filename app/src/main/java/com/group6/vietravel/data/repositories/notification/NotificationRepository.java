package com.group6.vietravel.data.repositories.notification;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.group6.vietravel.data.models.notification.Notification;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NotificationRepository {

    private static final String TAG = "NotificationRepo";
    private static NotificationRepository instance;

    private final FirebaseFirestore db;

    private final MutableLiveData<List<Notification>> notificationsLiveData;

    private NotificationRepository() {
        db = FirebaseFirestore.getInstance();
        notificationsLiveData = new MutableLiveData<>();
    }

    public static synchronized NotificationRepository getInstance() {
        if (instance == null) {
            instance = new NotificationRepository();
        }
        return instance;
    }

    public MutableLiveData<List<Notification>> getNotificationsLiveData() {
        return notificationsLiveData;
    }

    public void fetchNotifications() {
        db.collection("notifications")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.e(TAG, "Lỗi lấy thông báo: ", error);
                        notificationsLiveData.postValue(new ArrayList<>());
                        return;
                    }

                    if (value != null) {
                        List<Notification> notificationList = value.toObjects(Notification.class);
                        notificationsLiveData.postValue(notificationList);
                        Log.d(TAG, "Đã tải " + notificationList.size() + " thông báo.");
                    } else {
                        notificationsLiveData.postValue(new ArrayList<>());
                    }
                });
    }
}
