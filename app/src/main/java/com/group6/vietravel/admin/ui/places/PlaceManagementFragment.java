package com.group6.vietravel.admin.ui.places;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.group6.vietravel.R;
import com.group6.vietravel.admin.ui.places.AdminPlaceAdapter;
import com.group6.vietravel.data.models.place.Place;

import java.util.ArrayList;
import java.util.List;

public class PlaceManagementFragment extends Fragment {

    private PlaceManagementViewModel viewModel;
    private RecyclerView recyclerView;
    private AdminPlaceAdapter adapter;
    private LinearLayout btnAddNew, btnFilter, btnToggleView;
    private SearchView searchView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_place_management, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 1. Init ViewModel
        viewModel = new ViewModelProvider(this).get(PlaceManagementViewModel.class);

        // 2. Bind View
        recyclerView = view.findViewById(R.id.recycler_places);
        btnAddNew = view.findViewById(R.id.btn_add_new);
        btnFilter = view.findViewById(R.id.btn_filter);
        btnToggleView = view.findViewById(R.id.btn_toggle_view);
        searchView = view.findViewById(R.id.search_view);

        // 3. Setup RecyclerView
        setupRecyclerView();

        // 4. Observe Data
        viewModel.getAllPlaces().observe(getViewLifecycleOwner(), places -> {
            adapter.setPlaces(places);
        });

        viewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                viewModel.resetStatus();
            }
        });

        viewModel.getOperationSuccess().observe(getViewLifecycleOwner(), success -> {
            if(success) {
                Toast.makeText(getContext(), "Thao tác thành công", Toast.LENGTH_SHORT).show();
                // Có thể reload data nếu cần, nhưng LiveData thường tự cập nhật
                viewModel.resetStatus();
            }
        });

        // 5. Load data ban đầu
        viewModel.loadAllPlaces();

        // 6. Sự kiện các nút chức năng
        btnAddNew.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), AdminPlaceEditorActivity.class);
            startActivity(intent);
        });

        btnFilter.setOnClickListener(v -> {
            // TODO: Hiển thị dialog filter
            Toast.makeText(getContext(), "Chức năng Filter", Toast.LENGTH_SHORT).show();
        });

        btnToggleView.setOnClickListener(v -> {
            // TODO: Chuyển sang Map View (Nếu làm chung 1 Fragment thì ẩn Recycler hiện MapView)
            Toast.makeText(getContext(), "Chuyển sang Map View", Toast.LENGTH_SHORT).show();
        });
    }

    private void setupRecyclerView() {
        adapter = new AdminPlaceAdapter(getContext(), new AdminPlaceAdapter.OnPlaceActionListener() {
            @Override
            public void onEdit(Place place) {
                Intent intent = new Intent(getContext(), AdminPlaceEditorActivity.class);
                intent.putExtra("place_data", place); // Truyền object sang để sửa
                startActivity(intent);
            }

            @Override
            public void onDelete(Place place) {
                new AlertDialog.Builder(getContext())
                        .setTitle("Xóa địa điểm")
                        .setMessage("Bạn chắc chắn muốn xóa " + place.getName() + "?")
                        .setPositiveButton("Xóa", (dialog, which) -> viewModel.deletePlace(place.getPlaceId()))
                        .setNegativeButton("Hủy", null)
                        .show();
            }

            @Override
            public void onClick(Place place) {
                Toast.makeText(getContext(), "Xem chi tiết: " + place.getName(), Toast.LENGTH_SHORT).show();
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }
}