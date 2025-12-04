package com.group6.vietravel.utils;

import android.util.Log;

import com.google.firebase.firestore.FirebaseFirestore;
import com.group6.vietravel.data.models.user.User;

public class UserUtils {
    public interface OnUserLoadedCallback {
        void onPlaceLoaded(User user);
        void onError(Exception e);
    }

    public static void getUserById(String userId, UserUtils.OnUserLoadedCallback callback) {

        if (userId == null || userId.isEmpty()) {
            Log.e("PlaceUtils", "userId bị null hoặc rỗng");
            return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("users").document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        User user = documentSnapshot.toObject(User.class);

                        callback.onPlaceLoaded(user);
                    } else {
                        Log.w("PlaceUtils", "Không tìm thấy user với ID: " + userId);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Lỗi kết nối: " + e.getMessage());
                    callback.onError(e);
                });
    }
}
