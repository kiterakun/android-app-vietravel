# 📋 PHÂN CÔNG CÔNG VIỆC CHI TIẾT - ADMIN MODULE

## 🎯 TỔNG QUAN

Đã tạo sẵn **Foundation Code** (90% Backend Logic):
- ✅ 4 Repositories (AdminPlaceRepository, AdminReviewRepository, AdminUserRepository, AdminNotificationRepository)
- ✅ 4 ViewModels (với tất cả methods cần thiết)
- ✅ 4 Fragment templates (với TODO lists chi tiết)
- ✅ AdminLoginActivity & AdminMainActivity (hoàn chỉnh)
- ✅ Navigation Drawer setup
- ✅ Resources (colors, strings)
- ✅ Notification Model

**Công việc còn lại:** Chủ yếu là UI/UX và kết nối với ViewModels đã có sẵn.

---

## 👤 NGƯỜI 1: QUẢN LÝ ĐỊA ĐIỂM (ADF01)

### 📁 Files cần làm việc:
```
app/src/main/java/com/group6/vietravel/admin/ui/places/
├── PlaceManagementFragment.java        [SỬA - Đã có template]
├── PlaceManagementViewModel.java       [✅ XONG - Không cần sửa]
├── AddEditPlaceActivity.java           [TẠO MỚI]
└── AdminPlaceAdapter.java              [TẠO MỚI]

app/src/main/res/layout/
├── fragment_place_management.xml       [TẠO MỚI]
├── activity_add_edit_place.xml         [TẠO MỚI]
└── item_admin_place.xml                [TẠO MỚI]
```

### 🎨 Task 1.1: Layout `fragment_place_management.xml`
```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.coordinatorlayout.widget.CoordinatorLayout>
    
    <!-- Top App Bar với Search -->
    <com.google.android.material.appbar.AppBarLayout>
        <androidx.appcompat.widget.SearchView
            android:id="@+id/searchView"
            android:hint="Tìm kiếm địa điểm..." />
    </com.google.android.material.appbar.AppBarLayout>
    
    <!-- Filter Chips -->
    <HorizontalScrollView>
        <com.google.android.material.chip.ChipGroup>
            <Chip text="Tất cả" />
            <Chip text="Chờ duyệt" />
            <Chip text="Đã duyệt" />
        </com.google.android.material.chip.ChipGroup>
    </HorizontalScrollView>
    
    <!-- RecyclerView -->
    <androidx.recyclerview.widget.RecyclerView
        android:id="@+id/recyclerViewPlaces" />
    
    <!-- FAB Add New -->
    <com.google.android.material.floatingactionbutton.FloatingActionButton
        android:id="@+id/fabAddPlace"
        android:src="@drawable/ic_add" />
        
    <!-- Progress Bar -->
    <ProgressBar android:id="@+id/progressBar" />
    
    <!-- Empty State -->
    <TextView 
        android:id="@+id/textEmptyState"
        android:text="Không có địa điểm nào" />
        
</androidx.coordinatorlayout.widget.CoordinatorLayout>
```

### 🎨 Task 1.2: Layout `item_admin_place.xml`
```xml
<com.google.android.material.card.MaterialCardView>
    <LinearLayout android:orientation="horizontal">
        
        <!-- Thumbnail -->
        <ImageView 
            android:id="@+id/imagePlaceThumbnail"
            android:layout_width="80dp"
            android:layout_height="80dp" />
        
        <!-- Info -->
        <LinearLayout android:orientation="vertical">
            <TextView 
                android:id="@+id/textPlaceName"
                android:textSize="16sp"
                android:textStyle="bold" />
            
            <TextView 
                android:id="@+id/textPlaceAddress"
                android:textSize="14sp" />
            
            <!-- Status Badge -->
            <com.google.android.material.chip.Chip
                android:id="@+id/chipStatus"
                android:text="Chờ duyệt"
                style="@style/Widget.MaterialComponents.Chip.Action" />
        </LinearLayout>
        
        <!-- Actions Menu -->
        <ImageButton
            android:id="@+id/buttonMenu"
            android:src="@drawable/ic_more_vert" />
            
    </LinearLayout>
</com.google.android.material.card.MaterialCardView>
```

### 💻 Task 1.3: `AdminPlaceAdapter.java`
```java
public class AdminPlaceAdapter extends RecyclerView.Adapter<AdminPlaceAdapter.ViewHolder> {
    
    private List<Place> places;
    private OnPlaceClickListener listener;
    
    public interface OnPlaceClickListener {
        void onPlaceClick(Place place);
        void onEditClick(Place place);
        void onDeleteClick(Place place);
        void onApproveClick(Place place);
    }
    
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Place place = places.get(position);
        
        holder.textName.setText(place.getName());
        holder.textAddress.setText(place.getAddress());
        
        // Set status badge
        if (place.isApproved()) {
            holder.chipStatus.setText("Đã duyệt");
            holder.chipStatus.setChipBackgroundColorResource(R.color.admin_success);
        } else {
            holder.chipStatus.setText("Chờ duyệt");
            holder.chipStatus.setChipBackgroundColorResource(R.color.admin_warning);
        }
        
        // Load image với Glide
        Glide.with(holder.itemView)
            .load(place.getGalleryUrls().get(0))
            .into(holder.imageThumb);
        
        // Menu click
        holder.buttonMenu.setOnClickListener(v -> showPopupMenu(v, place));
    }
    
    private void showPopupMenu(View view, Place place) {
        PopupMenu popup = new PopupMenu(view.getContext(), view);
        popup.inflate(R.menu.menu_place_actions);
        
        // Hide "Duyệt" nếu đã approved
        if (place.isApproved()) {
            popup.getMenu().findItem(R.id.action_approve).setVisible(false);
        }
        
        popup.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_edit) {
                listener.onEditClick(place);
            } else if (item.getItemId() == R.id.action_delete) {
                listener.onDeleteClick(place);
            } else if (item.getItemId() == R.id.action_approve) {
                listener.onApproveClick(place);
            }
            return true;
        });
        popup.show();
    }
}
```

### 💻 Task 1.4: Complete `PlaceManagementFragment.java`
```java
public class PlaceManagementFragment extends Fragment {
    
    private PlaceManagementViewModel viewModel;
    private AdminPlaceAdapter adapter;
    private RecyclerView recyclerView;
    private SearchView searchView;
    private ChipGroup chipGroup;
    private FloatingActionButton fabAdd;
    private ProgressBar progressBar;
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        // Initialize views
        recyclerView = view.findViewById(R.id.recyclerViewPlaces);
        searchView = view.findViewById(R.id.searchView);
        chipGroup = view.findViewById(R.id.chipGroupFilter);
        fabAdd = view.findViewById(R.id.fabAddPlace);
        progressBar = view.findViewById(R.id.progressBar);
        
        // Setup RecyclerView
        adapter = new AdminPlaceAdapter();
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        
        // Setup ViewModel
        viewModel = new ViewModelProvider(this).get(PlaceManagementViewModel.class);
        
        // Observe data
        viewModel.getAllPlaces().observe(getViewLifecycleOwner(), places -> {
            adapter.updateData(places);
            progressBar.setVisibility(View.GONE);
        });
        
        // Filter chips
        chipGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.chipAll) {
                viewModel.loadAllPlaces();
            } else if (checkedId == R.id.chipPending) {
                viewModel.loadPendingPlaces();
            }
        });
        
        // Search
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.filter(newText);
                return true;
            }
        });
        
        // FAB click
        fabAdd.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), AddEditPlaceActivity.class);
            startActivity(intent);
        });
        
        // Adapter listeners
        adapter.setOnPlaceClickListener(new AdminPlaceAdapter.OnPlaceClickListener() {
            @Override
            public void onEditClick(Place place) {
                Intent intent = new Intent(getContext(), AddEditPlaceActivity.class);
                intent.putExtra("PLACE", place);
                startActivity(intent);
            }
            
            @Override
            public void onDeleteClick(Place place) {
                showDeleteDialog(place);
            }
            
            @Override
            public void onApproveClick(Place place) {
                showApproveDialog(place);
            }
        });
        
        // Load initial data
        viewModel.loadAllPlaces();
    }
    
    private void showDeleteDialog(Place place) {
        new AlertDialog.Builder(getContext())
            .setTitle("Xác nhận xóa")
            .setMessage("Bạn có chắc muốn xóa địa điểm này?")
            .setPositiveButton("Xóa", (dialog, which) -> {
                viewModel.deletePlace(place.getPlaceId());
            })
            .setNegativeButton("Hủy", null)
            .show();
    }
}
```

### 💻 Task 1.5: `AddEditPlaceActivity.java`
```java
public class AddEditPlaceActivity extends AppCompatActivity {
    
    private EditText editName, editDescription, editAddress;
    private Spinner spinnerProvince, spinnerDistrict, spinnerCategory;
    private RecyclerView recyclerViewImages;
    private Button buttonSave;
    
    private Place editingPlace; // null nếu thêm mới
    private List<Uri> selectedImageUris = new ArrayList<>();
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_place);
        
        // Get place from intent (null if adding new)
        editingPlace = getIntent().getParcelableExtra("PLACE");
        
        // Initialize views
        initViews();
        
        // If editing, populate fields
        if (editingPlace != null) {
            populateFields(editingPlace);
            getSupportActionBar().setTitle("Sửa Địa điểm");
        } else {
            getSupportActionBar().setTitle("Thêm Địa điểm");
        }
        
        // Button pick images
        findViewById(R.id.buttonPickImages).setOnClickListener(v -> {
            pickImages();
        });
        
        // Button save
        buttonSave.setOnClickListener(v -> {
            savePlace();
        });
    }
    
    private void pickImages() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(intent, REQUEST_PICK_IMAGES);
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_PICK_IMAGES && resultCode == RESULT_OK) {
            if (data.getClipData() != null) {
                // Multiple images
                for (int i = 0; i < data.getClipData().getItemCount(); i++) {
                    selectedImageUris.add(data.getClipData().getItemAt(i).getUri());
                }
            } else if (data.getData() != null) {
                // Single image
                selectedImageUris.add(data.getData());
            }
            updateImageGrid();
        }
    }
    
    private void savePlace() {
        // Validate inputs
        String name = editName.getText().toString();
        if (name.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập tên", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Create/Update Place object
        Place place = editingPlace != null ? editingPlace : new Place();
        place.setName(name);
        place.setDescription(editDescription.getText().toString());
        // ... set other fields
        
        // Call repository
        AdminPlaceRepository repository = AdminPlaceRepository.getInstance();
        if (editingPlace != null) {
            repository.updatePlace(place, selectedImageUris);
        } else {
            repository.addPlace(place, selectedImageUris);
        }
        
        finish();
    }
}
```

### ⏱️ Ước tính thời gian: **2-3 ngày**

---

## 👤 NGƯỜI 2: KIỂM DUYỆT REVIEW & QUẢN LÝ USER (ADF02 & ADF03)

### Part A: REVIEW MODERATION

### 📁 Files cần làm việc:
```
app/src/main/java/com/group6/vietravel/admin/ui/reviews/
├── ReviewModerationFragment.java       [SỬA - Đã có template]
├── ReviewModerationViewModel.java      [✅ XONG]
└── AdminReviewAdapter.java             [TẠO MỚI]

app/src/main/res/layout/
├── fragment_review_moderation.xml      [TẠO MỚI]
└── item_admin_review.xml               [TẠO MỚI]
```

### 🎨 Task 2.1: `item_admin_review.xml`
```xml
<com.google.android.material.card.MaterialCardView>
    <LinearLayout>
        
        <!-- Checkbox for bulk selection -->
        <CheckBox android:id="@+id/checkboxSelect" />
        
        <!-- User Avatar -->
        <ImageView android:id="@+id/imageUserAvatar" />
        
        <!-- Content -->
        <LinearLayout android:orientation="vertical">
            <TextView android:id="@+id/textUsername" />
            <TextView android:id="@+id/textPlaceName" />
            <RatingBar android:id="@+id/ratingBar" />
            <TextView android:id="@+id/textComment" />
            
            <!-- Status Badge -->
            <Chip android:id="@+id/chipStatus" />
            
            <!-- Date -->
            <TextView android:id="@+id/textDate" />
        </LinearLayout>
        
        <!-- Action Buttons -->
        <LinearLayout>
            <Button 
                android:id="@+id/buttonApprove"
                android:text="Duyệt"
                android:backgroundTint="@color/admin_success" />
            <Button 
                android:id="@+id/buttonReject"
                android:text="Từ chối"
                android:backgroundTint="@color/admin_danger" />
        </LinearLayout>
        
    </LinearLayout>
</com.google.android.material.card.MaterialCardView>
```

### 💻 Task 2.2: `AdminReviewAdapter.java`
```java
public class AdminReviewAdapter extends RecyclerView.Adapter<AdminReviewAdapter.ViewHolder> {
    
    private List<Review> reviews;
    private Set<String> selectedIds = new HashSet<>();
    
    public interface OnReviewActionListener {
        void onApprove(Review review);
        void onReject(Review review);
        void onDelete(Review review);
    }
    
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Review review = reviews.get(position);
        
        // Load user info
        UserUtils.getUserById(review.getUserId(), new UserUtils.OnUserLoadedCallback() {
            @Override
            public void onPlaceLoaded(User user) {
                holder.textUsername.setText(user.getUsername());
                Glide.with(holder.itemView).load(user.getAvatar_url()).into(holder.imageAvatar);
            }
        });
        
        // Load place info
        PlaceUtils.getPlaceById(review.getPlaceId(), new PlaceUtils.OnPlaceLoadedCallback() {
            @Override
            public void onPlaceLoaded(Place place) {
                holder.textPlaceName.setText(place.getName());
            }
        });
        
        holder.ratingBar.setRating(review.getRating());
        holder.textComment.setText(review.getComment());
        
        // Status
        String status = review.getStatus();
        holder.chipStatus.setText(status);
        if ("approved".equals(status)) {
            holder.chipStatus.setChipBackgroundColorResource(R.color.admin_success);
            holder.buttonApprove.setVisibility(View.GONE);
        } else if ("pending".equals(status)) {
            holder.chipStatus.setChipBackgroundColorResource(R.color.admin_warning);
        }
        
        // Checkbox
        holder.checkbox.setChecked(selectedIds.contains(review.getReviewId()));
        holder.checkbox.setOnCheckedChangeListener((v, isChecked) -> {
            if (isChecked) {
                selectedIds.add(review.getReviewId());
            } else {
                selectedIds.remove(review.getReviewId());
            }
            notifySelectionChanged();
        });
        
        // Buttons
        holder.buttonApprove.setOnClickListener(v -> listener.onApprove(review));
        holder.buttonReject.setOnClickListener(v -> listener.onReject(review));
    }
    
    public List<String> getSelectedIds() {
        return new ArrayList<>(selectedIds);
    }
}
```

### Part B: USER MANAGEMENT

### 📁 Files cần làm việc:
```
app/src/main/java/com/group6/vietravel/admin/ui/users/
├── UserManagementFragment.java         [SỬA - Đã có template]
├── UserManagementViewModel.java        [✅ XONG]
└── AdminUserAdapter.java               [TẠO MỚI]

app/src/main/res/layout/
├── fragment_user_management.xml        [TẠO MỚI]
├── item_admin_user.xml                 [TẠO MỚI]
└── dialog_user_actions.xml             [TẠO MỚI]
```

### 🎨 Task 2.3: `item_admin_user.xml`
```xml
<com.google.android.material.card.MaterialCardView>
    <LinearLayout>
        
        <ImageView android:id="@+id/imageAvatar" />
        
        <LinearLayout android:orientation="vertical">
            <TextView android:id="@+id/textUsername" />
            <TextView android:id="@+id/textEmail" />
            
            <LinearLayout>
                <!-- Role Badge -->
                <Chip 
                    android:id="@+id/chipRole"
                    android:text="user" />
                
                <!-- Status Badge -->
                <Chip 
                    android:id="@+id/chipStatus"
                    android:text="Active" />
            </LinearLayout>
            
            <!-- Stats -->
            <LinearLayout>
                <TextView 
                    android:id="@+id/textPoints"
                    android:text="100 điểm" />
                <TextView 
                    android:id="@+id/textReviews"
                    android:text="5 reviews" />
            </LinearLayout>
        </LinearLayout>
        
        <!-- Menu Button -->
        <ImageButton
            android:id="@+id/buttonMenu"
            android:src="@drawable/ic_more_vert" />
            
    </LinearLayout>
</com.google.android.material.card.MaterialCardView>
```

### 💻 Task 2.4: `AdminUserAdapter.java`
```java
public class AdminUserAdapter extends RecyclerView.Adapter<AdminUserAdapter.ViewHolder> {
    
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        User user = users.get(position);
        
        holder.textUsername.setText(user.getUsername());
        holder.textEmail.setText(user.getEmail());
        holder.textPoints.setText(user.getPoints() + " điểm");
        
        // Role chip
        holder.chipRole.setText(user.getRole());
        if ("admin".equals(user.getRole())) {
            holder.chipRole.setChipBackgroundColorResource(R.color.admin_accent);
        }
        
        // Status chip
        String status = user.getStatus();
        holder.chipStatus.setText(status);
        if ("active".equals(status)) {
            holder.chipStatus.setChipBackgroundColorResource(R.color.admin_success);
        } else if ("locked".equals(status)) {
            holder.chipStatus.setChipBackgroundColorResource(R.color.admin_danger);
        } else if ("hidden".equals(status)) {
            holder.chipStatus.setChipBackgroundColorResource(R.color.admin_text_secondary);
        }
        
        // Menu click
        holder.buttonMenu.setOnClickListener(v -> showActionsDialog(user));
    }
    
    private void showActionsDialog(User user) {
        String[] actions;
        if ("active".equals(user.getStatus())) {
            actions = new String[]{"Xem chi tiết", "Khóa tài khoản", "Ẩn", "Cập nhật điểm", "Xóa"};
        } else if ("locked".equals(user.getStatus())) {
            actions = new String[]{"Xem chi tiết", "Mở khóa", "Xóa"};
        } else {
            actions = new String[]{"Xem chi tiết", "Khôi phục", "Xóa"};
        }
        
        new AlertDialog.Builder(context)
            .setTitle("Chọn hành động")
            .setItems(actions, (dialog, which) -> {
                switch (actions[which]) {
                    case "Khóa tài khoản":
                        listener.onLockUser(user);
                        break;
                    case "Mở khóa":
                        listener.onUnlockUser(user);
                        break;
                    // ... handle other actions
                }
            })
            .show();
    }
}
```

### ⏱️ Ước tính thời gian: **3-4 ngày**

---

## 👤 NGƯỜI 3: THÔNG BÁO & ADMIN MAIN (ADF04)

### Part A: ADMIN MAIN ACTIVITY (Already done! ✅)

### Part B: NOTIFICATIONS

### 📁 Files cần làm việc:
```
app/src/main/java/com/group6/vietravel/admin/ui/notifications/
├── NotificationFragment.java           [SỬA - Đã có template]
├── NotificationViewModel.java          [✅ XONG]
├── SendNotificationActivity.java       [TẠO MỚI]
└── AdminNotificationAdapter.java       [TẠO MỚI]

app/src/main/res/layout/
├── fragment_notification.xml           [TẠO MỚI]
├── activity_send_notification.xml      [TẠO MỚI]
└── item_admin_notification.xml         [TẠO MỚI]
```

### 🎨 Task 3.1: `fragment_notification.xml`
```xml
<androidx.coordinatorlayout.widget.CoordinatorLayout>
    
    <!-- RecyclerView for history -->
    <androidx.recyclerview.widget.RecyclerView
        android:id="@+id/recyclerViewNotifications"
        android:layout_width="match_parent"
        android:layout_height="match_parent" />
    
    <!-- FAB to send new notification -->
    <com.google.android.material.floatingactionbutton.FloatingActionButton
        android:id="@+id/fabSendNotification"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_gravity="bottom|end"
        android:layout_margin="16dp"
        android:src="@drawable/ic_send"
        android:contentDescription="Gửi thông báo mới" />
    
    <!-- Empty state -->
    <LinearLayout
        android:id="@+id/layoutEmptyState"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:orientation="vertical"
        android:gravity="center"
        android:visibility="gone">
        
        <ImageView
            android:layout_width="120dp"
            android:layout_height="120dp"
            android:src="@drawable/ic_notifications_black_24dp"
            android:alpha="0.3" />
        
        <TextView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="Chưa có thông báo nào"
            android:textSize="16sp"
            android:layout_marginTop="16dp" />
    </LinearLayout>
    
</androidx.coordinatorlayout.widget.CoordinatorLayout>
```

### 🎨 Task 3.2: `activity_send_notification.xml`
```xml
<ScrollView>
    <LinearLayout android:orientation="vertical" android:padding="16dp">
        
        <!-- Title -->
        <com.google.android.material.textfield.TextInputLayout>
            <com.google.android.material.textfield.TextInputEditText
                android:id="@+id/editTextTitle"
                android:hint="Tiêu đề thông báo"
                android:maxLines="1" />
        </com.google.android.material.textfield.TextInputLayout>
        
        <!-- Message -->
        <com.google.android.material.textfield.TextInputLayout>
            <com.google.android.material.textfield.TextInputEditText
                android:id="@+id/editTextMessage"
                android:hint="Nội dung thông báo"
                android:minLines="4"
                android:gravity="top" />
        </com.google.android.material.textfield.TextInputLayout>
        
        <!-- Target RadioGroup -->
        <TextView 
            android:text="Gửi đến:"
            android:textStyle="bold"
            android:layout_marginTop="16dp" />
        
        <RadioGroup android:id="@+id/radioGroupTarget">
            <RadioButton
                android:id="@+id/radioAllUsers"
                android:text="Tất cả người dùng"
                android:checked="true" />
            <RadioButton
                android:id="@+id/radioSpecificUsers"
                android:text="Người dùng cụ thể" />
        </RadioGroup>
        
        <!-- User IDs Input (hidden by default) -->
        <com.google.android.material.textfield.TextInputLayout
            android:id="@+id/layoutUserIds"
            android:visibility="gone">
            <com.google.android.material.textfield.TextInputEditText
                android:id="@+id/editTextUserIds"
                android:hint="User IDs (phân cách bằng dấu phẩy)" />
        </com.google.android.material.textfield.TextInputLayout>
        
        <!-- Send Button -->
        <Button
            android:id="@+id/buttonSend"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="GỬI THÔNG BÁO"
            android:layout_marginTop="24dp"
            android:backgroundTint="@color/admin_primary" />
        
    </LinearLayout>
</ScrollView>
```

### 🎨 Task 3.3: `item_admin_notification.xml`
```xml
<com.google.android.material.card.MaterialCardView
    android:layout_margin="8dp">
    
    <LinearLayout android:orientation="vertical" android:padding="16dp">
        
        <!-- Header -->
        <LinearLayout android:orientation="horizontal">
            <TextView
                android:id="@+id/textTitle"
                android:layout_width="0dp"
                android:layout_weight="1"
                android:textSize="16sp"
                android:textStyle="bold"
                android:textColor="@color/admin_text_primary" />
            
            <ImageButton
                android:id="@+id/buttonDelete"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:src="@drawable/ic_delete"
                android:background="?attr/selectableItemBackgroundBorderless" />
        </LinearLayout>
        
        <!-- Message Preview -->
        <TextView
            android:id="@+id/textMessage"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_marginTop="8dp"
            android:maxLines="2"
            android:ellipsize="end"
            android:textColor="@color/admin_text_secondary" />
        
        <!-- Footer Info -->
        <LinearLayout 
            android:layout_marginTop="12dp"
            android:orientation="horizontal">
            
            <!-- Target Chip -->
            <com.google.android.material.chip.Chip
                android:id="@+id/chipTarget"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:text="Tất cả"
                style="@style/Widget.MaterialComponents.Chip.Entry" />
            
            <View
                android:layout_width="0dp"
                android:layout_height="1dp"
                android:layout_weight="1" />
            
            <!-- Date -->
            <TextView
                android:id="@+id/textDate"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:textSize="12sp"
                android:textColor="@color/admin_text_secondary" />
        </LinearLayout>
        
    </LinearLayout>
</com.google.android.material.card.MaterialCardView>
```

### 💻 Task 3.4: `SendNotificationActivity.java`
```java
public class SendNotificationActivity extends AppCompatActivity {
    
    private TextInputEditText editTitle, editMessage, editUserIds;
    private RadioGroup radioGroupTarget;
    private RadioButton radioAllUsers, radioSpecificUsers;
    private TextInputLayout layoutUserIds;
    private Button buttonSend;
    
    private NotificationViewModel viewModel;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_notification);
        
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Gửi Thông báo");
        
        // Initialize views
        editTitle = findViewById(R.id.editTextTitle);
        editMessage = findViewById(R.id.editTextMessage);
        editUserIds = findViewById(R.id.editTextUserIds);
        radioGroupTarget = findViewById(R.id.radioGroupTarget);
        radioAllUsers = findViewById(R.id.radioAllUsers);
        radioSpecificUsers = findViewById(R.id.radioSpecificUsers);
        layoutUserIds = findViewById(R.id.layoutUserIds);
        buttonSend = findViewById(R.id.buttonSend);
        
        viewModel = new ViewModelProvider(this).get(NotificationViewModel.class);
        
        // Radio group listener
        radioGroupTarget.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radioSpecificUsers) {
                layoutUserIds.setVisibility(View.VISIBLE);
            } else {
                layoutUserIds.setVisibility(View.GONE);
            }
        });
        
        // Send button
        buttonSend.setOnClickListener(v -> sendNotification());
        
        // Observe result
        viewModel.getSendSuccess().observe(this, success -> {
            if (success) {
                Toast.makeText(this, "Gửi thông báo thành công", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
        
        viewModel.getError().observe(this, error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(this, "Lỗi: " + error, Toast.LENGTH_LONG).show();
            }
        });
    }
    
    private void sendNotification() {
        String title = editTitle.getText().toString().trim();
        String message = editMessage.getText().toString().trim();
        
        // Validation
        if (title.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập tiêu đề", Toast.LENGTH_SHORT).show();
            return;
        }
        
        if (message.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập nội dung", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Determine target
        String targetType = radioAllUsers.isChecked() ? "all" : "specific";
        String targetUserIds = "";
        
        if ("specific".equals(targetType)) {
            targetUserIds = editUserIds.getText().toString().trim();
            if (targetUserIds.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập User IDs", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        
        // Create notification object
        Notification notification = new Notification(title, message, targetType, targetUserIds, null);
        
        // Send
        viewModel.sendNotification(notification);
        
        // Show loading
        buttonSend.setEnabled(false);
        buttonSend.setText("Đang gửi...");
    }
    
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
```

### 💻 Task 3.5: `AdminNotificationAdapter.java`
```java
public class AdminNotificationAdapter extends RecyclerView.Adapter<AdminNotificationAdapter.ViewHolder> {
    
    private List<Notification> notifications;
    private OnNotificationActionListener listener;
    
    public interface OnNotificationActionListener {
        void onDeleteClick(Notification notification);
        void onItemClick(Notification notification);
    }
    
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Notification notification = notifications.get(position);
        
        holder.textTitle.setText(notification.getTitle());
        holder.textMessage.setText(notification.getMessage());
        
        // Target chip
        if ("all".equals(notification.getTargetType())) {
            holder.chipTarget.setText("Tất cả");
            holder.chipTarget.setChipBackgroundColorResource(R.color.admin_info);
        } else {
            holder.chipTarget.setText("Cụ thể");
            holder.chipTarget.setChipBackgroundColorResource(R.color.admin_warning);
        }
        
        // Date format
        if (notification.getSentAt() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            holder.textDate.setText(sdf.format(notification.getSentAt()));
        }
        
        // Delete button
        holder.buttonDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(holder.itemView.getContext())
                .setTitle("Xác nhận xóa")
                .setMessage("Xóa thông báo này khỏi lịch sử?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    listener.onDeleteClick(notification);
                })
                .setNegativeButton("Hủy", null)
                .show();
        });
        
        // Item click to view detail
        holder.itemView.setOnClickListener(v -> listener.onItemClick(notification));
    }
    
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textTitle, textMessage, textDate;
        Chip chipTarget;
        ImageButton buttonDelete;
        
        ViewHolder(View view) {
            super(view);
            textTitle = view.findViewById(R.id.textTitle);
            textMessage = view.findViewById(R.id.textMessage);
            textDate = view.findViewById(R.id.textDate);
            chipTarget = view.findViewById(R.id.chipTarget);
            buttonDelete = view.findViewById(R.id.buttonDelete);
        }
    }
}
```

### 💻 Task 3.6: Complete `NotificationFragment.java`
```java
public class NotificationFragment extends Fragment {
    
    private NotificationViewModel viewModel;
    private AdminNotificationAdapter adapter;
    private RecyclerView recyclerView;
    private FloatingActionButton fabSend;
    private LinearLayout layoutEmptyState;
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_notification, container, false);
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        // Initialize views
        recyclerView = view.findViewById(R.id.recyclerViewNotifications);
        fabSend = view.findViewById(R.id.fabSendNotification);
        layoutEmptyState = view.findViewById(R.id.layoutEmptyState);
        
        // Setup RecyclerView
        adapter = new AdminNotificationAdapter();
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        
        // ViewModel
        viewModel = new ViewModelProvider(this).get(NotificationViewModel.class);
        
        // Observe data
        viewModel.getNotificationHistory().observe(getViewLifecycleOwner(), notifications -> {
            if (notifications == null || notifications.isEmpty()) {
                layoutEmptyState.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            } else {
                layoutEmptyState.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                adapter.updateData(notifications);
            }
        });
        
        // FAB click
        fabSend.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), SendNotificationActivity.class);
            startActivity(intent);
        });
        
        // Adapter listener
        adapter.setOnNotificationActionListener(new AdminNotificationAdapter.OnNotificationActionListener() {
            @Override
            public void onDeleteClick(Notification notification) {
                viewModel.deleteNotification(notification.getNotificationId());
            }
            
            @Override
            public void onItemClick(Notification notification) {
                // Optional: Show detail dialog
                showDetailDialog(notification);
            }
        });
        
        // Load data
        viewModel.loadHistory();
    }
    
    @Override
    public void onResume() {
        super.onResume();
        // Refresh list khi quay lại từ SendNotificationActivity
        viewModel.loadHistory();
    }
    
    private void showDetailDialog(Notification notification) {
        new AlertDialog.Builder(getContext())
            .setTitle(notification.getTitle())
            .setMessage(notification.getMessage() + "\n\n" +
                "Gửi đến: " + notification.getTargetType() + "\n" +
                "Ngày: " + new SimpleDateFormat("dd/MM/yyyy HH:mm").format(notification.getSentAt()))
            .setPositiveButton("OK", null)
            .show();
    }
}
```

### ⏱️ Ước tính thời gian: **2 ngày**

---

## 📝 CHECKLIST TỔNG HỢP

### ✅ Đã hoàn thành (Foundation):
- [x] Tất cả Repositories với full CRUD operations
- [x] Tất cả ViewModels với LiveData
- [x] AdminLoginActivity với role checking
- [x] AdminMainActivity với Navigation Drawer
- [x] Fragment templates với TODO lists
- [x] Resources (colors, strings)
- [x] Notification Model

### 🔨 Cần làm:

**Người 1:**
- [ ] 3 layouts (fragment, activity, item)
- [ ] AdminPlaceAdapter
- [ ] AddEditPlaceActivity
- [ ] Complete PlaceManagementFragment logic
- [ ] Image picker & upload
- [ ] Search & filter

**Người 2:**
- [ ] 5 layouts (2 fragments, 3 items, 1 dialog)
- [ ] AdminReviewAdapter với bulk selection
- [ ] AdminUserAdapter
- [ ] Complete ReviewModerationFragment
- [ ] Complete UserManagementFragment
- [ ] User actions dialog

**Người 3:**
- [ ] 3 layouts (fragment, activity, item)
- [ ] AdminNotificationAdapter
- [ ] SendNotificationActivity
- [ ] Complete NotificationFragment
- [ ] Form validation
- [ ] (Optional) FCM integration

---

## 🚀 WORKFLOW ĐỀ XUẤT

### Week 1: Setup & UI
- Ngày 1-2: Tạo tất cả layouts
- Ngày 3-4: Tạo adapters
- Ngày 5: Test display data

### Week 2: Logic & Integration
- Ngày 1-2: Connect với ViewModels
- Ngày 3-4: Implement actions (approve, delete, etc.)
- Ngày 5: Error handling & loading states

### Week 3: Polish & Testing
- Ngày 1-2: UI polish, dialogs, confirmations
- Ngày 3-4: Full testing theo ADMIN_TESTING_GUIDE.md
- Ngày 5: Bug fixes & demo video

---

## 📞 HỖ TRỢ

Nếu gặp khó khăn:
1. ✅ Check code tham khảo từ User App
2. ✅ Xem ViewModels đã có sẵn - chỉ cần gọi methods
3. ✅ Test từng chức năng nhỏ trước khi integrate
4. ✅ Dùng Log.d() để debug Firestore operations

**Repositories đã có SẴN tất cả methods, chỉ cần gọi!**

Good luck! 🎉
