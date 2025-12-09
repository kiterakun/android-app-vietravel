package com.group6.vietravel.admin.ui.places;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.group6.vietravel.R;
import com.group6.vietravel.admin.ui.places.PhotoAdapter;
import com.group6.vietravel.data.models.place.District;
import com.group6.vietravel.data.models.place.Place;
import com.group6.vietravel.data.models.place.Province;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import com.google.android.libraries.places.api.model.Place.Field;

public class AdminPlaceEditorActivity extends AppCompatActivity {

    private PlaceManagementViewModel viewModel;
    private Place currentPlace; // Nếu null là Thêm mới, không null là Edit
    private PhotoAdapter photoAdapter;
    private List<Uri> newImageUris = new ArrayList<>(); // Ảnh mới chọn

    // Views
    private TextInputEditText etName, etAddress, etDesc, etLat, etLng;
    private TextInputEditText etPhone, etWebsite;
    private Spinner spinnerCategory, spinnerPrice, spinnerProvince, spinnerDistrict;
    private View loadingOverlay;

    // Data cứng cho Spinner (Trong thực tế nên load từ resource hoặc DB)
    private final String[] CATEGORIES = {"Vui chơi", "Ăn uống", "Tâm linh", "Thiên nhiên", "Lịch sử", "Mua sắm"};
    private final String[] CAT_IDS = {"entertainment", "food", "religious", "nature", "historical", "shopping"};
    private final String[] PRICES = {"Thấp", "Trung bình", "Cao"};

    private final List<EditText> etOpeningHoursList = new ArrayList<>();
    private List<Province> provinceList = new ArrayList<>();
    private List<District> districtList = new ArrayList<>();
    private ArrayAdapter<Province> provinceAdapter;
    private ArrayAdapter<District> districtAdapter;

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

        // Khởi tạo Google Places (Chỉ init 1 lần)
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), getString(R.string.google_maps_key));
        }

        viewModel = new ViewModelProvider(this).get(PlaceManagementViewModel.class);
        viewModel.resetStatus();

        initViews();
        loadLocationData(); // Đọc file data.json
        setupSpinners();
        setupPlacesAutocomplete(); // Cấu hình gợi ý địa chỉ
        setupRecyclerView();

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
        // Nút Back: finish() sẽ đóng Activity và xóa dữ liệu form
        toolbar.setNavigationOnClickListener(v -> finish());

        etName = findViewById(R.id.et_name);
        etAddress = findViewById(R.id.et_address);
        etDesc = findViewById(R.id.et_description);
        etLat = findViewById(R.id.et_lat);
        etLng = findViewById(R.id.et_lng);
        etPhone = findViewById(R.id.et_phone);
        etWebsite = findViewById(R.id.et_website);

        spinnerCategory = findViewById(R.id.spinner_category);
        spinnerPrice = findViewById(R.id.spinner_price);
        spinnerProvince = findViewById(R.id.spinner_province);
        spinnerDistrict = findViewById(R.id.spinner_district);
        loadingOverlay = findViewById(R.id.loading_overlay);

        // Map các EditText giờ mở cửa
        etOpeningHoursList.add(findViewById(R.id.et_hour_mon));
        etOpeningHoursList.add(findViewById(R.id.et_hour_tue));
        etOpeningHoursList.add(findViewById(R.id.et_hour_wed));
        etOpeningHoursList.add(findViewById(R.id.et_hour_thu));
        etOpeningHoursList.add(findViewById(R.id.et_hour_fri));
        etOpeningHoursList.add(findViewById(R.id.et_hour_sat));
        etOpeningHoursList.add(findViewById(R.id.et_hour_sun));

        findViewById(R.id.btn_add_photo).setOnClickListener(v -> pickImageLauncher.launch("image/*"));
        findViewById(R.id.btn_save).setOnClickListener(v -> savePlace());
    }

    // Đọc file data.json từ thư mục assets
    private void loadLocationData() {
        try {
            InputStream is = getAssets().open("data.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String json = new String(buffer, StandardCharsets.UTF_8);

            Gson gson = new Gson();
            Type listType = new TypeToken<List<Province>>() {}.getType();
            provinceList = gson.fromJson(json, listType);

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Lỗi đọc dữ liệu Tỉnh/Thành", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupSpinners() {
        // 1. Category & Price (Cố định)
        ArrayAdapter<String> catAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, CATEGORIES);
        catAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerCategory.setAdapter(catAdapter);

        ArrayAdapter<String> priceAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, PRICES);
        priceAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerPrice.setAdapter(priceAdapter);

        // 2. Province (Lấy từ json)
        provinceAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, provinceList);
        provinceAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerProvince.setAdapter(provinceAdapter);

        // 3. District (Thay đổi theo Province)
        districtAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, new ArrayList<>());
        districtAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerDistrict.setAdapter(districtAdapter);

        spinnerProvince.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Province selectedProvince = provinceList.get(position);
                districtList.clear();
                if (selectedProvince.getDistricts() != null) {
                    districtList.addAll(selectedProvince.getDistricts());
                }
                districtAdapter.notifyDataSetChanged();

                // Reset District selection khi đổi tỉnh
                spinnerDistrict.setSelection(0);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void setupPlacesAutocomplete() {
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        if (autocompleteFragment != null) {
            // CÁCH SỬA: Dùng ArrayList + Field (đã import ở trên)
            // Cách này máy sẽ hiểu ngay, không bị nhầm với Place của bạn
            java.util.List<Field> fields = new java.util.ArrayList<>();
            fields.add(Field.ID);
            fields.add(Field.DISPLAY_NAME);
            fields.add(Field.FORMATTED_ADDRESS);
            fields.add(Field.LOCATION);

            autocompleteFragment.setPlaceFields(fields);

            autocompleteFragment.setHint("Nhập để tìm kiếm địa chỉ...");
            autocompleteFragment.setCountries("VN");

            autocompleteFragment.setOnPlaceSelectedListener(new com.google.android.libraries.places.widget.listener.PlaceSelectionListener() {
                @Override
                public void onPlaceSelected(@NonNull com.google.android.libraries.places.api.model.Place googlePlace) {
                    // Xử lý dữ liệu trả về
                    if (googlePlace.getFormattedAddress() != null) {
                        etAddress.setText(googlePlace.getFormattedAddress());
                    }
                    if (googlePlace.getLocation() != null) {
                        etLat.setText(String.valueOf(googlePlace.getLocation().latitude));
                        etLng.setText(String.valueOf(googlePlace.getLocation().longitude));
                    }
                }

                @Override
                public void onError(@androidx.annotation.NonNull com.google.android.gms.common.api.Status status) {
                    Log.i("Places", "Lỗi: " + status);
                    Toast.makeText(AdminPlaceEditorActivity.this, "Lỗi: " + status.getStatusMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void setupRecyclerView() {
        RecyclerView recyclerPhotos = findViewById(R.id.recycler_photos);
        photoAdapter = new PhotoAdapter(this, position -> {
            Object item = photoAdapter.getCurrentPhotos().get(position);
            if (item instanceof Uri) {
                newImageUris.remove(item);
            } else if (item instanceof String) {
                if (currentPlace.getGalleryUrls() != null) {
                    currentPlace.getGalleryUrls().remove(item);
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
        etPhone.setText(currentPlace.getPhoneNumber());
        etWebsite.setText(currentPlace.getWebsiteUri());

        // Fill Opening Hours
        List<String> hours = currentPlace.getOpeningHours();
        if (hours != null && !hours.isEmpty()) {
            for (int i = 0; i < Math.min(hours.size(), etOpeningHoursList.size()); i++) {
                etOpeningHoursList.get(i).setText(hours.get(i));
            }
        }

        // Set Categories/Price (Code cũ)
        for (int i = 0; i < CAT_IDS.length; i++) {
            if (CAT_IDS[i].equals(currentPlace.getCategoryId())) {
                spinnerCategory.setSelection(i);
                break;
            }
        }
        for (int i = 0; i < PRICES.length; i++) {
            if (PRICES[i].equals(currentPlace.getPriceRange())) {
                spinnerPrice.setSelection(i);
                break;
            }
        }

        // Set Province/District (Khó hơn chút vì phải dò ID)
        // Lưu ý: Cần đảm bảo data.json đã load xong.
        if (currentPlace.getProvince() != null) {
            for (int i = 0; i < provinceList.size(); i++) {
                // So sánh theo mã code hoặc tên (tùy data bạn lưu là gì).
                // Ở đây giả sử lưu mã code dạng String
                if (String.valueOf(provinceList.get(i).getCode()).equals(currentPlace.getProvince())) {
                    spinnerProvince.setSelection(i);
                    // Sau khi set Province, districtAdapter sẽ cập nhật
                    // Ta cần chờ UI update hoặc set District thủ công ở đây (phức tạp)
                    // Để đơn giản, ta chỉ set Province.
                    break;
                }
            }
        }

        photoAdapter.setPhotos(currentPlace.getGalleryUrls());
    }

    private void savePlace() {
        // 1. Validate Tên
        String name = etName.getText().toString().trim();
        if (name.isEmpty()) {
            etName.setError("Tên không được để trống");
            etName.requestFocus();
            return;
        }

        // 2. Validate Tọa độ (Quan trọng)
        double lat = 0;
        double lng = 0;
        try {
            lat = Double.parseDouble(etLat.getText().toString());
            lng = Double.parseDouble(etLng.getText().toString());
        } catch (NumberFormatException e) {
            etLat.setError("Tọa độ không hợp lệ");
            etLng.setError("Tọa độ không hợp lệ");
            Toast.makeText(this, "Vui lòng kiểm tra lại tọa độ", Toast.LENGTH_SHORT).show();
            return; // Dừng lại, không lưu
        }

        loadingOverlay.setVisibility(View.VISIBLE);

        // 3. Fill data cơ bản
        currentPlace.setName(name);
        currentPlace.setAddress(etAddress.getText().toString());
        currentPlace.setDescription(etDesc.getText().toString());
        currentPlace.setPhoneNumber(etPhone.getText().toString());
        currentPlace.setWebsiteUri(etWebsite.getText().toString());
        currentPlace.setLatitude(lat);
        currentPlace.setLongitude(lng);

        // 4. Xử lý Spinner an toàn (Tránh crash index -1)
        int catPos = spinnerCategory.getSelectedItemPosition();
        if (catPos != -1 && catPos < CAT_IDS.length) {
            currentPlace.setCategoryId(CAT_IDS[catPos]);
        } else {
            // Mặc định hoặc báo lỗi
            currentPlace.setCategoryId(CAT_IDS[0]);
        }

        int pricePos = spinnerPrice.getSelectedItemPosition();
        if (pricePos != -1 && pricePos < PRICES.length) {
            currentPlace.setPriceRange(PRICES[pricePos]);
        } else {
            currentPlace.setPriceRange(PRICES[0]);
        }

        // 5. Xử lý Province/District an toàn
        Object selectedProv = spinnerProvince.getSelectedItem();
        if (selectedProv instanceof Province) {
            currentPlace.setProvince(String.valueOf(((Province) selectedProv).getCode()));
        }

        Object selectedDist = spinnerDistrict.getSelectedItem();
        if (selectedDist instanceof District) {
            currentPlace.setDistrict(String.valueOf(((District) selectedDist).getCode()));
        }

        // 6. Lấy Opening Hours
        List<String> hoursList = new ArrayList<>();
        for (EditText et : etOpeningHoursList) {
            String h = et.getText().toString().trim();
            hoursList.add(h.isEmpty() ? "Đóng cửa" : h);
        }
        currentPlace.setOpeningHours(hoursList);

        // 7. Gọi ViewModel
        if (currentPlace.getPlaceId() == null) {
            currentPlace.setApproved(true);
            viewModel.addPlace(currentPlace, newImageUris);
        } else {
            viewModel.updatePlace(currentPlace, newImageUris);
        }
    }
    private void observeViewModel() {
        viewModel.getOperationSuccess().observe(this, success -> {
            loadingOverlay.setVisibility(View.GONE);
            if (success) {
                finish();
            }
        });
        viewModel.getError().observe(this, error -> {
            loadingOverlay.setVisibility(View.GONE);
            if(error != null) Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
        });
    }
}