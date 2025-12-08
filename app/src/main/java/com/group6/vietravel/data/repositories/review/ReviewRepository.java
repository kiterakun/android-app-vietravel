package com.group6.vietravel.data.repositories.review;

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

    // LiveData cho User
    private final MutableLiveData<List<Review>> allUserReviewLiveData;
    private final MutableLiveData<List<Review>> allReviewPlaceLiveData;

    // [MỚI] LiveData cho Admin (chứa tất cả review: pending, approved, rejected)
    private final MutableLiveData<List<Review>> allReviewsAdminLiveData;

    private final FirebaseFirestore db;
    private final FirebaseAuth auth;

    // Interface callback để lấy 1 review duy nhất (dùng cho logic tính điểm)
    public interface OnReviewLoadedCallback {
        void onReviewLoaded(Review review);
    }

    private ReviewRepository(){
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        allUserReviewLiveData = new MutableLiveData<>();
        allReviewPlaceLiveData = new MutableLiveData<>();
        allReviewsAdminLiveData = new MutableLiveData<>(); // Init

        fetchAllUserReview();
    }

    public static synchronized ReviewRepository getInstance() {
        if (instance == null) {
            instance = new ReviewRepository();
        }
        return instance;
    }

    // --- GETTERS ---
    public LiveData<List<Review>> getAllUserReview(){
        return allUserReviewLiveData;
    }

    public LiveData<List<Review>> getAllReviewPlace(){
        return allReviewPlaceLiveData;
    }

    // [MỚI] Getter cho Admin
    public LiveData<List<Review>> getAllReviewsAdmin() {
        return allReviewsAdminLiveData;
    }

    // --- EXISTING METHODS ---

    public void fetchAllUserReview(){
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            allUserReviewLiveData.postValue(new ArrayList<>());
            return;
        }
        String userId = user.getUid();

        // Chỉ lấy review của user đó
        db.collection("reviews")
                .whereEqualTo("user_id", userId)
                .orderBy("created_at", Query.Direction.DESCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.e(TAG, "Lỗi lắng nghe user reviews", error);
                        return;
                    }
                    if(value != null){
                        // Tự động map ID từ document vào object nếu class Review có annotation @DocumentId hoặc setter setReviewId
                        List<Review> reviewList = value.toObjects(Review.class);
                        // Gán ID thủ công để chắc chắn (nếu model chưa xử lý)
                        for (int i = 0; i < reviewList.size(); i++) {
                            reviewList.get(i).setReviewId(value.getDocuments().get(i).getId());
                        }
                        allUserReviewLiveData.postValue(reviewList);
                    } else {
                        allUserReviewLiveData.postValue(new ArrayList<>());
                    }
                });
    }

    public void fetchAllReviewPlace(String placeId) {
        // Chỉ lấy review đã duyệt (approved) để hiển thị công khai
        db.collection("reviews")
                .whereEqualTo("status", "approved")
                .whereEqualTo("place_id", placeId)
                .orderBy("created_at", Query.Direction.DESCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.e(TAG, "Lỗi lắng nghe place reviews", error);
                        return;
                    }
                    if(value != null){
                        List<Review> reviewList = value.toObjects(Review.class);
                        // Gán ID thủ công
                        for (int i = 0; i < reviewList.size(); i++) {
                            reviewList.get(i).setReviewId(value.getDocuments().get(i).getId());
                        }
                        allReviewPlaceLiveData.postValue(reviewList);
                    } else {
                        allReviewPlaceLiveData.postValue(new ArrayList<>());
                    }
                });
    }

    // --- [MỚI] CÁC HÀM DÀNH CHO ADMIN ---

    // 1. Load tất cả Review (không lọc status) cho trang Admin
    public void fetchAllReviewsForAdmin() {
        db.collection("reviews")
                .orderBy("created_at", Query.Direction.DESCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.e(TAG, "Admin: Lỗi load reviews", error);
                        return;
                    }
                    if (value != null) {
                        List<Review> reviewList = value.toObjects(Review.class);
                        // Gán ID thủ công
                        for (int i = 0; i < reviewList.size(); i++) {
                            reviewList.get(i).setReviewId(value.getDocuments().get(i).getId());
                        }
                        allReviewsAdminLiveData.postValue(reviewList);
                    } else {
                        allReviewsAdminLiveData.postValue(new ArrayList<>());
                    }
                });
    }

    // 2. Cập nhật toàn bộ thông tin Review (Content, Rating, Status)
    public void updateReview(Review review) {
        if (review.getReviewId() == null || review.getReviewId().isEmpty()) return;

        db.collection("reviews").document(review.getReviewId())
                .set(review) // set sẽ ghi đè object
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Update review thành công"))
                .addOnFailureListener(e -> Log.e(TAG, "Update review thất bại", e));
    }

    // 3. Chỉ cập nhật trạng thái (Dùng cho Rejected nhanh)
    public void updateReviewStatus(String reviewId, String newStatus) {
        if (reviewId == null) return;

        db.collection("reviews").document(reviewId)
                .update("status", newStatus)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Update status thành công: " + newStatus))
                .addOnFailureListener(e -> Log.e(TAG, "Update status thất bại", e));
    }

    // 4. Lấy 1 Review theo ID (Dùng cho logic Bulk Approve hoặc tính điểm)
    public void getReviewById(String reviewId, OnReviewLoadedCallback callback) {
        db.collection("reviews").document(reviewId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Review review = documentSnapshot.toObject(Review.class);
                        if (review != null) {
                            review.setReviewId(documentSnapshot.getId());
                            callback.onReviewLoaded(review);
                        }
                    } else {
                        callback.onReviewLoaded(null);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Lỗi lấy review by ID", e);
                    callback.onReviewLoaded(null);
                });
    }

    // --- GIỮ NGUYÊN HÀM CŨ ---

    public void saveNewReviewPlace(Review review){
        if (review.getCreated_at() == null) {
            review.setCreated_at(new Date());
        }
        // Lưu ý: dòng này set lại date mỗi lần save, cẩn thận khi update
        // review.setCreated_at(new Date());

        if(review.getReviewId()!= null && !review.getReviewId().isEmpty()) {
            // Update
            db.collection("reviews").document(review.getReviewId()).set(review)
                    .addOnSuccessListener(aVoid -> Log.d(TAG, "Update thanh cong "))
                    .addOnFailureListener(e -> Log.e(TAG, "Loi", e));

        }
        else{
            // Add mới: Firebase tự sinh ID, nhưng ta cần lưu ID đó vào field reviewId của object nếu cần
            db.collection("reviews").add(review)
                    .addOnSuccessListener(documentReference -> {
                        Log.d(TAG, "Add thanh cong ID: " + documentReference.getId());
                        // Cập nhật lại ID vào chính document đó để dễ query sau này
                        documentReference.update("reviewId", documentReference.getId());
                    })
                    .addOnFailureListener(e -> Log.e(TAG, "Loi", e));
        }
    }
}