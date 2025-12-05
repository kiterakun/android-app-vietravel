package com.group6.vietravel.admin.ui.places;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.group6.vietravel.R;
import com.group6.vietravel.admin.ui.places.PhotoAdapter;
import com.group6.vietravel.data.models.place.Place;

import java.util.ArrayList;
import java.util.List;

public class AdminPlaceEditorActivity extends AppCompatActivity {

    private PlaceManagementViewModel viewModel;
    private Place currentPlace; // Nếu null là Thêm mới, không null là Edit
    private PhotoAdapter photoAdapter;
    private List<Uri> newImageUris = new ArrayList<>(); // Ảnh mới chọn

    // Views
    private TextInputEditText etName, etAddress, etDesc, etLat, etLng;
    private Spinner spinnerCategory, spinnerPrice;
    private View loadingOverlay;

    // Data cứng cho Spinner (Trong thực tế nên load từ resource hoặc DB)
    private final String[] CATEGORIES = {"Vui chơi", "Ăn uống", "Tâm linh", "Thiên nhiên", "Lịch sử"};
    private final String[] CAT_IDS = {"entertainment", "food", "spiritual", "nature", "history"};
    private final String[] PRICES = {"Thấp", "Trung bình", "Cao"};

    // Launcher chọn ảnh
    private final ActivityResultLauncher<String> pickImageLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    newImageUris.add(uri);
                    photoAdapter.addPhoto(uri);
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_place_editor);

        viewModel = new ViewModelProvider(this).get(PlaceManagementViewModel.class);

        viewModel.resetStatus();

        initViews();
        setupSpinners();
        setupRecyclerView();

        // Kiểm tra xem là Add hay Edit
        if (getIntent().hasExtra("place_data")) {
            currentPlace = getIntent().getParcelableExtra("place_data");
            setupEditMode();
        } else {
            currentPlace = new Place();
            setupAddMode();
        }

        observeViewModel();
    }

    private void initViews() {
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        etName = findViewById(R.id.et_name);
        etAddress = findViewById(R.id.et_address);
        etDesc = findViewById(R.id.et_description);
        etLat = findViewById(R.id.et_lat);
        etLng = findViewById(R.id.et_lng);
        spinnerCategory = findViewById(R.id.spinner_category);
        spinnerPrice = findViewById(R.id.spinner_price);
        loadingOverlay = findViewById(R.id.loading_overlay);

        MaterialButton btnAddPhoto = findViewById(R.id.btn_add_photo);
        MaterialButton btnSave = findViewById(R.id.btn_save);

        btnAddPhoto.setOnClickListener(v -> pickImageLauncher.launch("image/*"));
        btnSave.setOnClickListener(v -> savePlace());
    }

    private void setupSpinners() {
        ArrayAdapter<String> catAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, CATEGORIES);
        catAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(catAdapter);

        ArrayAdapter<String> priceAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, PRICES);
        priceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPrice.setAdapter(priceAdapter);
    }

    private void setupRecyclerView() {
        RecyclerView recyclerPhotos = findViewById(R.id.recycler_photos);
        photoAdapter = new PhotoAdapter(this, position -> {
            // Xử lý xóa ảnh
            Object item = photoAdapter.getCurrentPhotos().get(position);
            if (item instanceof Uri) {
                newImageUris.remove(item); // Xóa khỏi list ảnh mới
            } else if (item instanceof String) {
                if (currentPlace.getGalleryUrls() != null) {
                    currentPlace.getGalleryUrls().remove(item); // Xóa khỏi list ảnh cũ
                }
            }
            photoAdapter.removePhoto(position);
        });
        recyclerPhotos.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerPhotos.setAdapter(photoAdapter);
    }

    private void setupAddMode() {
        getSupportActionBar().setTitle("Thêm địa điểm mới");
    }

    private void setupEditMode() {
        getSupportActionBar().setTitle("Chỉnh sửa địa điểm");

        etName.setText(currentPlace.getName());
        etAddress.setText(currentPlace.getAddress());
        etDesc.setText(currentPlace.getDescription());
        etLat.setText(String.valueOf(currentPlace.getLatitude()));
        etLng.setText(String.valueOf(currentPlace.getLongitude()));

        // Set spinner Category
        for (int i = 0; i < CAT_IDS.length; i++) {
            if (CAT_IDS[i].equals(currentPlace.getCategoryId())) {
                spinnerCategory.setSelection(i);
                break;
            }
        }

        // Set spinner Price
        for (int i = 0; i < PRICES.length; i++) {
            if (PRICES[i].equals(currentPlace.getPriceRange())) {
                spinnerPrice.setSelection(i);
                break;
            }
        }

        // Load ảnh cũ
        photoAdapter.setPhotos(currentPlace.getGalleryUrls());
    }

    private void savePlace() {
        String name = etName.getText().toString().trim();
        if (name.isEmpty()) {
            etName.setError("Tên không được để trống");
            return;
        }

        loadingOverlay.setVisibility(View.VISIBLE);

        // Fill data vào object Place
        currentPlace.setName(name);
        currentPlace.setAddress(etAddress.getText().toString());
        currentPlace.setDescription(etDesc.getText().toString());

        // Parse tọa độ an toàn
        try {
            currentPlace.setLatitude(Double.parseDouble(etLat.getText().toString()));
            currentPlace.setLongitude(Double.parseDouble(etLng.getText().toString()));
        } catch (NumberFormatException e) {
            currentPlace.setLatitude(0);
            currentPlace.setLongitude(0);
        }

        currentPlace.setCategoryId(CAT_IDS[spinnerCategory.getSelectedItemPosition()]);
        currentPlace.setPriceRange(PRICES[spinnerPrice.getSelectedItemPosition()]);

        // Tạo giờ mở cửa mặc định (nếu chưa có)
        if(currentPlace.getOpeningHours() == null || currentPlace.getOpeningHours().isEmpty()){
            List<String> defaultHours = new ArrayList<>();
            for(int i=0; i<7; i++) defaultHours.add("08:00 - 22:00");
            currentPlace.setOpeningHours(defaultHours);
        }

        // Gọi ViewModel
        if (currentPlace.getPlaceId() == null) {
            // ADD
            currentPlace.setApproved(true); // Admin tạo thì tự duyệt
            viewModel.addPlace(currentPlace, newImageUris);
        } else {
            // UPDATE
            viewModel.updatePlace(currentPlace, newImageUris);
        }
    }

    private void observeViewModel() {
        viewModel.getOperationSuccess().observe(this, success -> {
            loadingOverlay.setVisibility(View.GONE);
            if (success) {
                Toast.makeText(this, "Lưu thành công!", Toast.LENGTH_SHORT).show();
                finish(); // Đóng màn hình
            }
        });

        viewModel.getError().observe(this, error -> {
            loadingOverlay.setVisibility(View.GONE);
            Toast.makeText(this, "Lỗi: " + error, Toast.LENGTH_LONG).show();
        });
    }
}