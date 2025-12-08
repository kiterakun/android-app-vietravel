package com.group6.vietravel.features.user.ui.main.account;

import android.app.Application;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.group6.vietravel.data.models.notification.Notification;
import com.group6.vietravel.data.models.user.User;
import com.group6.vietravel.data.repositories.auth.AuthRepository;
import com.group6.vietravel.data.repositories.notification.NotificationRepository;
import com.group6.vietravel.features.ui.auth.LoginActivity;

import java.util.List;

public class AccountViewModel extends ViewModel {
    private final AuthRepository authRepository;

    private NotificationRepository notificationRepository;

    public AccountViewModel(){
        authRepository = AuthRepository.getInstance();

        notificationRepository = NotificationRepository.getInstance();

        notificationRepository.fetchNotifications();
    }


    public LiveData<List<Notification>> getNotifications() {
        return notificationRepository.getNotificationsLiveData();
    }

    public void refreshNotifications() {
        notificationRepository.fetchNotifications();
    }

    public LiveData<User> getUserProfile() {
        return authRepository.getUserProfileLiveData();
    }
    public void setUserProfile(Uri uri, String username, String email){
        authRepository.updateUserProfile(uri, username, email);
    }
    public void logout(){
        FirebaseAuth.getInstance().signOut();
    }

    public void changePass(String currPassword,String newPassword){
        authRepository.changePassword(currPassword, newPassword);
    }
}