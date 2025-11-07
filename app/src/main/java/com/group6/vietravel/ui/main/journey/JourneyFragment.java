package com.group6.vietravel.ui.main.journey;

import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.group6.vietravel.R;
import com.group6.vietravel.ui.main.journey.favoriteLocation.FavoriteLocationFragment;
import com.group6.vietravel.ui.main.journey.historyJourney.HistoryJourneyFragment;
import com.group6.vietravel.ui.main.journey.myEvaluation.MyEvaluationFragment;
import com.group6.vietravel.ui.main.journey.saveLocation.SaveLocationFragment;

public class JourneyFragment extends Fragment{

    private JourneyViewModel mViewModel;

    public static JourneyFragment newInstance() {
        return new JourneyFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_journey, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view,savedInstanceState);
        if(savedInstanceState == null ){
            getChildFragmentManager().beginTransaction().
                    replace(R.id.child_fragment_container,new HistoryJourneyFragment()).commit();
        }
        LinearLayout saveLocation = view.findViewById(R.id.saveLocation);
        LinearLayout myEvaluation = view.findViewById(R.id.myEvaluation);
        LinearLayout favoriteLocation = view.findViewById(R.id.favoriteLocation);
        LinearLayout historyJourney = view.findViewById(R.id.historyJourney);

        saveLocation.setOnClickListener(v -> {
            loadChildFragment(new SaveLocationFragment());
        });

        myEvaluation.setOnClickListener(v -> {
            loadChildFragment(new MyEvaluationFragment());
        });

        favoriteLocation.setOnClickListener(v -> {
            loadChildFragment(new FavoriteLocationFragment());
        });

        historyJourney.setOnClickListener(v -> {
            loadChildFragment(new HistoryJourneyFragment());
        });

    }

    private void loadChildFragment(Fragment childFragment){
        FragmentTransaction transaction= getChildFragmentManager().beginTransaction();

        transaction.replace(R.id.child_fragment_container, childFragment);

        transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);

        transaction.commit();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(JourneyViewModel.class);
        // TODO: Use the ViewModel
    }

}