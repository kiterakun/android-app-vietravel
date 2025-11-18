package com.group6.vietravel.ui.detail;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.group6.vietravel.R;
import com.group6.vietravel.data.models.Place;
import com.group6.vietravel.ui.detail.description.DescriptionFragment;
import com.group6.vietravel.ui.detail.evaluate.EvaluateFragment;
import com.group6.vietravel.ui.detail.overview.OverViewFragment;

import java.util.List;

public class DetailActivity extends AppCompatActivity {

    private DetailViewModel detailViewModel;
    private ImageView imagePlace;
    private TextView ratingAvg;
    private TextView namePlaceTextView;
    private ImageView back;
    private ImageView favorite;
    private ImageView visited;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_detail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        transactionFragment(savedInstanceState);

        detailViewModel = new ViewModelProvider(this).get(DetailViewModel.class);

        imagePlace = findViewById(R.id.imagePlace);
        namePlaceTextView = findViewById(R.id.namePlaceTextView);
        ratingAvg = findViewById(R.id.ratingAvg);
        back = findViewById(R.id.back);
        favorite = findViewById(R.id.favorite);
        visited = findViewById(R.id.visited);

        Intent intent = getIntent();
        Place place = intent.getParcelableExtra("PLACE_OBJECT");
        loadContent(place);

        detailViewModel.getListFavoritePlace().observe(this, new Observer<List<Place>>() {
            @Override
            public void onChanged(List<Place> places) {
                handelFavorite(place,places);
            }
        });

        detailViewModel.getListVisitedPlace().observe(this, new Observer<List<Place>>() {
            @Override
            public void onChanged(List<Place> places) {
                handelVisited(place,places);
            }
        });

        back.setOnClickListener(v->{
            finish();
        });
    }

    private void handelFavorite(Place place,List<Place> placeList){
        if(detailViewModel.containsPlace(place,placeList)){
            favorite.setImageTintList(
                    ColorStateList.valueOf(
                            ContextCompat.getColor(this, R.color.pink)
                    )
            );

            favorite.setOnClickListener(v->{
                detailViewModel.removeFavorite(place);
            });
        }
        else{
            favorite.setImageTintList(null);

            favorite.setOnClickListener(v->{
                detailViewModel.addFavorite(place);
            });
        }
    }

    private void handelVisited(Place place,List<Place> placeList){
        if(detailViewModel.containsPlace(place,placeList)){
            visited.setImageTintList(
                    ColorStateList.valueOf(
                            ContextCompat.getColor(this, R.color.blue)
                    )
            );

            visited.setOnClickListener(v->{
                detailViewModel.removeVisited(place);
            });
        }
        else{
            visited.setImageTintList(null);

            visited.setOnClickListener(v->{
                detailViewModel.addVisited(place);
            });
        }
    }

    private void loadContent(Place place){
        String imageUrl = place.getCached_image_url();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(DetailActivity.this)
                    .load(imageUrl) // Tải từ URL
                    .into(imagePlace); // Đặt vào ImageView
        }
        namePlaceTextView.setText(place.getName());

        ratingAvg.setText(String.format("%.1f", place.getRating_avg()));
    }

    private void loadChildFragment(Fragment childFragment){
        FragmentTransaction transaction= getSupportFragmentManager().beginTransaction();

        transaction.replace(R.id.child_fragment_container, childFragment);

        transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);

        transaction.commit();
    }

    private void transactionFragment(Bundle savedInstanceState){
        if(savedInstanceState == null ){
            getSupportFragmentManager().beginTransaction().
                    replace(R.id.child_fragment_container,new OverViewFragment()).commit();
        }

        TextView overview = findViewById(R.id.overview);
        TextView description = findViewById(R.id.description);
        TextView evaluate = findViewById(R.id.evaluate);

        overview.setOnClickListener(v -> {
            loadChildFragment(new OverViewFragment());
        });
        description.setOnClickListener(v -> {
            loadChildFragment(new DescriptionFragment());
        });
        evaluate.setOnClickListener(v -> {
            loadChildFragment(new EvaluateFragment());
        });
    }
}