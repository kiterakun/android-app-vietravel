package com.group6.vietravel.ui.detail;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.group6.vietravel.data.models.Place;
import com.group6.vietravel.data.repositorys.PlaceRepository;

import java.util.List;
import java.util.Objects;

public class DetailViewModel extends AndroidViewModel {
    private PlaceRepository repository;

    public DetailViewModel(Application application) {
        super(application);
        repository = PlaceRepository.getInstance(application.getApplicationContext());
    }

    public LiveData<List<Place>> getListFavoritePlace(){
        return repository.getFavoritePlaces();
    }

    public LiveData<List<Place>> getListVisitedPlace(){
        return repository.getVisitedPlaces();
    }

    public boolean containsPlace(Place place,List<Place> placeList){
        for(Place p:placeList){
            if(Objects.equals(p.getPlaceId(), place.getPlaceId()))return true;
        }
        return false;
    }

    public void addFavorite(Place place){
        repository.addFavorite(place.getPlaceId());
    }

    public void removeFavorite(Place place){
        repository.removeFavorite(place.getPlaceId());
    }

    public void addVisited(Place place){
        repository.addVisited(place.getPlaceId());
    }

    public void removeVisited(Place place){
        repository.removeVisited(place.getPlaceId());
    }
}