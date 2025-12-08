package com.group6.vietravel.features.user.ui.main;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.group6.vietravel.data.models.place.Place;


public class MainViewModel extends ViewModel {

    private MutableLiveData<Place> selectedPlace;

    private MutableLiveData<String> pendingPlaceId;

    public MainViewModel() {
        selectedPlace = new MutableLiveData<Place>();
        pendingPlaceId = new MutableLiveData<>();
    }

    public LiveData<Place> getSelectedPlace() {

        return selectedPlace;
    }

    public void selectPlace(Place place) {
        selectedPlace.setValue(place);
    }

    public LiveData<String> getPendingPlaceId() {

        return pendingPlaceId;
    }

    public void pendingPlaceId(String placeId) {
        pendingPlaceId.setValue(placeId);

    }
}
