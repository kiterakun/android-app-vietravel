package com.group6.vietravel.ui.main.journey.historyJourney;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.group6.vietravel.data.models.Place;
import com.group6.vietravel.data.repositorys.PlaceRepository;

import java.util.List;

public class HistoryJourneyViewModel extends AndroidViewModel {
    private PlaceRepository repository;

    public HistoryJourneyViewModel(Application application) {
        super(application);
        repository = PlaceRepository.getInstance(application.getApplicationContext());
    }

    public LiveData<List<Place>> getVisitedPlaceList(){
        return repository.getVisitedPlaces();
    }

    public void loadPlaces(){
        repository.fetchVisitedPlaces();
    }
}