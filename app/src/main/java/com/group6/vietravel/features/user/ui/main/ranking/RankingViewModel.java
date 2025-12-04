package com.group6.vietravel.features.user.ui.main.ranking;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;


import com.group6.vietravel.data.models.place.Place;
import com.group6.vietravel.data.repositorys.PlaceRepository;

import java.util.List;

public class RankingViewModel extends AndroidViewModel {

    private final PlaceRepository repository;
    private final MutableLiveData<List<Place>> rankingPlaces = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);

    public RankingViewModel(@NonNull Application application) {
        super(application);
        repository = PlaceRepository.getInstance(application.getApplicationContext());
        loadRanking("all","all","all",7);
    }

    public void loadRanking(String province, String district, String categoryId, int topN) {
        isLoading.setValue(true);

        LiveData<List<Place>> repoSource = repository.fetchRankedPlaces(province, district, categoryId, topN);

        repoSource.observeForever(new Observer<List<Place>>() {
            @Override
            public void onChanged(List<Place> places) {
                rankingPlaces.setValue(places);
                isLoading.setValue(false);
                repoSource.removeObserver(this);
            }
        });
    }

    public LiveData<List<Place>> getRankingPlaces() { return rankingPlaces; }
    public LiveData<Boolean> getIsLoading() { return isLoading; }
}