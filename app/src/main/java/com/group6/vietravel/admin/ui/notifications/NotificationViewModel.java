package com.group6.vietravel.admin.ui.notifications;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.group6.vietravel.admin.data.models.Notification;
import com.group6.vietravel.admin.data.repositories.AdminNotificationRepository;

import java.util.List;

public class NotificationViewModel extends AndroidViewModel {
    
    private final AdminNotificationRepository repository;
    
    public NotificationViewModel(@NonNull Application application) {
        super(application);
        repository = AdminNotificationRepository.getInstance();
    }
    
    public LiveData<List<Notification>> getNotificationHistory() {
        return repository.getNotificationHistory();
    }
    
    public LiveData<Boolean> getSendSuccess() {
        return repository.getSendSuccess();
    }
    
    public LiveData<String> getError() {
        return repository.getError();
    }
    
    public void loadHistory() {
        repository.fetchNotificationHistory();
    }
    
    public void sendNotification(Notification notification) {
        repository.sendNotification(notification);
    }
    
    public void deleteNotification(String notificationId) {
        repository.deleteNotification(notificationId);
    }
}
