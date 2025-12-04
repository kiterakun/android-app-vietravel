package com.group6.vietravel.ui.main;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.group6.vietravel.R;
import com.group6.vietravel.data.models.place.Place;
import com.group6.vietravel.databinding.ActivityMainBinding;
import com.group6.vietravel.features.ui.detail.DetailActivity;
import com.group6.vietravel.features.ui.main.discovery.DiscoveryFragment;
import com.group6.vietravel.features.ui.main.journey.favoriteLocation.FavoriteLocationViewModel;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private MainViewModel mViewModel;
    private BottomNavigationView navView;
    public String pendingPlaceId = null;

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        setIntent(intent);

        String placeId = intent.getStringExtra("TARGET_LOCATION_ID");

        if (placeId != null) {
            this.pendingPlaceId = placeId;
            navView.setSelectedItemId(R.id.navigation_discovery);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.container), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right,0);
            return insets;
        });

        navView = findViewById(R.id.nav_view);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_discovery, R.id.navigation_ranking, R.id.navigation_journey, R.id.navigation_account, R.id.navigation_chatbot)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupWithNavController(binding.navView, navController);

        mViewModel = new ViewModelProvider(this).get(MainViewModel.class);

        String placeId = getIntent().getStringExtra("TARGET_LOCATION_ID");

        if (placeId != null) {
            Fragment foundFragment = getSupportFragmentManager().findFragmentById(R.id.map_fragment);

            if (foundFragment instanceof DiscoveryFragment) {
                ((DiscoveryFragment) foundFragment).highlightLocation(placeId);
            }
        }

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