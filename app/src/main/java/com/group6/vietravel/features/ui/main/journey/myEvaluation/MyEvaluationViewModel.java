package com.group6.vietravel.ui.main.journey.myEvaluation;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.group6.vietravel.data.models.place.Place;
import com.group6.vietravel.data.models.review.Review;
import com.group6.vietravel.data.repositorys.ReviewRepository;

import java.util.List;

public class MyEvaluationViewModel extends ViewModel{
    private ReviewRepository repository;

    public MyEvaluationViewModel() {
        repository = ReviewRepository.getInstance();
    }

    public LiveData<List<Review>> getReviewList(){
        return repository.getAllUserReview();
    }

    public void loadReviews(){
        repository.fetchAllUserReview();
    }
}