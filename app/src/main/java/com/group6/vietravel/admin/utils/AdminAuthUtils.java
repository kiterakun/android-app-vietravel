package com.group6.vietravel.admin.utils;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class AdminAuthUtils {
    
    // Check if current user is admin
    public static void checkIsAdmin(OnAdminCheckListener listener) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        
        if (currentUser == null) {
            listener.onResult(false);
            return;
        }
        
        FirebaseFirestore.getInstance()
            .collection("users")
            .document(currentUser.getUid())
            .get()
            .addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    String role = documentSnapshot.getString("role");
                    listener.onResult("admin".equals(role));
                } else {
                    listener.onResult(false);
                }
            })
            .addOnFailureListener(e -> listener.onResult(false));
    }
    
    public interface OnAdminCheckListener {
        void onResult(boolean isAdmin);
    }
}
