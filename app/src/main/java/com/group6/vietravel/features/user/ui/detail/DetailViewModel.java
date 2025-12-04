package com.group6.vietravel.features.user.ui.detail;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.group6.vietravel.data.models.place.Place;
import com.group6.vietravel.data.models.review.Review;
import com.group6.vietravel.data.repositories.place.PlaceRepository;
import com.group6.vietravel.data.repositories.review.ReviewRepository;

import java.util.List;
import java.util.Objects;

public class DetailViewModel extends AndroidViewModel {
    private final PlaceRepository placeRepository;
    private  final ReviewRepository reviewRepository;

    private final MutableLiveData<Place> placeMutableLiveData;

    public DetailViewModel(Application application) {
        super(application);
        placeRepository = PlaceRepository.getInstance(application.getApplicationContext());
        reviewRepository = ReviewRepository.getInstance();
        placeMutableLiveData =new MutableLiveData<>();
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
}