package com.group6.vietravel.ui.main;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.group6.vietravel.data.models.Place;


public class MainViewModel extends ViewModel {

    private MutableLiveData<Place> selectedPlace;

    public MainViewModel() {
        selectedPlace = new MutableLiveData<Place>();
    }

    public LiveData<Place> getSelectedPlace() {

        return selectedPlace;
    }

    public void selectPlace(Place place){
        selectedPlace.setValue(place);
    }
}
