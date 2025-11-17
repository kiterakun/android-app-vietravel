package com.group6.vietravel.ui.main;

import android.content.Intent;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.group6.vietravel.R;
import com.group6.vietravel.data.models.Place;
import com.group6.vietravel.databinding.ActivityMainBinding;
import com.group6.vietravel.ui.detail.DetailActivity;
import com.group6.vietravel.ui.main.journey.favoriteLocation.FavoriteLocationViewModel;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private MainViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_discovery, R.id.navigation_ranking, R.id.navigation_journey, R.id.navigation_account, R.id.navigation_chatbot)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupWithNavController(binding.navView, navController);

        mViewModel = new ViewModelProvider(this).get(MainViewModel.class);

        mViewModel.getSelectedPlace().observe(this, new Observer<Place>() {
            @Override
            public void onChanged(Place place) {
                Intent intent = new Intent(MainActivity.this,DetailActivity.class);

                intent.putExtra("PLACE_OBJECT",place);
                startActivity(intent);
            }
        });
    }

}