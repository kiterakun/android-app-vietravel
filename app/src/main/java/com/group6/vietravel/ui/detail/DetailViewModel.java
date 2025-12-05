package com.group6.vietravel.ui.detail;

import android.app.Application;
import android.util.Log;
import android.util.Pair;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.group6.vietravel.data.models.Place;
import com.group6.vietravel.data.models.Review;
import com.group6.vietravel.data.repositorys.PlaceRepository;
import com.group6.vietravel.data.repositorys.ReviewRepository;
import com.group6.vietravel.utils.GeminiUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DetailViewModel extends AndroidViewModel {
    private final PlaceRepository placeRepository;
    private  final ReviewRepository reviewRepository;
    private final MutableLiveData<Place> placeMutableLiveData;
    private final MutableLiveData<String> aiReview;
    private final GeminiUtils geminiUtils;

    public DetailViewModel(Application application) {
        super(application);
        placeRepository = PlaceRepository.getInstance(application.getApplicationContext());
        reviewRepository = ReviewRepository.getInstance();
        placeMutableLiveData = new MutableLiveData<>();
        aiReview = new MutableLiveData<>();
        geminiUtils = new GeminiUtils();
    }

    public LiveData<Place> getPlace(){
        return placeMutableLiveData;
    }

    public void setPlace(Place place){
        placeMutableLiveData.setValue(place);
    }

    public LiveData<List<Place>> getListFavoritePlace(){
        return placeRepository.getFavoritePlaces();
    }

    public LiveData<List<Place>> getListVisitedPlace(){
        return placeRepository.getVisitedPlaces();
    }

    public LiveData<List<Review>> getListReviewPlace(){
        return reviewRepository.getAllReviewPlace();
    }

    public void setListReviewPlace(Place place){
        reviewRepository.fetchAllReviewPlace(place.getPlaceId());
    }

    public void addReviewPlace(String comment, Place place,Float rating){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user == null){
            Log.d("addReviewPlace","User null");
            return;
        }
        if(place == null){
            Log.e("addRatingPlace","Place null");
            return;
        }
        Review review = new Review(comment, place.getPlaceId(), rating, "approved", user.getUid());
        reviewRepository.saveNewReviewPlace(review);

        int rating_count = place.getRatingCount();
        float rating_avg = place.getRatingAvg();
        float new_rating =  (rating_avg*rating_count + rating)/(rating_count+1);
        int new_rating_count = rating_count+1;
        place.setRatingAvg(new_rating);
        place.setRatingCount(new_rating_count);

        placeRepository.savePlace(place);
        setPlace(place);
    }

    public boolean containsPlace(Place place,List<Place> placeList){
        for(Place p:placeList){
            if(Objects.equals(p.getPlaceId(), place.getPlaceId()))return true;
        }
        return false;
    }

    public void setAiReview(Place place, List<Review> reviewList){
        aiReview.postValue("Đang tổng hợp đánh giá, đợi chút nhé...");

        geminiUtils.getReview(reviewList, place, new GeminiUtils.AiCallbackReview() {
            @Override
            public void onSuccess(String response) {
                aiReview.postValue(response);
            }

            @Override
            public void onError(Throwable t) {

                if (t.getMessage() != null && t.getMessage().contains("429")) {
                    aiReview.postValue("Hệ thống đang quá tải, vui lòng thử lại sau 1 phút nhé!");
                } else {
                    aiReview.postValue("Xin lỗi, tôi đang gặp sự cố kết nối: " + t.getMessage());
                }

                // In log để debug
                Log.e("ChatViewModel", "AI Error", t);
            }
        });
    }

    public void addFavorite(Place place){
        placeRepository.addFavorite(place.getPlaceId());
    }

    public void removeFavorite(Place place){
        placeRepository.removeFavorite(place.getPlaceId());
    }

    public void addVisited(Place place){
        placeRepository.addVisited(place.getPlaceId());
    }

    public void removeVisited(Place place){
        placeRepository.removeVisited(place.getPlaceId());
    }

    public LiveData<String> getAiReview() {
        return aiReview;
    }
}