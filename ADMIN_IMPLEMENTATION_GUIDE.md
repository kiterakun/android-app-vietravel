# HƯỚNG DẪN TRIỂN KHAI ADMIN - PHÂN CHIA CÔNG VIỆC CHO 3 NGƯỜI

## 📋 TỔNG QUAN

Dự án đã được setup cơ bản với:
- ✅ Models & Repositories (AdminPlaceRepository, AdminReviewRepository, AdminUserRepository, AdminNotificationRepository)
- ✅ AdminLoginActivity với role checking
- ✅ AdminAuthUtils để verify admin

## 👥 PHÂN CHIA CÔNG VIỆC

### 🔷 NGƯỜI 1: QUẢN LÝ ĐỊA ĐIỂM (ADF01) - Priority: HIGH

**Nhiệm vụ:**
1. Tạo `PlaceManagementFragment.java` - Fragment hiển thị danh sách địa điểm
2. Tạo `PlaceManagementViewModel.java` - ViewModel kết nối với AdminPlaceRepository
3. Tạo `AddEditPlaceActivity.java` - Activity để thêm/sửa địa điểm
4. Tạo `AdminPlaceAdapter.java` - Adapter cho RecyclerView
5. Tạo layouts:
   - `fragment_place_management.xml` - Layout fragment
   - `activity_add_edit_place.xml` - Form thêm/sửa
   - `item_admin_place.xml` - Item trong RecyclerView

**Chức năng cần implement:**
- ✅ Hiển thị danh sách tất cả địa điểm (approved & pending)
- ✅ Tìm kiếm và lọc địa điểm
- ✅ Thêm địa điểm mới (với upload nhiều ảnh)
- ✅ Sửa thông tin địa điểm
- ✅ Xóa địa điểm (có confirm dialog)
- ✅ Approve/Reject địa điểm pending
- ✅ Xem chi tiết địa điểm

**Code tham khảo từ User App:**
- `PlaceAdapter.java` - Cách hiển thị địa điểm
- `DetailActivity.java` - Xem chi tiết
- `SearchActivity.java` - Tìm kiếm và filter
- `PlaceRepository.java` - Operations với Firestore

**Firestore Operations:**
```java
// Lấy danh sách: adminPlaceRepository.fetchAllPlaces()
// Thêm: adminPlaceRepository.addPlace(place, imageUris)
// Sửa: adminPlaceRepository.updatePlace(place, newImageUris)
// Xóa: adminPlaceRepository.deletePlace(placeId)
// Duyệt: adminPlaceRepository.approvePlace(placeId)
```

---

### 🔶 NGƯỜI 2: KIỂM DUYỆT REVIEW & QUẢN LÝ USER (ADF02 & ADF03) - Priority: HIGH

**Nhiệm vụ:**

#### Part A: Kiểm duyệt Review (ADF02)
1. Tạo `ReviewModerationFragment.java`
2. Tạo `ReviewModerationViewModel.java`
3. Tạo `AdminReviewAdapter.java`
4. Tạo layouts:
   - `fragment_review_moderation.xml`
   - `item_admin_review.xml`

**Chức năng:**
- ✅ Hiển thị danh sách review chờ duyệt (pending)
- ✅ Filter: All/Pending/Approved/Rejected
- ✅ Approve review (nút xanh)
- ✅ Reject review (nút đỏ)
- ✅ Xóa review
- ✅ Bulk actions (chọn nhiều để approve/reject)
- ✅ Xem thông tin place và user của review

#### Part B: Quản lý User (ADF03)
1. Tạo `UserManagementFragment.java`
2. Tạo `UserManagementViewModel.java`
3. Tạo `AdminUserAdapter.java`
4. Tạo layouts:
   - `fragment_user_management.xml`
   - `item_admin_user.xml`
   - `dialog_user_actions.xml`

**Chức năng:**
- ✅ Hiển thị danh sách user
- ✅ Tìm kiếm user theo email/username
- ✅ Filter theo status: Active/Locked/Hidden
- ✅ Lock/Unlock tài khoản
- ✅ Hide/Restore tài khoản (soft delete)
- ✅ Delete tài khoản (có confirm)
- ✅ Xem thống kê user: số review, số check-in, points

**Code tham khảo:**
- `ReviewAdapter.java`, `ReviewPlaceAdapter.java` - Hiển thị reviews
- `AuthRepository.java` - User operations
- `JourneyFragment.java` - User profile display

**Firestore Operations:**
```java
// Reviews
adminReviewRepository.fetchPendingReviews()
adminReviewRepository.approveReview(reviewId)
adminReviewRepository.rejectReview(reviewId)
adminReviewRepository.bulkApproveReviews(reviewIds)

// Users
adminUserRepository.fetchAllUsers()
adminUserRepository.lockUser(userId)
adminUserRepository.unlockUser(userId)
adminUserRepository.hideUser(userId)
adminUserRepository.deleteUser(userId)
```

---

### 🔵 NGƯỜI 3: THÔNG BÁO & ADMIN MAIN (ADF04 + Infrastructure) - Priority: MEDIUM

**Nhiệm vụ:**

#### Part A: Admin Main Activity (Infrastructure)
1. Tạo `AdminMainActivity.java` - Activity chính với NavigationDrawer
2. Tạo `AdminMainViewModel.java`
3. Tạo layouts:
   - `activity_admin_main.xml` (với DrawerLayout)
   - `nav_header_admin.xml` (Header của drawer)
   - `admin_drawer_menu.xml` (Menu items)

**Chức năng:**
- ✅ NavigationDrawer với 4 menu items
- ✅ Toolbar với title động theo fragment
- ✅ Display admin info ở drawer header
- ✅ Logout functionality
- ✅ Fragment container để load 4 fragments

#### Part B: Gửi Thông báo (ADF04)
1. Tạo `NotificationFragment.java` - Hiển thị lịch sử
2. Tạo `NotificationViewModel.java`
3. Tạo `SendNotificationActivity.java` - Form gửi thông báo
4. Tạo `AdminNotificationAdapter.java`
5. Tạo layouts:
   - `fragment_notification.xml`
   - `activity_send_notification.xml`
   - `item_admin_notification.xml`

**Chức năng:**
- ✅ Hiển thị lịch sử thông báo đã gửi
- ✅ Form gửi thông báo mới:
  + Title & Message
  + Target: All users / Specific users
  + Schedule: Send now / Schedule later
- ✅ Xem chi tiết thông báo đã gửi
- ✅ Xóa thông báo khỏi lịch sử
- ⚠️ Firebase Cloud Messaging (FCM) - Optional nếu có thời gian

**Code tham khảo:**
- `MainActivity.java` - Bottom Navigation structure
- `AccountFragment.java` - User info display

**Firestore Operations:**
```java
adminNotificationRepository.fetchNotificationHistory()
adminNotificationRepository.sendNotification(notification)
adminNotificationRepository.deleteNotification(notificationId)
```

---

## 🎨 RESOURCES CHUNG CẦN TẠO

### Colors (values/colors_admin.xml)
```xml
<color name="admin_primary">#FF5722</color>
<color name="admin_primary_dark">#E64A19</color>
<color name="admin_accent">#FFC107</color>
<color name="admin_background">#FAFAFA</color>
<color name="admin_success">#4CAF50</color>
<color name="admin_danger">#F44336</color>
<color name="admin_warning">#FF9800</color>
```

### Strings (values/strings_admin.xml)
```xml
<string name="admin_title">Admin Panel</string>
<string name="admin_place_management">Quản lý Địa điểm</string>
<string name="admin_review_moderation">Kiểm duyệt Review</string>
<string name="admin_user_management">Quản lý User</string>
<string name="admin_notifications">Thông báo</string>
<!-- Thêm các strings khác -->
```

### Drawables
- Tạo icons cho 4 menu items (place, review, user, notification)
- Tạo buttons với states (normal, pressed, disabled)

---

## 📱 MANIFEST UPDATES

Thêm vào `AndroidManifest.xml`:
```xml
<activity
    android:name=".admin.ui.auth.AdminLoginActivity"
    android:exported="true"
    android:label="Admin Login"
    android:theme="@style/Theme.VieTravel" />

<activity
    android:name=".admin.ui.main.AdminMainActivity"
    android:exported="false"
    android:label="Admin Panel"
    android:theme="@style/Theme.VieTravel" />

<activity
    android:name=".admin.ui.places.AddEditPlaceActivity"
    android:exported="false"
    android:theme="@style/Theme.VieTravel" />

<activity
    android:name=".admin.ui.notifications.SendNotificationActivity"
    android:exported="false"
    android:theme="@style/Theme.VieTravel" />
```

---

## 🔐 FIRESTORE SECURITY RULES

Cập nhật rules để chỉ admin mới được thao tác:

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    
    // Helper function
    function isAdmin() {
      return request.auth != null && 
        get(/databases/$(database)/documents/users/$(request.auth.uid)).data.role == 'admin';
    }
    
    // Places - Admin có full quyền
    match /places/{placeId} {
      allow read: if true;
      allow create, update, delete: if isAdmin();
    }
    
    // Reviews - Admin có thể update status
    match /reviews/{reviewId} {
      allow read: if true;
      allow create: if request.auth != null;
      allow update, delete: if isAdmin();
    }
    
    // Users - Admin có thể quản lý
    match /users/{userId} {
      allow read: if request.auth != null;
      allow create: if request.auth.uid == userId;
      allow update, delete: if isAdmin() || request.auth.uid == userId;
    }
    
    // Notifications - Chỉ admin
    match /notifications/{notificationId} {
      allow read, write: if isAdmin();
    }
  }
}
```

---

## 🧪 TESTING CHECKLIST

### Người 1 - Places:
- [ ] Thêm địa điểm mới với 1-5 ảnh
- [ ] Sửa thông tin địa điểm
- [ ] Xóa địa điểm (với confirm)
- [ ] Approve địa điểm pending
- [ ] Tìm kiếm địa điểm
- [ ] Filter theo category/province

### Người 2 - Reviews & Users:
- [ ] Hiển thị reviews pending
- [ ] Approve/Reject review
- [ ] Bulk approve nhiều reviews
- [ ] Lock/Unlock user
- [ ] Hide/Restore user
- [ ] Xem thống kê user

### Người 3 - Notifications & Main:
- [ ] NavigationDrawer hoạt động
- [ ] Switch giữa các fragments
- [ ] Gửi thông báo tới all users
- [ ] Xem lịch sử thông báo
- [ ] Logout admin

---

## 🚀 WORKFLOW

### Phase 1 (Week 1): Setup & Basic UI
- Tất cả 3 người tạo fragments và layouts của mình
- Test navigation và basic display

### Phase 2 (Week 2): Core Functions
- Implement CRUD operations
- Connect với Repositories
- Test trên Firestore

### Phase 3 (Week 3): Polish & Integration
- Handle errors gracefully
- Add loading states
- Confirm dialogs
- Final testing

---

## 📞 SUPPORT

Nếu gặp vấn đề:
1. Check code tham khảo từ User App
2. Xem log Firestore trong Logcat
3. Test trên Firebase Console trước
4. Hỏi nhóm hoặc giảng viên

## 🎯 DELIVERABLES

Mỗi người cần commit:
- [ ] Java files (Activity, Fragment, ViewModel, Adapter)
- [ ] Layout XML files
- [ ] Screenshot của UI đã implement
- [ ] Video demo các chức năng (30-60s)

---

**LƯU Ý QUAN TRỌNG:**
- Dùng `AdminXXXRepository` đã tạo sẵn, KHÔNG tạo mới
- Follow pattern của User App (MVVM architecture)
- Test trên Firebase Emulator trước khi test production
- Tạo tài khoản admin test với role="admin" trong Firestore

**Good luck! 🚀**
