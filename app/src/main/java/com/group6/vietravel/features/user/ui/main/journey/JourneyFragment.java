package com.group6.vietravel.features.user.ui.main.journey;

import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.group6.vietravel.R;
import com.group6.vietravel.data.models.user.User;
import com.group6.vietravel.features.ui.auth.AuthViewModel;
import com.group6.vietravel.features.user.ui.main.journey.favoriteLocation.FavoriteLocationFragment;
import com.group6.vietravel.features.user.ui.main.journey.historyJourney.HistoryJourneyFragment;
import com.group6.vietravel.features.user.ui.main.journey.myEvaluation.MyEvaluationFragment;

public class JourneyFragment extends Fragment{

    private JourneyViewModel mViewModel;
    private AuthViewModel authViewModel;

    private ImageView imageAvatar;
    private TextView textName;
    private TextView textPoint;
    private ImageView imageRank;
    private TextView textRank;


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

        LinearLayout myEvaluation = view.findViewById(R.id.myEvaluation);
        LinearLayout favoriteLocation = view.findViewById(R.id.favoriteLocation);
        LinearLayout historyJourney = view.findViewById(R.id.historyJourney);

        if(savedInstanceState == null ){
            getChildFragmentManager().beginTransaction().
                    replace(R.id.child_fragment_container,new HistoryJourneyFragment()).commit();
            historyJourney.setBackgroundResource(R.drawable.shape_blue_rounded_corners_8dp);
        }

        myEvaluation.setOnClickListener(v -> {
            loadChildFragment(new MyEvaluationFragment());
            myEvaluation.setBackgroundResource(R.drawable.shape_blue_rounded_corners_8dp);
            favoriteLocation.setBackgroundResource(R.drawable.shape_rounded_corners_8dp);
            historyJourney.setBackgroundResource(R.drawable.shape_rounded_corners_8dp);

        });

        favoriteLocation.setOnClickListener(v -> {
            loadChildFragment(new FavoriteLocationFragment());
            favoriteLocation.setBackgroundResource(R.drawable.shape_blue_rounded_corners_8dp);
            myEvaluation.setBackgroundResource(R.drawable.shape_rounded_corners_8dp);
            historyJourney.setBackgroundResource(R.drawable.shape_rounded_corners_8dp);
        });

        historyJourney.setOnClickListener(v -> {
            loadChildFragment(new HistoryJourneyFragment());
            historyJourney.setBackgroundResource(R.drawable.shape_blue_rounded_corners_8dp);
            favoriteLocation.setBackgroundResource(R.drawable.shape_rounded_corners_8dp);
            myEvaluation.setBackgroundResource(R.drawable.shape_rounded_corners_8dp);
        });


        imageAvatar = view.findViewById(R.id.imageAvatar);
        textName = view.findViewById(R.id.textName);
        textPoint = view.findViewById(R.id.textPoint);
        imageRank = view.findViewById(R.id.imageRank);
        textRank = view.findViewById(R.id.textRank);

        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        authViewModel.getUserProfileLiveData().observe(getViewLifecycleOwner(), new Observer<User>() {
            @Override
            public void onChanged(User user) {

                String avataUrl = user.getAvatar_url();
                if (avataUrl != null && !avataUrl.isEmpty()) {
                    Glide.with(getContext())
                            .load(avataUrl)
                            .into(imageAvatar);
                }

                textName.setText(user.getUsername());

                textPoint.setText("Điểm du lịch: " + String.valueOf(user.getPoints()));

                setRankUser(user.getPoints());

            }
        });
    }

    public void setRankUser(long points){

        if (points<100){
            textRank.setText("Rank Vàng");
            imageRank.setImageResource(R.drawable.ic_journey_rankgold);
        }
        else if (points<200){
            textRank.setText("Rank Bạch Kim");
            imageRank.setImageResource(R.drawable.ic_journey_rankplatinum);
        }
        else if (points<200){
            textRank.setText("Rank Lục Bảo");
            imageRank.setImageResource(R.drawable.ic_journey_rankemerald);
        }
        else {
            textRank.setText("Rank Cao Thủ");
            imageRank.setImageResource(R.drawable.ic_journey_rankmaster);
        }

    }

    private void loadChildFragment(Fragment childFragment){

        FragmentTransaction transaction= getChildFragmentManager().beginTransaction();

        transaction.replace(R.id.child_fragment_container, childFragment);

        transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);

        transaction.commit();
    }

}