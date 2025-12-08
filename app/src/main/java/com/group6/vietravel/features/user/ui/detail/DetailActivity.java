package com.group6.vietravel.features.user.ui.detail;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.group6.vietravel.R;
import com.group6.vietravel.features.user.ui.adapters.ReviewPlaceAdapter;
import com.group6.vietravel.data.models.place.Place;
import com.group6.vietravel.data.models.review.Review;
import com.group6.vietravel.features.user.ui.main.MainActivity;

import java.util.ArrayList;
import java.util.List;

public class DetailActivity extends AppCompatActivity {

    private DetailViewModel detailViewModel;
    private ReviewPlaceAdapter adapter;
    private ImageView imagePlace;
    private TextView namePlaceTextView, tv_description, tv_address, tv_opening_hours, tv_phone,
            tv_website_uri, tv_price_range, tv_rating_count, tv_ai_content;
    private Toolbar back;
    private MaterialButton btn_favorite, btn_checkin, btn_submit_review, btn_map;
    private RecyclerView rv_reviews;
    private TextInputEditText edt_comment;
    private RatingBar rb_user_rating, ratingAvg;

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

        View root = findViewById(R.id.main);

        ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            Insets ime = insets.getInsets(WindowInsetsCompat.Type.ime());

            v.setPadding(
                    systemBars.left,
                    systemBars.top,
                    systemBars.right,
                    Math.max(systemBars.bottom, ime.bottom)
            );

            return insets;
        });

        detailViewModel = new ViewModelProvider(this).get(DetailViewModel.class);

        imagePlace = findViewById(R.id.imagePlace);
        namePlaceTextView = findViewById(R.id.namePlaceTextView);
        ratingAvg = findViewById(R.id.ratingAvg);
        back = findViewById(R.id.back);
        btn_favorite = findViewById(R.id.btn_favorite);
        btn_checkin = findViewById(R.id.btn_checkin);
        rv_reviews = findViewById(R.id.rv_reviews);
        tv_description = findViewById(R.id.tv_description);
        tv_address = findViewById(R.id.tv_address);
        tv_phone = findViewById(R.id.tv_phone);
        tv_opening_hours = findViewById(R.id.tv_opening_hours);
        tv_website_uri = findViewById(R.id.tv_website_uri);
        edt_comment = findViewById(R.id.edt_comment);
        rb_user_rating = findViewById(R.id.rb_user_rating);
        tv_price_range = findViewById(R.id.tv_price_range);
        tv_rating_count = findViewById(R.id.tv_rating_count);
        btn_submit_review = findViewById(R.id.btn_submit_review);
        btn_map = findViewById(R.id.btn_map);
        tv_ai_content = findViewById(R.id.tv_ai_content);

        Intent intent = getIntent();
        Place place = intent.getParcelableExtra("PLACE_OBJECT");

        detailViewModel.setPlace(place);
        detailViewModel.getPlace().observe(this, new Observer<Place>() {
            @Override
            public void onChanged(Place place) {
                loadContent(place);
            }
        });

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

        adapter = new ReviewPlaceAdapter(this,new ArrayList<>());
        rv_reviews.setAdapter(adapter);
        rv_reviews.setLayoutManager(new LinearLayoutManager(this));
        detailViewModel.setListReviewPlace(place);
        detailViewModel.getListReviewPlace().observe(this, new Observer<List<Review>>() {
            @Override
            public void onChanged(List<Review> reviews) {
                adapter.updateData(reviews);
            }
        });

        btn_map.setOnClickListener(v->{
            Intent new_intent = new Intent(DetailActivity.this, MainActivity.class);
            new_intent.putExtra("TARGET_LOCATION_ID", place.getPlaceId());

            new_intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

            startActivity(new_intent);
        });

        detailViewModel.setAiReview(place);

        detailViewModel.getAiReview().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                tv_ai_content.setText(s);
            }
        });

        back.setOnClickListener(v->{
            finish();
        });
    }

    private void handelFavorite(Place place,List<Place> placeList){
        if(detailViewModel.containsPlace(place,placeList)){
            btn_favorite.setBackgroundColor(Color.parseColor("#FF00FF"));

            btn_favorite.setOnClickListener(v->{
                detailViewModel.removeFavorite(place);
            });
        }
        else{
            btn_favorite.setBackgroundColor(Color.WHITE);

            btn_favorite.setOnClickListener(v->{
                detailViewModel.addFavorite(place);
            });
        }
    }

    private void handelVisited(Place place,List<Place> placeList){
        if(detailViewModel.containsPlace(place,placeList)){
            btn_checkin.setBackgroundColor(Color.parseColor("#2563EB"));

            btn_checkin.setOnClickListener(v->{
                detailViewModel.removeVisited(place);
            });
        }
        else{
            btn_checkin.setBackgroundColor(Color.WHITE);

            btn_checkin.setOnClickListener(v->{
                detailViewModel.addVisited(place);
            });
        }
    }

    private void loadContent(Place place){

        String imageUrl = place.getThumbnailUrl();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(DetailActivity.this)
                    .load(imageUrl) // Tải từ URL
                    .into(imagePlace); // Đặt vào ImageView
        }
        namePlaceTextView.setText(place.getName());

        ratingAvg.setRating(place.getRatingAvg());
        tv_description.setText(place.getDescription());
        tv_address.setText(place.getAddress());
        tv_rating_count.setText("(" + place.getRatingCount() + ")");
        tv_price_range.setText("$$ - " +place.getPriceRange());

        String phone = place.getPhoneNumber();
        if (phone != null && !phone.isEmpty()) {
            tv_phone.setText(phone);
        }

        if(!place.getOpeningHours().isEmpty()) {
            for(int i=0;i<place.getOpeningHours().size();i++){
                tv_opening_hours.append("\n" + place.getOpeningHours().get(i));
            }
        }
        String website = place.getWebsiteUri();
        if (website != null && !website.isBlank()) {
            tv_website_uri.setText(website);
        }

        btn_submit_review.setOnClickListener(v -> {
            String comment = edt_comment.getText().toString().trim();
            float rating = rb_user_rating.getRating();
            if(comment.isBlank() || rating==0){
                Toast.makeText(this,"Comments and rating cannot be blank",Toast.LENGTH_SHORT).show();
            }
            else {
                detailViewModel.addReviewPlace(comment,place,rating);
                edt_comment.setText("");
                rb_user_rating.setRating(0);
            }
        });
    }
}