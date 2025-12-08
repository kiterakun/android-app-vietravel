package com.group6.vietravel.admin.ui.reviews;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.group6.vietravel.data.models.place.Place;
import com.group6.vietravel.data.models.review.Review;
import com.group6.vietravel.data.repositories.place.PlaceRepository;
import com.group6.vietravel.data.repositories.review.ReviewRepository;

import java.util.List;

public class ReviewModerationViewModel extends AndroidViewModel {

    private final ReviewRepository reviewRepository;
    private final PlaceRepository placeRepository;

    // LiveData để báo kết quả thao tác (hiển thị Toast)
    private final MutableLiveData<Boolean> operationSuccess = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public ReviewModerationViewModel(@NonNull Application application) {
        super(application);
        // Khởi tạo các Repository có sẵn
        reviewRepository = ReviewRepository.getInstance();
        placeRepository = PlaceRepository.getInstance(application.getApplicationContext());
    }

    // --- LOADING DATA ---

    public void loadAllReviews() {
        // Gọi hàm fetch review cho admin
        reviewRepository.fetchAllReviewsForAdmin();
    }

    public LiveData<List<Review>> getAllReviews() {
        return reviewRepository.getAllReviewsAdmin();
    }

    public void loadAllPlaces() {
        // Load danh sách địa điểm để hiển thị tên địa điểm (nếu cần)
        placeRepository.fetchAllPlaces(); // Đảm bảo PlaceRepository có hàm này
    }

    public LiveData<List<Place>> getAllPlaces() {
        return placeRepository.getAllPlaces();
    }

    // --- APPROVE LOGIC (DUYỆT & TÍNH ĐIỂM) ---

    public void approveReview(Review review) {
        if (review == null) return;

        // 1. Cập nhật trạng thái Review thành "approved"
        review.setStatus("approved");

        // Mẹo: Nếu muốn cập nhật giao diện ngay lập tức mà không đợi Firebase trả về,
        // bạn có thể thêm logic cập nhật list local ở đây.

        reviewRepository.updateReview(review);

        // 2. Lấy thông tin địa điểm để tính lại điểm trung bình
        String placeId = review.getPlaceId();

        placeRepository.getPlaceById(placeId, new PlaceRepository.OnPlaceLoadedCallback() {
            @Override
            public void onPlaceLoaded(Place place) {
                if (place != null) {
                    // 3. Tính toán lại Rating và lưu Place
                    calculateAndSaveNewRating(place, review.getRating());
                    operationSuccess.postValue(true);
                } else {
                    errorMessage.postValue("Không tìm thấy địa điểm " + placeId);
                }
            }
        });
    }

    // [QUAN TRỌNG] Logic tính toán lại điểm số
    private void calculateAndSaveNewRating(Place place, float newRatingValue) {
        int currentCount = place.getRatingCount();
        float currentAvg = place.getRatingAvg();

        // Công thức tính trung bình cộng tích lũy:
        // ((Trung bình cũ * Số lượng cũ) + Điểm mới) / (Số lượng cũ + 1)
        float newAvg = ((currentAvg * currentCount) + newRatingValue) / (currentCount + 1);
        int newCount = currentCount + 1;

        place.setRatingAvg(newAvg);
        place.setRatingCount(newCount);

        placeRepository.savePlace(place);
    }

    // --- REJECT LOGIC (TỪ CHỐI) ---

    public void rejectReview(String reviewId) {
        reviewRepository.updateReviewStatus(reviewId, "rejected");
        operationSuccess.postValue(true);
    }

    public void updateReview(Review review) {
        reviewRepository.updateReview(review);
        operationSuccess.postValue(true);
    }

    // --- BULK ACTIONS (XỬ LÝ HÀNG LOẠT) ---

    public void bulkApprove(List<String> reviewIds) {
        for (String id : reviewIds) {
            // Lấy chi tiết review để có rating và placeId chính xác
            reviewRepository.getReviewById(id, review -> {
                // Chỉ duyệt những cái chưa approved để tránh cộng điểm nhiều lần
                if (review != null && !"approved".equals(review.getStatus())) {
                    approveReview(review);
                }
            });
        }
    }

    public void bulkReject(List<String> reviewIds) {
        for (String id : reviewIds) {
            rejectReview(id);
        }
    }

    // --- GETTERS CHO UI STATE ---

    public LiveData<Boolean> getOperationSuccess() {
        return operationSuccess;
    }

    public LiveData<String> getError() {
        return errorMessage;
    }
}