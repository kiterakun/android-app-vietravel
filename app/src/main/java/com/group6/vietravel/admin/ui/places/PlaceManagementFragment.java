package com.group6.vietravel.admin.ui.places;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.button.MaterialButton;
import com.group6.vietravel.R;
import com.group6.vietravel.admin.ui.places.AdminPlaceAdapter;
import com.group6.vietravel.data.models.place.Place;

import java.util.ArrayList;
import java.util.List;

// [QUAN TRỌNG] Thêm implements OnMapReadyCallback
public class PlaceManagementFragment extends Fragment implements OnMapReadyCallback {

    private PlaceManagementViewModel viewModel;

    // UI components
    private RecyclerView recyclerView;
    private AdminPlaceAdapter adapter;
    private LinearLayout btnAddNew, btnFilter, btnToggleView;
    private EditText searchView;

    // UI cho Toggle (Đổi chữ/Icon)
    private TextView tvScreenTitle, tvToggleText;
    private ImageView imgToggleIcon;

    // UI cho Popup Map
    private CardView cvPopup;
    private TextView tvPopupName, tvPopupAddress;
    private ImageView imgPopupThumb;
    private MaterialButton btnPopupEdit, btnPopupDelete;
    private ImageButton btnClosePopup;

    // Map components
    private GoogleMap mMap;
    private View mapFragmentView;
    private boolean isMapView = false; // Biến cờ: đang xem map hay list?
    private List<Place> currentPlaceList = new ArrayList<>(); // Lưu list để hiển thị lên Map

    private final String[] FILTER_CATS = {"Tất cả", "Vui chơi", "Ăn uống", "Tâm linh", "Thiên nhiên", "Lịch sử"};
    private final String[] FILTER_CAT_IDS = {"all", "entertainment", "food", "spiritual", "nature", "history"};
    private final String[] FILTER_PRICES = {"Tất cả", "Thấp", "Trung bình", "Cao"};

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

        // 2. Bind View (Header & List)
        recyclerView = view.findViewById(R.id.recycler_places);
        btnAddNew = view.findViewById(R.id.btn_add_new);
        btnFilter = view.findViewById(R.id.btn_filter);
        btnToggleView = view.findViewById(R.id.btn_toggle_view);
        searchView = view.findViewById(R.id.search_view);

        // Header Text & Icon
        tvScreenTitle = view.findViewById(R.id.tv_screen_title);
        tvToggleText = view.findViewById(R.id.tv_toggle_text);
        imgToggleIcon = view.findViewById(R.id.img_toggle_icon);

        // Popup View
        cvPopup = view.findViewById(R.id.cv_place_info_popup);
        tvPopupName = view.findViewById(R.id.tv_popup_name);
        tvPopupAddress = view.findViewById(R.id.tv_popup_address);
        imgPopupThumb = view.findViewById(R.id.img_popup_thumb);
        btnPopupEdit = view.findViewById(R.id.btn_popup_edit);
        btnPopupDelete = view.findViewById(R.id.btn_popup_delete);
        btnClosePopup = view.findViewById(R.id.btn_close_popup);

        // 3. Setup Map
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map_fragment);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
            mapFragmentView = mapFragment.getView(); // Lấy view để ẩn/hiện
        }

        // 4. Setup RecyclerView
        setupRecyclerView();

        // 5. Observe Data
        viewModel.getAllPlaces().observe(getViewLifecycleOwner(), places -> {
            currentPlaceList = places; // Lưu lại để dùng cho Map
            adapter.setPlaces(places);
            updateMapMarkers(places); // Cập nhật marker trên map
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
                cvPopup.setVisibility(View.GONE); // Ẩn popup nếu đang hiện
                viewModel.resetStatus();
            }
        });

        // 6. Load data ban đầu
        viewModel.loadAllPlaces();

        // 7. Sự kiện Click
        btnAddNew.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), AdminPlaceEditorActivity.class);
            startActivity(intent);
        });

        searchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterPlaces(s.toString()); // Lọc ngay khi gõ
            }
            @Override
            public void afterTextChanged(Editable s) { }
        });

        btnFilter.setOnClickListener(v -> {
            showFilterDialog();
            Toast.makeText(getContext(), "Chức năng Filter (Coming soon)", Toast.LENGTH_SHORT).show();
        });

        // Toggle Logic: Chuyển đổi Map <-> List
        btnToggleView.setOnClickListener(v -> toggleViewMode());

        // Popup Logic
        btnClosePopup.setOnClickListener(v -> cvPopup.setVisibility(View.GONE));
    }

    private void toggleViewMode() {
        isMapView = !isMapView; // Đổi trạng thái

        if (isMapView) {
            // Hiển thị Map
            recyclerView.setVisibility(View.GONE);
            if (mapFragmentView != null) mapFragmentView.setVisibility(View.VISIBLE);

            // Cập nhật Header
            tvScreenTitle.setText("Bản đồ địa điểm");
            tvToggleText.setText("List");
            imgToggleIcon.setImageResource(R.drawable.ic_discovery); // Hoặc icon list nếu bạn có
        } else {
            // Hiển thị List
            recyclerView.setVisibility(View.VISIBLE);
            if (mapFragmentView != null) mapFragmentView.setVisibility(View.GONE);
            cvPopup.setVisibility(View.GONE); // Ẩn popup map đi

            // Cập nhật Header
            tvScreenTitle.setText("Danh sách địa điểm");
            tvToggleText.setText("Map");
            imgToggleIcon.setImageResource(R.drawable.ic_discovery); // Icon map
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        // Cấu hình Map
        mMap.getUiSettings().setZoomControlsEnabled(true);

        // Mặc định zoom vào VN hoặc TP.HCM
        LatLng hcm = new LatLng(10.762622, 106.660172);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(hcm, 10));

        // Nếu đã có data thì hiển thị luôn
        if (!currentPlaceList.isEmpty()) {
            updateMapMarkers(currentPlaceList);
        }

        // Click vào Marker -> Hiện Popup
        mMap.setOnMarkerClickListener(marker -> {
            Place place = (Place) marker.getTag(); // Lấy data từ tag
            if (place != null) {
                showPopup(place);
            }
            return false;
        });

        // Click ra ngoài -> Ẩn Popup
        mMap.setOnMapClickListener(latLng -> cvPopup.setVisibility(View.GONE));
    }

    private void updateMapMarkers(List<Place> places) {
        if (mMap == null) return;
        mMap.clear();

        for (Place place : places) {
            // Chỉ hiện marker nếu có tọa độ (khác 0)
            if (place.getLatitude() != 0 && place.getLongitude() != 0) {
                LatLng pos = new LatLng(place.getLatitude(), place.getLongitude());
                Marker marker = mMap.addMarker(new MarkerOptions()
                        .position(pos)
                        .title(place.getName()));

                // Gắn object Place vào marker để dùng lại khi click
                if (marker != null) marker.setTag(place);
            }
        }
    }

    private void showPopup(Place place) {
        cvPopup.setVisibility(View.VISIBLE);
        tvPopupName.setText(place.getName());
        tvPopupAddress.setText(place.getAddress());

        // Load ảnh thumbnail (dùng Glide)
        if (place.getThumbnailUrl() != null && !place.getThumbnailUrl().isEmpty()) {
            Glide.with(this).load(place.getThumbnailUrl()).into(imgPopupThumb);
        } else {
            imgPopupThumb.setImageResource(R.drawable.ic_launcher_background);
        }

        // Gán sự kiện cho nút Sửa/Xóa trong Popup
        btnPopupEdit.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), AdminPlaceEditorActivity.class);
            intent.putExtra("place_data", place);
            startActivity(intent);
        });

        btnPopupDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(getContext())
                    .setTitle("Xóa địa điểm")
                    .setMessage("Bạn chắc chắn muốn xóa " + place.getName() + "?")
                    .setPositiveButton("Xóa", (dialog, which) -> viewModel.deletePlace(place.getPlaceId()))
                    .setNegativeButton("Hủy", null)
                    .show();
        });
    }

    private void setupRecyclerView() {
        adapter = new AdminPlaceAdapter(getContext(), new AdminPlaceAdapter.OnPlaceActionListener() {
            @Override
            public void onEdit(Place place) {
                Intent intent = new Intent(getContext(), AdminPlaceEditorActivity.class);
                intent.putExtra("place_data", place);
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

    // Hàm lọc danh sách theo từ khóa
    private void filterPlaces(String text) {
        List<Place> fullList = viewModel.getAllPlaces().getValue();
        if (fullList == null) return;

        List<Place> filteredList = new ArrayList<>();

        // Nếu ô tìm kiếm rỗng -> Lấy tất cả
        if (text == null || text.isEmpty()) {
            filteredList.addAll(fullList);
        } else {
            String query = text.toLowerCase().trim();
            for (Place item : fullList) {
                // Lọc theo Tên hoặc Địa chỉ
                if (item.getName().toLowerCase().contains(query) ||
                        item.getAddress().toLowerCase().contains(query)) {
                    filteredList.add(item);
                }
            }
        }

        // Cập nhật lại UI (Cả List và Map)
        currentPlaceList = filteredList; // Cập nhật biến toàn cục
        adapter.setPlaces(filteredList);
        updateMapMarkers(filteredList);
    }

    private void showFilterDialog() {
        com.google.android.material.bottomsheet.BottomSheetDialog dialog =
                new com.google.android.material.bottomsheet.BottomSheetDialog(requireContext());

        // Gán layout vừa tạo
        dialog.setContentView(R.layout.dialog_admin_filter);

        // Ánh xạ View trong Dialog
        android.widget.Spinner spinCat = dialog.findViewById(R.id.spinner_filter_category);
        android.widget.Spinner spinPrice = dialog.findViewById(R.id.spinner_filter_price);
        MaterialButton btnApply = dialog.findViewById(R.id.btn_apply_filter);
        MaterialButton btnReset = dialog.findViewById(R.id.btn_reset_filter);

        // Setup dữ liệu cho Spinner
        android.widget.ArrayAdapter<String> catAdapter = new android.widget.ArrayAdapter<>(getContext(), R.layout.spinner_item, FILTER_CATS);
        catAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinCat.setAdapter(catAdapter);

        android.widget.ArrayAdapter<String> priceAdapter = new android.widget.ArrayAdapter<>(getContext(), R.layout.spinner_item, FILTER_PRICES);
        priceAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinPrice.setAdapter(priceAdapter);

        // Nút Áp dụng
        btnApply.setOnClickListener(v -> {
            int catPos = spinCat.getSelectedItemPosition();
            int pricePos = spinPrice.getSelectedItemPosition();

            String selectedCatId = FILTER_CAT_IDS[catPos];
            String selectedPrice = FILTER_PRICES[pricePos];

            applyFilter(selectedCatId, selectedPrice);
            dialog.dismiss();
        });

        // Nút Reset
        btnReset.setOnClickListener(v -> {
            applyFilter("all", "Tất cả");
            dialog.dismiss();
        });

        dialog.show();
    }

    // Hàm xử lý logic lọc
    private void applyFilter(String catId, String price) {
        List<Place> fullList = viewModel.getAllPlaces().getValue();
        if (fullList == null) return;

        List<Place> filtered = new ArrayList<>();

        for (Place p : fullList) {
            boolean matchCat = catId.equals("all") || (p.getCategoryId() != null && p.getCategoryId().equals(catId));
            boolean matchPrice = price.equals("Tất cả") || (p.getPriceRange() != null && p.getPriceRange().equals(price));

            if (matchCat && matchPrice) {
                filtered.add(p);
            }
        }

        // Cập nhật UI
        currentPlaceList = filtered;
        adapter.setPlaces(filtered);
        updateMapMarkers(filtered);

        String message = "Đã lọc: " + filtered.size() + " kết quả";
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }
}