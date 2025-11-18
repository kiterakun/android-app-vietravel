package com.group6.vietravel.ui.main.journey.historyJourney;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.group6.vietravel.R;
import com.group6.vietravel.adapters.PlaceAdapter;
import com.group6.vietravel.data.models.Place;
import com.group6.vietravel.ui.main.MainViewModel;
import com.group6.vietravel.ui.main.journey.favoriteLocation.FavoriteLocationViewModel;

import java.util.ArrayList;
import java.util.List;

public class HistoryJourneyFragment extends Fragment {

    private HistoryJourneyViewModel mViewModel;

    private MainViewModel mainViewModel;
    private ListView list_view_place;
    private PlaceAdapter adapter;

    public static HistoryJourneyFragment newInstance() {
        return new HistoryJourneyFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_history_journey, container, false);
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view,savedInstanceState);

        list_view_place = view.findViewById(R.id.list_view_place);
        adapter = new PlaceAdapter(getContext(),new ArrayList<>());
        list_view_place.setAdapter(adapter);

        mViewModel = new ViewModelProvider(this).get(HistoryJourneyViewModel.class);
        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        mViewModel.getVisitedPlaceList().observe(getViewLifecycleOwner(), new Observer<List<Place>>() {
            @Override
            public void onChanged(List<Place> places) {
                adapter.updateData(places);
            }
        });

        list_view_place.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Place selectedPlace = (Place) parent.getItemAtPosition(position);
                mainViewModel.selectPlace(selectedPlace);
            }
        });


    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(HistoryJourneyViewModel.class);
        // TODO: Use the ViewModel
    }

}