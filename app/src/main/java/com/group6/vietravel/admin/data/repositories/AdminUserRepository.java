package com.group6.vietravel.admin.data.repositories;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.group6.vietravel.data.models.User;

import java.util.List;

public class AdminUserRepository {
    
    private static final String TAG = "AdminUserRepository";
    private static AdminUserRepository instance;
    
    private final FirebaseFirestore db;
    private final MutableLiveData<List<User>> allUsersLiveData;
    private final MutableLiveData<Boolean> operationSuccessLiveData;
    private final MutableLiveData<String> errorLiveData;
    
    private AdminUserRepository() {
        db = FirebaseFirestore.getInstance();
        allUsersLiveData = new MutableLiveData<>();
        operationSuccessLiveData = new MutableLiveData<>();
        errorLiveData = new MutableLiveData<>();
    }
    
    public static synchronized AdminUserRepository getInstance() {
        if (instance == null) {
            instance = new AdminUserRepository();
        }
        return instance;
    }
    
    public LiveData<List<User>> getAllUsers() {
        return allUsersLiveData;
    }
    
    public LiveData<Boolean> getOperationSuccess() {
        return operationSuccessLiveData;
    }
    
    public LiveData<String> getError() {
        return errorLiveData;
    }
    
    // Fetch all users
    public void fetchAllUsers() {
        db.collection("users")
            .orderBy("created_at", Query.Direction.DESCENDING)
            .addSnapshotListener((value, error) -> {
                if (error != null) {
                    Log.w(TAG, "Error fetching users", error);
                    errorLiveData.postValue("Lỗi tải danh sách người dùng");
                    return;
                }
                if (value != null) {
                    List<User> users = value.toObjects(User.class);
                    allUsersLiveData.postValue(users);
                }
            });
    }
    
    // Lock user account
    public void lockUser(String userId) {
        db.collection("users").document(userId)
            .update("status", "locked")
            .addOnSuccessListener(aVoid -> {
                Log.d(TAG, "User locked");
                operationSuccessLiveData.postValue(true);
            })
            .addOnFailureListener(e -> {
                Log.e(TAG, "Error locking user", e);
                errorLiveData.postValue("Lỗi khóa tài khoản");
            });
    }
    
    // Unlock user account
    public void unlockUser(String userId) {
        db.collection("users").document(userId)
            .update("status", "active")
            .addOnSuccessListener(aVoid -> {
                Log.d(TAG, "User unlocked");
                operationSuccessLiveData.postValue(true);
            })
            .addOnFailureListener(e -> {
                Log.e(TAG, "Error unlocking user", e);
                errorLiveData.postValue("Lỗi mở khóa tài khoản");
            });
    }
    
    // Hide user (soft delete)
    public void hideUser(String userId) {
        db.collection("users").document(userId)
            .update("status", "hidden")
            .addOnSuccessListener(aVoid -> {
                Log.d(TAG, "User hidden");
                operationSuccessLiveData.postValue(true);
            })
            .addOnFailureListener(e -> {
                Log.e(TAG, "Error hiding user", e);
                errorLiveData.postValue("Lỗi ẩn tài khoản");
            });
    }
    
    // Restore hidden user
    public void restoreUser(String userId) {
        db.collection("users").document(userId)
            .update("status", "active")
            .addOnSuccessListener(aVoid -> {
                Log.d(TAG, "User restored");
                operationSuccessLiveData.postValue(true);
            })
            .addOnFailureListener(e -> {
                Log.e(TAG, "Error restoring user", e);
                errorLiveData.postValue("Lỗi khôi phục tài khoản");
            });
    }
    
    // Delete user permanently
    public void deleteUser(String userId) {
        db.collection("users").document(userId)
            .delete()
            .addOnSuccessListener(aVoid -> {
                Log.d(TAG, "User deleted");
                operationSuccessLiveData.postValue(true);
            })
            .addOnFailureListener(e -> {
                Log.e(TAG, "Error deleting user", e);
                errorLiveData.postValue("Lỗi xóa tài khoản");
            });
    }
    
    // Update user points
    public void updateUserPoints(String userId, long points) {
        db.collection("users").document(userId)
            .update("points", points)
            .addOnSuccessListener(aVoid -> {
                Log.d(TAG, "User points updated");
                operationSuccessLiveData.postValue(true);
            })
            .addOnFailureListener(e -> {
                Log.e(TAG, "Error updating points", e);
                errorLiveData.postValue("Lỗi cập nhật điểm");
            });
    }
}
