package com.group6.vietravel.features.ui.auth;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData; // Thêm import này

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot; // Thêm import này
import com.google.firebase.firestore.FirebaseFirestore; // Thêm import này
import com.group6.vietravel.data.models.user.User;
import com.group6.vietravel.data.repositories.auth.AuthRepository;

public class AuthViewModel extends AndroidViewModel {

    private final AuthRepository authRepository;
    private final LiveData<FirebaseUser> userLiveData;
    private final LiveData<String> errorLiveData;
    private final LiveData<User> userProfileLiveData;

    // 1. Thêm LiveData để chứa kết quả Role
    private final MutableLiveData<String> roleLiveData = new MutableLiveData<>();

    public AuthViewModel(@NonNull Application application) {
        super(application);
        authRepository = AuthRepository.getInstance();

        userLiveData = authRepository.getUserLiveData();
        errorLiveData = authRepository.getErrorLiveData();
        userProfileLiveData = authRepository.getUserProfileLiveData();
    }

    // ... (Các hàm register, login giữ nguyên) ...
    public void register(String username, String email, String password) {
        authRepository.register(username, email, password);
    }

    public void login(String email, String password) {
        authRepository.login(email, password);
    }

    // 2. Thêm hàm kiểm tra Role từ Firestore
    public void checkUserRole(String uid) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Giả sử collection tên là "users" và document ID chính là UID
        db.collection("users").document(uid).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Lấy trường "role" (ví dụ: "admin" hoặc "user")
                        String role = documentSnapshot.getString("role");
                        if (role != null) {
                            roleLiveData.postValue(role);
                        } else {
                            // Nếu không có field role, mặc định là user
                            roleLiveData.postValue("user");
                        }
                    } else {
                        // Nếu user mới đăng ký chưa có data trong Firestore -> coi là user thường
                        roleLiveData.postValue("user");
                    }
                })
                .addOnFailureListener(e -> {
                    // Nếu lỗi mạng hoặc lỗi khác -> báo lỗi hoặc mặc định user
                    roleLiveData.postValue("user");
                });
    }

    // 3. Getter cho Role LiveData
    public LiveData<String> getRoleLiveData() {
        return roleLiveData;
    }

    // ... (Các getter cũ giữ nguyên) ...
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