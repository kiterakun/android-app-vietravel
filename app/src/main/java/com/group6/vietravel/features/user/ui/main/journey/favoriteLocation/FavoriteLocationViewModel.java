package com.group6.vietravel.features.user.ui.main.journey.favoriteLocation;

import android.app.Application;
import android.graphics.Bitmap;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.group6.vietravel.data.models.place.Place;
import com.group6.vietravel.data.repositorys.PlaceRepository;

import java.util.List;

public class FavoriteLocationViewModel extends AndroidViewModel {
    private PlaceRepository repository;

    public FavoriteLocationViewModel(Application application) {
        super(application);
        repository = PlaceRepository.getInstance(application.getApplicationContext());
    }

    public LiveData<List<Place>> getPlaceList(){
        return repository.getAllPlaces();
    }

    public LiveData<List<Place>> getFavotitePlaceList(){
        return repository.getFavoritePlaces();
    }

    public void loadPlaces(){
        repository.fetchAllPlaces();
    }
}