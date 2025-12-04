package com.group6.vietravel.admin.ui.places;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.group6.vietravel.admin.data.repositories.AdminPlaceRepository;
import com.group6.vietravel.data.models.place.Place;

import java.util.List;

public class PlaceManagementViewModel extends AndroidViewModel {
    
    private final AdminPlaceRepository repository;
    
    public PlaceManagementViewModel(@NonNull Application application) {
        super(application);
        repository = AdminPlaceRepository.getInstance();
    }
    
    public LiveData<List<Place>> getAllPlaces() {
        return repository.getAllPlaces();
    }
    
    public LiveData<List<Place>> getPendingPlaces() {
        return repository.getPendingPlaces();
    }
    
    public LiveData<Boolean> getOperationSuccess() {
        return repository.getOperationSuccess();
    }
    
    public LiveData<String> getError() {
        return repository.getError();
    }
    
    public void loadAllPlaces() {
        repository.fetchAllPlaces();
    }
    
    public void loadPendingPlaces() {
        repository.fetchPendingPlaces();
    }
    
    public void deletePlace(String placeId) {
        repository.deletePlace(placeId);
    }
    
    public void approvePlace(String placeId) {
        repository.approvePlace(placeId);
    }
    
    public void rejectPlace(String placeId) {
        repository.rejectPlace(placeId);
    }
}
