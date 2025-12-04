package com.group6.vietravel.admin.ui.reviews;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.group6.vietravel.admin.data.repositories.AdminReviewRepository;
import com.group6.vietravel.data.models.Review;

import java.util.List;

public class ReviewModerationViewModel extends AndroidViewModel {
    
    private final AdminReviewRepository repository;
    
    public ReviewModerationViewModel(@NonNull Application application) {
        super(application);
        repository = AdminReviewRepository.getInstance();
    }
    
    public LiveData<List<Review>> getAllReviews() {
        return repository.getAllReviews();
    }
    
    public LiveData<List<Review>> getPendingReviews() {
        return repository.getPendingReviews();
    }
    
    public LiveData<Boolean> getOperationSuccess() {
        return repository.getOperationSuccess();
    }
    
    public LiveData<String> getError() {
        return repository.getError();
    }
    
    public void loadAllReviews() {
        repository.fetchAllReviews();
    }
    
    public void loadPendingReviews() {
        repository.fetchPendingReviews();
    }
    
    public void approveReview(String reviewId) {
        repository.approveReview(reviewId);
    }
    
    public void rejectReview(String reviewId) {
        repository.rejectReview(reviewId);
    }
    
    public void deleteReview(String reviewId) {
        repository.deleteReview(reviewId);
    }
    
    public void bulkApprove(List<String> reviewIds) {
        repository.bulkApproveReviews(reviewIds);
    }
    
    public void bulkReject(List<String> reviewIds) {
        repository.bulkRejectReviews(reviewIds);
    }
}
