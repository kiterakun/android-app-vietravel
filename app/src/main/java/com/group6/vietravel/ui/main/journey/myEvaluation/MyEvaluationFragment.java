package com.group6.vietravel.ui.main.journey.myEvaluation;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.group6.vietravel.R;
import com.group6.vietravel.adapters.PlaceAdapter;
import com.group6.vietravel.adapters.ReviewAdapter;
import com.group6.vietravel.data.models.Place;
import com.group6.vietravel.data.models.Review;
import com.group6.vietravel.ui.main.MainViewModel;
import com.group6.vietravel.ui.main.journey.favoriteLocation.FavoriteLocationViewModel;
import com.group6.vietravel.utils.PlaceUtils;

import java.util.ArrayList;
import java.util.List;

public class MyEvaluationFragment extends Fragment {

    private MyEvaluationViewModel mViewModel;
    private MainViewModel mainViewModel;
    private ListView list_view_review;
    private ReviewAdapter adapter;

    public static MyEvaluationFragment newInstance() {
        return new MyEvaluationFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_my_evaluation, container, false);
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view,savedInstanceState);

        list_view_review = view.findViewById(R.id.list_view_review);
        adapter = new ReviewAdapter(getContext(),new ArrayList<>());
        list_view_review.setAdapter(adapter);

        mViewModel = new ViewModelProvider(this).get(MyEvaluationViewModel.class);
        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        mViewModel.getReviewList().observe(getViewLifecycleOwner(), new Observer<List<Review>>() {
            @Override
            public void onChanged(List<Review> reviews) {
                adapter.updateData(reviews);
            }
        });

        list_view_review.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Review selectedReview = (Review) parent.getItemAtPosition(position);
                PlaceUtils.getPlaceById(selectedReview.getPlaceId(), new PlaceUtils.OnPlaceLoadedCallback() {
                    @Override
                    public void onPlaceLoaded(Place place) {
                        mainViewModel.selectPlace(place);
                    }
                    @Override
                    public void onError(Exception e) {
                        Log.e("MyEvaluationFragment","Can not load place data");
                    }
                });

            }
        });


    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(MyEvaluationViewModel.class);
        // TODO: Use the ViewModel
    }

}