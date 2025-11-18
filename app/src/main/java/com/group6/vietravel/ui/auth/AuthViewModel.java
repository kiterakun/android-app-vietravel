package com.group6.vietravel.ui.auth;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.google.firebase.auth.FirebaseUser;
import com.group6.vietravel.models.User;

import com.group6.vietravel.data.repositorys.AuthRepository;

public class AuthViewModel extends AndroidViewModel {

    private final AuthRepository authRepository;
    private final LiveData<FirebaseUser> userLiveData;
    private final LiveData<String> errorLiveData;
    private final LiveData<User> userProfileLiveData;

    public AuthViewModel(@NonNull Application application) {
        super(application);
        authRepository = AuthRepository.getInstance(application);

        userLiveData = authRepository.getUserLiveData();
        errorLiveData = authRepository.getErrorLiveData();
        userProfileLiveData = authRepository.getUserProfileLiveData();
    }


    public void register(String username, String email, String password) {
        authRepository.register(username, email, password);
    }

    public void login(String email, String password) {
        authRepository.login(email, password);
    }

    public LiveData<User> getUserProfileLiveData() {
        return userProfileLiveData;
    }

    public LiveData<FirebaseUser> getUserLiveData() {
        return userLiveData;
    }
    public LiveData<String> getErrorLiveData() {
        return errorLiveData;
    }
}