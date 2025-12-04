package com.group6.vietravel.data.repositories.review;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.group6.vietravel.data.models.review.Review;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ReviewRepository {
    private static final String TAG = "ReviewRepository";
    private static ReviewRepository instance;
    private final MutableLiveData<List<Review>> allUserReviewLiveData;

    private final MutableLiveData<List<Review>> allReviewPlaceLiveData;


    private final FirebaseFirestore db;
    private final FirebaseAuth auth;

    private ReviewRepository(){
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        allUserReviewLiveData = new MutableLiveData<>();
        allReviewPlaceLiveData = new MutableLiveData<>();

        fetchAllUserReview();
    }

    public static synchronized ReviewRepository getInstance() {
        if (instance == null) {
            instance = new ReviewRepository();
        }
        return instance;
    }

    public LiveData<List<Review>> getAllUserReview(){
        return allUserReviewLiveData;
    }

    public LiveData<List<Review>> getAllReviewPlace(){
        return allReviewPlaceLiveData;
    }

    public void fetchAllUserReview(){
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            allUserReviewLiveData.postValue(new ArrayList<>());
            return;
        }

        String userId = user.getUid();

        db.collection("reviews")
                .whereEqualTo("status", "approved")
                .whereEqualTo("user_id",userId)
                .orderBy("created_at", Query.Direction.DESCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.e("Firestore", "Lỗi lắng nghe", error);
                        return;
                    }
                    if(value!=null){
                        List<Review> reviewList = value.toObjects(Review.class);
                        allUserReviewLiveData.postValue(reviewList);
                        Log.d("Firestore", "Đã tải " + reviewList.size() + " bài đánh giá.");
                    }
                    else {
                        allUserReviewLiveData.postValue(new ArrayList<>());
                    }
                });
    }

    public void fetchAllReviewPlace(String placeId) {
        db.collection("reviews").whereEqualTo("status", "approved")
                .whereEqualTo("place_id",placeId)
                .orderBy("created_at", Query.Direction.DESCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.e("Firestore", "Lỗi lắng nghe", error);
                        return;
                    }
                    if(value!=null){
                        List<Review> reviewList = value.toObjects(Review.class);
                        allReviewPlaceLiveData.postValue(reviewList);
                        Log.d("Firestore", "Đã tải " + reviewList.size() + " bài đánh giá.");
                    }
                    else {
                        allReviewPlaceLiveData.postValue(new ArrayList<>());
                    }
                });
    }

    public void saveNewReviewPlace(Review review){
        if (review.getCreated_at() == null) {
            review.setCreated_at(new Date());
        }
        review.setCreated_at(new Date());
        if(review.getReviewId()!= null && !review.getReviewId().isEmpty()) {
            db.collection("reviews").document(review.getReviewId()).set(review)
                    .addOnSuccessListener(aVoid -> Log.d("Firestore", "Update thanh cong "))
                    .addOnFailureListener(e -> Log.e("Firestore", "Loi", e));

        }
        else{
            db.collection("reviews").add(review)
                    .addOnSuccessListener(aVoid -> Log.d("Firestore", "Add thanh cong "))
                    .addOnFailureListener(e -> Log.e("Firestore", "Loi", e));
        }
    }

}
