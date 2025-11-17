package com.group6.vietravel.ui.detail;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.group6.vietravel.R;
import com.group6.vietravel.data.models.Place;
import com.group6.vietravel.ui.detail.description.DescriptionFragment;
import com.group6.vietravel.ui.detail.evaluate.EvaluateFragment;
import com.group6.vietravel.ui.detail.overview.OverViewFragment;

public class DetailActivity extends AppCompatActivity {
    private ImageView imagePlace;
    private TextView ratingAvg;
    private TextView namePlaceTextView;
    private ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        if(savedInstanceState == null ){
            getSupportFragmentManager().beginTransaction().
                    replace(R.id.child_fragment_container,new OverViewFragment()).commit();
        }

        imagePlace = findViewById(R.id.imagePlace);
        namePlaceTextView = findViewById(R.id.namePlaceTextView);
        ratingAvg = findViewById(R.id.ratingAvg);
        back = findViewById(R.id.back);

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

        Intent intent = getIntent();
        Place place = intent.getParcelableExtra("PLACE_OBJECT");
        loadContent(place);

        back.setOnClickListener(v->{
            finish();
        });

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
}