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
    private MutableLiveData<String> roleLiveData = new MutableLiveData<>();
    private MutableLiveData<String> errorLiveData = new MutableLiveData<>();

    // Nếu bạn muốn giữ tính đóng gói (Encapsulation), hãy thêm Getter trả về LiveData:

    private final LiveData<User> userProfileLiveData;

    // 1. Thêm LiveData để chứa kết quả Role


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

        db.collection("users").document(uid).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // 1. Lấy trạng thái (status)
                        String status = documentSnapshot.getString("status");

                        // 2. Kiểm tra status trước
                        if ("locked".equalsIgnoreCase(status)) {
                            // Nếu bị khóa, báo về LiveData giá trị đặc biệt
                            roleLiveData.postValue("LOCKED");
                        } else {
                            // 3. Nếu không khóa, mới lấy role và trả về bình thường
                            String role = documentSnapshot.getString("role");
                            roleLiveData.postValue(role != null ? role : "user");
                        }
                    } else {
                        // Không tìm thấy user trong DB -> Coi như user thường
                        roleLiveData.postValue("user");
                    }
                })
                .addOnFailureListener(e -> {
                    errorLiveData.postValue("Lỗi kiểm tra quyền: " + e.getMessage());
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