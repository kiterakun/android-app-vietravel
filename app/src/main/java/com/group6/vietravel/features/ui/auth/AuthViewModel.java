package com.group6.vietravel.features.ui.auth;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.group6.vietravel.data.models.user.User;
import com.group6.vietravel.data.repositories.auth.AuthRepository;

public class AuthViewModel extends AndroidViewModel {

    private final AuthRepository authRepository;

    // 1. Thay đổi userLiveData thành Mutable để ViewModel tự quản lý luồng Auth
    private final MutableLiveData<FirebaseUser> userLiveData = new MutableLiveData<>();

    private MutableLiveData<String> roleLiveData = new MutableLiveData<>();
    private MutableLiveData<String> errorLiveData = new MutableLiveData<>();
    private final LiveData<User> userProfileLiveData;

    private FirebaseAuth.AuthStateListener authStateListener;

    public AuthViewModel(@NonNull Application application) {
        super(application);
        authRepository = AuthRepository.getInstance();

        // 2. KHÔNG lấy userLiveData từ Repository nữa (vì Repository có thể bị cache cũ)
        // userLiveData = authRepository.getUserLiveData();  <-- XÓA HOẶC COMMENT DÒNG NÀY

        // 3. Lấy dữ liệu khác từ Repo thì vẫn giữ nguyên
        errorLiveData = authRepository.getErrorLiveData();
        userProfileLiveData = authRepository.getUserProfileLiveData();

        // 4. THÊM MỚI: Tự lắng nghe trạng thái Firebase tại đây
        // Cơ chế này đảm bảo khi signOut() -> Firebase báo null -> LiveData cập nhật null ngay lập tức
        authStateListener = firebaseAuth -> {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            userLiveData.postValue(user);
        };
        FirebaseAuth.getInstance().addAuthStateListener(authStateListener);
    }

    // 5. Quan trọng: Hủy đăng ký lắng nghe khi ViewModel bị hủy để tránh rò rỉ bộ nhớ
    @Override
    protected void onCleared() {
        super.onCleared();
        if (authStateListener != null) {
            FirebaseAuth.getInstance().removeAuthStateListener(authStateListener);
        }
    }

    // ... (Các hàm register, login giữ nguyên) ...
    public void register(String username, String email, String password) {
        authRepository.register(username, email, password);
    }

    public void login(String email, String password) {
        authRepository.login(email, password);
    }

    // ... (Hàm checkUserRole giữ nguyên như bạn đã viết) ...
    public void checkUserRole(String uid) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(uid).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String status = documentSnapshot.getString("status");
                        if ("locked".equalsIgnoreCase(status)) {
                            roleLiveData.postValue("LOCKED");
                        } else {
                            String role = documentSnapshot.getString("role");
                            roleLiveData.postValue(role != null ? role : "user");
                        }
                    } else {
                        roleLiveData.postValue("user");
                    }
                })
                .addOnFailureListener(e -> {
                    errorLiveData.postValue("Lỗi kiểm tra quyền: " + e.getMessage());
                });
    }

    // Getter
    public LiveData<FirebaseUser> getUserLiveData() {
        return userLiveData;
    }

    public LiveData<String> getRoleLiveData() {
        return roleLiveData;
    }

    public LiveData<User> getUserProfileLiveData() {
        return userProfileLiveData;
    }

    public LiveData<String> getErrorLiveData() {
        return errorLiveData;
    }
}