package com.group6.vietravel.admin.ui.notifications;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import com.group6.vietravel.admin.data.models.Notification;
import com.group6.vietravel.admin.data.repositories.AdminNotificationRepository;
import java.util.List;

public class NotificationViewModel extends ViewModel {
    private final AdminNotificationRepository repository;
    public NotificationViewModel() { repository = AdminNotificationRepository.getInstance(); }
    public LiveData<List<Notification>> getNotifications() { return repository.getNotifications(); }
}