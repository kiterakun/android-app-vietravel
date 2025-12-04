package com.group6.vietravel.admin.ui.users;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.group6.vietravel.admin.data.repositories.AdminUserRepository;
import com.group6.vietravel.data.models.user.User;

import java.util.ArrayList;
import java.util.List;

public class UserManagementViewModel extends AndroidViewModel {
    
    private final AdminUserRepository repository;
    private final MutableLiveData<List<User>> filteredUsersLiveData = new MutableLiveData<>();
    
    private List<User> allUsers = new ArrayList<>();
    private String currentQuery = "";
    private String currentFilter = "All";
    
    public UserManagementViewModel(@NonNull Application application) {
        super(application);
        repository = AdminUserRepository.getInstance();
        
        // Observe repository data
        repository.getAllUsers().observeForever(users -> {
            if (users != null) {
                allUsers = users;
                applyFilters();
            }
        });
    }
    
    public LiveData<List<User>> getUsers() {
        return filteredUsersLiveData;
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
    
    public void searchUsers(String query) {
        currentQuery = query;
        applyFilters();
    }
    
    public void filterUsers(String status) {
        currentFilter = status;
        applyFilters();
    }
    
    private void applyFilters() {
        List<User> filteredList = new ArrayList<>();
        for (User user : allUsers) {
            boolean matchesQuery = currentQuery.isEmpty() ||
                    (user.getUsername() != null && user.getUsername().toLowerCase().contains(currentQuery.toLowerCase())) ||
                    (user.getEmail() != null && user.getEmail().toLowerCase().contains(currentQuery.toLowerCase()));

            boolean matchesFilter = "All".equals(currentFilter) || "Tất cả".equals(currentFilter) ||
                    (user.getStatus() != null && user.getStatus().equalsIgnoreCase(currentFilter));

            if (matchesQuery && matchesFilter) {
                filteredList.add(user);
            }
        }
        filteredUsersLiveData.postValue(filteredList);
    }
    
    public void lockUser(User user) {
        repository.lockUser(user.getUid());
    }
    
    public void unlockUser(User user) {
        repository.unlockUser(user.getUid());
    }
    
    public void hideUser(User user) {
        repository.hideUser(user.getUid());
    }
    
    public void restoreUser(User user) {
        repository.restoreUser(user.getUid());
    }
    
    public void deleteUser(User user) {
        repository.deleteUser(user.getUid());
    }
    
    public void updateUserPoints(User user, long points) {
        repository.updateUserPoints(user.getUid(), points);
    }
}
