package com.group6.vietravel.data.repositorys;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.group6.vietravel.data.models.User;

public class AuthRepository {

    private static final String TAG = "AuthRepository";
    private static AuthRepository instance;
    private final FirebaseAuth mAuth;
    private final FirebaseFirestore db;

    // LiveData
    private final MutableLiveData<FirebaseUser> userLiveData;
    private final MutableLiveData<String> errorLiveData;
    private final MutableLiveData<User> userProfileLiveData;

    private AuthRepository() {
        this.mAuth = FirebaseAuth.getInstance();
        this.db = FirebaseFirestore.getInstance();
        this.userLiveData = new MutableLiveData<>();
        this.errorLiveData = new MutableLiveData<>();
        this.userProfileLiveData = new MutableLiveData<>();

        // Kiểm tra xem có ai đã đăng nhập từ phiên trước không
        if (mAuth.getCurrentUser() != null) {
            userLiveData.postValue(mAuth.getCurrentUser());
            fetchUserProfile(mAuth.getCurrentUser().getUid());
        }
    }

    // Phương thức Singleton
    public static synchronized AuthRepository getInstance() {
        if (instance == null) {
            instance = new AuthRepository();
        }
        return instance;
    }

    public void register(String username, String email, String password) {
        errorLiveData.postValue(""); // Xóa lỗi cũ

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Đăng ký Auth thành công.");
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();

                        // Quan trọng: Tạo hồ sơ người dùng trong Firestore
                        if (firebaseUser != null) {
                            String uid = firebaseUser.getUid();
                            // Dùng model User.java chúng ta đã tạo
                            User newUser = new User(uid, username, email);
                            newUser.setAvatar_url("https://firebasestorage.googleapis.com" +
                                    "/v0/b/backend-vie-travel.firebasestorage.app/o/place_image" +
                                    "s%2Fth.jpg?alt=media&token=141f8044-00e0-4a71-abf8-66af673fb4f1");
                            // Ghi vào collection "users" với ID là UID
                            db.collection("users").document(uid)
                                    .set(newUser)
                                    .addOnSuccessListener(aVoid -> {
                                        Log.d(TAG, "Tạo hồ sơ Firestore thành công.");
                                        userLiveData.postValue(firebaseUser); // Báo thành công
                                        userProfileLiveData.postValue(newUser);
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e(TAG, "Lỗi tạo hồ sơ Firestore", e);
                                        errorLiveData.postValue(e.getMessage());
                                    });
                        }
                    } else {
                        // Đăng ký Auth thất bại
                        Log.w(TAG, "Đăng ký Auth thất bại", task.getException());
                        errorLiveData.postValue(task.getException().getMessage());
                    }
                });
    }

    public void login(String email, String password) {
        errorLiveData.postValue(""); // Xóa lỗi cũ

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Đăng nhập thành công.");
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        userLiveData.postValue(firebaseUser);

                        if (firebaseUser != null) {
                            fetchUserProfile(firebaseUser.getUid());
                        }
                    } else {
                        Log.w(TAG, "Đăng nhập thất bại", task.getException());
                        errorLiveData.postValue(task.getException().getMessage());
                    }
                });
    }

    public void fetchUserProfile(String uid) {
        if (uid == null) {
            userProfileLiveData.postValue(null);
            return;
        }
        db.collection("users").document(uid).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Chuyển đổi tài liệu Firestore thành đối tượng User.java
                        User user = documentSnapshot.toObject(User.class);
                        userProfileLiveData.postValue(user);
                    } else {
                        Log.w(TAG, "Không tìm thấy hồ sơ người dùng trong Firestore!");
                        userProfileLiveData.postValue(null);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Lỗi khi lấy hồ sơ người dùng", e);
                    userProfileLiveData.postValue(null);
                });
    }

    public void logout() {
        mAuth.signOut();
        userLiveData.postValue(null);
        userProfileLiveData.postValue(null);
    }

    public LiveData<User> getUserProfileLiveData() {
        return userProfileLiveData;
    }

    public MutableLiveData<FirebaseUser> getUserLiveData() {
        return userLiveData;
    }
    public MutableLiveData<String> getErrorLiveData() {
        return errorLiveData;
    }
}