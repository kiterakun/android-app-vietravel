package com.group6.vietravel.features.user.ui.search;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.group6.vietravel.data.models.place.Place;
import com.group6.vietravel.data.repositorys.PlaceRepository;

import java.util.List;

public class SearchViewModel extends AndroidViewModel {

    PlaceRepository placeRepository;

    public SearchViewModel(@NonNull Application application) {
        super(application);
        placeRepository = PlaceRepository.getInstance(application.getApplicationContext());
    }

    public void setListSearch(String query, String catId, String prov, String dist) {
        placeRepository.searchPlaces(query, catId, prov, dist);
    }

    public LiveData<List<Place>> getListSearch(){
        return placeRepository.getSearchPlaces();
    }
}
