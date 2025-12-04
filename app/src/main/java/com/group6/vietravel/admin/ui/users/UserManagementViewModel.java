package com.group6.vietravel.admin.ui.users;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.group6.vietravel.admin.data.repositories.AdminUserRepository;
import com.group6.vietravel.data.models.user.User;

import java.util.List;

public class UserManagementViewModel extends AndroidViewModel {
    
    private final AdminUserRepository repository;
    
    public UserManagementViewModel(@NonNull Application application) {
        super(application);
        repository = AdminUserRepository.getInstance();
    }
    
    public LiveData<List<User>> getAllUsers() {
        return repository.getAllUsers();
    }
    
    public LiveData<Boolean> getOperationSuccess() {
        return repository.getOperationSuccess();
    }
    
    public LiveData<String> getError() {
        return repository.getError();
    }
    
    public void loadAllUsers() {
        repository.fetchAllUsers();
    }
    
    public void lockUser(String userId) {
        repository.lockUser(userId);
    }
    
    public void unlockUser(String userId) {
        repository.unlockUser(userId);
    }
    
    public void hideUser(String userId) {
        repository.hideUser(userId);
    }
    
    public void restoreUser(String userId) {
        repository.restoreUser(userId);
    }
    
    public void deleteUser(String userId) {
        repository.deleteUser(userId);
    }
    
    public void updateUserPoints(String userId, long points) {
        repository.updateUserPoints(userId, points);
    }
}
