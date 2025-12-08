package com.group6.vietravel.ui.main.discovery;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;

import com.google.android.gms.location.LocationServices;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.group6.vietravel.R;
import com.group6.vietravel.data.models.Place;
import com.group6.vietravel.ui.detail.DetailActivity;
import com.group6.vietravel.ui.main.MainActivity;
import com.group6.vietravel.ui.main.MainViewModel;
import com.group6.vietravel.ui.search.SearchActivity;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DiscoveryFragment extends Fragment implements OnMapReadyCallback {

    private DiscoveryViewModel mViewModel;
    private MainViewModel mainViewModel;
    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private static final int REQUEST_CODE_LOCATION_PERMISSION = 1;
    private Map<String, Marker> mMarkerMap;
    private CardView cv_place_info_popup;
    private ImageButton btn_close_popup;
    private ImageView img_popup_thumb;
    private TextView tv_popup_name;
    private RatingBar ratingAvg;
    private TextView tv_popup_rating;
    private TextView tv_popup_review;
    private TextView tv_popup_address;
    private MaterialButton btn_popup_detail;
    private MaterialButton btn_popup_direction;
    private LinearLayout goToPlace;
    private CardView cv_search_bar;

    private Polyline currentPolyline;
    private FloatingActionButton fab_my_location;
    private Marker currSelectLocation;

    public static DiscoveryFragment newInstance() {
        return new DiscoveryFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_discovery, container, false);
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        mViewModel = new ViewModelProvider(this).get(DiscoveryViewModel.class);
        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        mMarkerMap = new HashMap<>();
        cv_place_info_popup = view.findViewById(R.id.cv_place_info_popup);
        btn_close_popup = view.findViewById(R.id.btn_close_popup);
        img_popup_thumb = view.findViewById(R.id.img_popup_thumb);
        tv_popup_name = view.findViewById(R.id.tv_popup_name);
        ratingAvg = view.findViewById(R.id.ratingAvg);
        tv_popup_rating = view.findViewById(R.id.tv_popup_rating);
        tv_popup_review = view.findViewById(R.id.tv_popup_review);
        tv_popup_address = view.findViewById(R.id.tv_popup_address);
        btn_popup_detail = view.findViewById(R.id.btn_popup_detail);
        btn_popup_direction = view.findViewById(R.id.btn_popup_direction);
        fab_my_location = view.findViewById(R.id.fab_my_location);
        goToPlace = view.findViewById(R.id.goToPlace);
        cv_search_bar = view.findViewById(R.id.cv_search_bar);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map_fragment);

        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        mViewModel.getRoutePath().observe(getViewLifecycleOwner(), path -> {
            if (mMap != null) {
                if (currentPolyline != null) currentPolyline.remove();

                currentPolyline = mMap.addPolyline(new PolylineOptions()
                        .addAll(path)
                        .width(12)
                        .color(Color.BLUE)
                        .geodesic(true));

            }
        });

        mViewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
        });

        fab_my_location.setOnClickListener(v->{
            cv_place_info_popup.setVisibility(VISIBLE);
            fab_my_location.setVisibility(GONE);
        });

        cv_search_bar.setOnClickListener(v->{
            Intent intent = new Intent(getContext(), SearchActivity.class);
            startActivity(intent);
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        mViewModel.getPlaceList().observe(this, new Observer<List<Place>>() {
            @Override
            public void onChanged(List<Place> places) {
                addMarkerAllPlace(places);
            }
        });

        getCurrentLocation();

        mainViewModel.getPendingPlaceId().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if(s!=null) {
                    highlightLocation(s);
                    mainViewModel.pendingPlaceId(null);
                }
            }
        });

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(@NonNull Marker marker) {
                showPopupDetail(marker);
                return false;
            }
        });
    }

    private void addMarkerAllPlace(List<Place> places){
        for (Place place : places){
            LatLng position = new LatLng(place.getLatitude(), place.getLongitude());
            Marker marker = mMap.addMarker(new MarkerOptions()
                    .position(position)
                    .title(place.getName()));

            marker.setTag(place);
            mMarkerMap.put(place.getPlaceId(), marker);
        }
    }

    public void highlightLocation(String targetId) {
        Marker targetMarker = mMarkerMap.get(targetId);

        if (targetMarker != null) {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(targetMarker.getPosition(), 15));
            showPopupDetail(targetMarker);
        }
    }

    private void showPopupDetail(Marker marker){
        currSelectLocation = marker;

        goToPlace.setOnClickListener(v->{
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currSelectLocation.getPosition(), 15));
        });
        Place place = (Place) marker.getTag();

        if (place != null) {
            cv_place_info_popup.setVisibility(VISIBLE);

            String imageUrl = place.getThumbnailUrl();
            if (imageUrl != null && !imageUrl.isEmpty()) {
                Glide.with(getContext())
                        .load(imageUrl)
                        .into(img_popup_thumb);
            }

            tv_popup_name.setText(place.getName());
            ratingAvg.setRating(place.getRatingAvg());
            tv_popup_rating.setText(String.valueOf(place.getRatingAvg()));
            tv_popup_review.setText(" ("+String.valueOf(place.getRatingCount())+")");
            tv_popup_address.setText(place.getAddress());

            btn_close_popup.setOnClickListener(v->{
                cv_place_info_popup.setVisibility(GONE);
                fab_my_location.setVisibility(VISIBLE);
            });

            btn_popup_detail.setOnClickListener(v->{
                mainViewModel.selectPlace(place);
            });

            btn_popup_direction.setOnClickListener(v->{
                LatLng destination = new LatLng(place.getLatitude(), place.getLongitude());
                String key = getString(R.string.google_maps_key);

                if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            REQUEST_CODE_LOCATION_PERMISSION
                    );
                    return;
                }
                fusedLocationClient.getLastLocation()
                        .addOnSuccessListener(requireActivity(), location -> {
                            if (location != null) {
                                double lat = location.getLatitude();
                                double lng = location.getLongitude();
                                LatLng currentLatLng = new LatLng(lat, lng);

                                mMap.setMyLocationEnabled(true);

                                mMap.animateCamera(CameraUpdateFactory.newLatLng(currentLatLng));

                                mViewModel.fetchRoute(currentLatLng,destination,key);

                            } else {
                                Toast.makeText(getContext(), "Không thể lấy vị trí hiện tại", Toast.LENGTH_SHORT).show();
                            }
                        });
            });
        }

    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_CODE_LOCATION_PERMISSION
            );
            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(requireActivity(), location -> {
                    if (location != null) {
                        double lat = location.getLatitude();
                        double lng = location.getLongitude();
                        LatLng currentLatLng = new LatLng(lat, lng);

                        mMap.setMyLocationEnabled(true);

                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15));

                    } else {
                        Toast.makeText(getContext(), "Không thể lấy vị trí hiện tại", Toast.LENGTH_SHORT).show();
                    }
                });
    }


}