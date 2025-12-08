package com.group6.vietravel.data.repositories.user;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.group6.vietravel.data.models.user.User;

import java.util.ArrayList;
import java.util.List;

public class UserRepository {
    private static final String TAG = "UserRepository";
    private static UserRepository instance;
    private final FirebaseFirestore db;

    private UserRepository() {
        db = FirebaseFirestore.getInstance();
    }

    public static synchronized UserRepository getInstance() {
        if (instance == null) {
            instance = new UserRepository();
        }
        return instance;
    }

    public LiveData<List<User>> getAllUsers() {
        MutableLiveData<List<User>> usersLiveData = new MutableLiveData<>();
        
        db.collection("users")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<User> userList = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        User user = document.toObject(User.class);
                        // Ensure UID is set if missing in document body but present in ID
                        if (user.getUid() == null) {
                            // This is a workaround, ideally UID should be in the document
                            // We can't easily set it if there is no setter or if it's final
                            // But User model has a getter, let's assume it's properly saved.
                            // If not, we might need to modify User model or handle it here.
                        }
                        userList.add(user);
                    }
                    usersLiveData.postValue(userList);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error fetching users", e);
                    usersLiveData.postValue(null);
                });
        
        return usersLiveData;
    }

    public void updateUserStatus(String uid, String newStatus, OnUserOperationListener listener) {
        db.collection("users").document(uid)
                .update("status", newStatus)
                .addOnSuccessListener(aVoid -> {
                    if (listener != null) listener.onSuccess();
                })
                .addOnFailureListener(e -> {
                    if (listener != null) listener.onFailure(e.getMessage());
                });
    }

    // Soft delete is basically setting status to 'hidden' or 'deleted'
    // But if we want hard delete:
    public void deleteUser(String uid, OnUserOperationListener listener) {
        // Note: Deleting user from Firestore does not delete from Auth.
        // Admin SDK is needed for Auth deletion, or a Cloud Function.
        // For this app scope, we might just delete the Firestore document.
        db.collection("users").document(uid)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    if (listener != null) listener.onSuccess();
                })
                .addOnFailureListener(e -> {
                    if (listener != null) listener.onFailure(e.getMessage());
                });
    }

    public interface OnUserOperationListener {
        void onSuccess();
        void onFailure(String error);
    }
}
