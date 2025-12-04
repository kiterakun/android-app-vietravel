package com.group6.vietravel.admin.data.repositories;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.group6.vietravel.data.models.review.Review;

import java.util.ArrayList;
import java.util.List;

public class AdminReviewRepository {
    
    private static final String TAG = "AdminReviewRepository";
    private static AdminReviewRepository instance;
    
    private final FirebaseFirestore db;
    private final MutableLiveData<List<Review>> allReviewsLiveData;
    private final MutableLiveData<List<Review>> pendingReviewsLiveData;
    private final MutableLiveData<Boolean> operationSuccessLiveData;
    private final MutableLiveData<String> errorLiveData;
    
    private AdminReviewRepository() {
        db = FirebaseFirestore.getInstance();
        allReviewsLiveData = new MutableLiveData<>();
        pendingReviewsLiveData = new MutableLiveData<>();
        operationSuccessLiveData = new MutableLiveData<>();
        errorLiveData = new MutableLiveData<>();
    }
    
    public static synchronized AdminReviewRepository getInstance() {
        if (instance == null) {
            instance = new AdminReviewRepository();
        }
        return instance;
    }
    
    public LiveData<List<Review>> getAllReviews() {
        return allReviewsLiveData;
    }
    
    public LiveData<List<Review>> getPendingReviews() {
        return pendingReviewsLiveData;
    }
    
    public LiveData<Boolean> getOperationSuccess() {
        return operationSuccessLiveData;
    }
    
    public LiveData<String> getError() {
        return errorLiveData;
    }
    
    // Fetch all reviews
    public void fetchAllReviews() {
        db.collection("reviews")
            .orderBy("created_at", Query.Direction.DESCENDING)
            .addSnapshotListener((value, error) -> {
                if (error != null) {
                    Log.w(TAG, "Error fetching reviews", error);
                    errorLiveData.postValue("Lỗi tải danh sách đánh giá");
                    return;
                }
                if (value != null) {
                    List<Review> reviews = value.toObjects(Review.class);
                    allReviewsLiveData.postValue(reviews);
                }
            });
    }
    
    // Fetch pending reviews (waiting approval)
    public void fetchPendingReviews() {
        db.collection("reviews")
            .whereEqualTo("status", "pending")
            .orderBy("created_at", Query.Direction.DESCENDING)
            .addSnapshotListener((value, error) -> {
                if (error != null) {
                    Log.w(TAG, "Error fetching pending reviews", error);
                    return;
                }
                if (value != null) {
                    List<Review> reviews = value.toObjects(Review.class);
                    pendingReviewsLiveData.postValue(reviews);
                }
            });
    }
    
    // Approve review
    public void approveReview(String reviewId) {
        db.collection("reviews").document(reviewId)
            .update("status", "approved")
            .addOnSuccessListener(aVoid -> {
                Log.d(TAG, "Review approved");
                operationSuccessLiveData.postValue(true);
            })
            .addOnFailureListener(e -> {
                Log.e(TAG, "Error approving review", e);
                errorLiveData.postValue("Lỗi duyệt đánh giá");
            });
    }
    
    // Reject review
    public void rejectReview(String reviewId) {
        db.collection("reviews").document(reviewId)
            .update("status", "rejected")
            .addOnSuccessListener(aVoid -> {
                Log.d(TAG, "Review rejected");
                operationSuccessLiveData.postValue(true);
            })
            .addOnFailureListener(e -> {
                Log.e(TAG, "Error rejecting review", e);
                errorLiveData.postValue("Lỗi từ chối đánh giá");
            });
    }
    
    // Delete review
    public void deleteReview(String reviewId) {
        db.collection("reviews").document(reviewId)
            .delete()
            .addOnSuccessListener(aVoid -> {
                Log.d(TAG, "Review deleted");
                operationSuccessLiveData.postValue(true);
            })
            .addOnFailureListener(e -> {
                Log.e(TAG, "Error deleting review", e);
                errorLiveData.postValue("Lỗi xóa đánh giá");
            });
    }
    
    // Bulk approve
    public void bulkApproveReviews(List<String> reviewIds) {
        int[] count = {0};
        for (String id : reviewIds) {
            db.collection("reviews").document(id)
                .update("status", "approved")
                .addOnSuccessListener(aVoid -> {
                    count[0]++;
                    if (count[0] == reviewIds.size()) {
                        operationSuccessLiveData.postValue(true);
                    }
                });
        }
    }
    
    // Bulk reject
    public void bulkRejectReviews(List<String> reviewIds) {
        int[] count = {0};
        for (String id : reviewIds) {
            db.collection("reviews").document(id)
                .update("status", "rejected")
                .addOnSuccessListener(aVoid -> {
                    count[0]++;
                    if (count[0] == reviewIds.size()) {
                        operationSuccessLiveData.postValue(true);
                    }
                });
        }
    }
}
