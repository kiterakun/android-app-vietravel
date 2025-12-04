# HƯỚNG DẪN TEST ADMIN APP

## 🔧 SETUP BAN ĐẦU

### 1. Tạo tài khoản Admin trên Firebase Console

Truy cập: https://console.firebase.google.com/

**Bước 1: Tạo user trong Authentication**
```
Email: admin@vietravel.com
Password: admin123456
```

**Bước 2: Thêm role admin trong Firestore**
- Vào Firestore Database
- Tìm collection `users`
- Tìm document có UID của admin vừa tạo
- Sửa field `role` từ `"user"` thành `"admin"`

```javascript
// Document trong Firestore
{
  uid: "abc123...",
  username: "Admin User",
  email: "admin@vietravel.com",
  role: "admin",  // ← QUAN TRỌNG
  avatar_url: "...",
  points: 0,
  status: "active",
  created_at: Timestamp
}
```

### 2. Cập nhật AndroidManifest.xml

Thêm admin activities vào manifest (nằm trong thẻ `<application>`):

```xml
<activity
    android:name=".admin.ui.auth.AdminLoginActivity"
    android:exported="true"
    android:label="Admin Login"
    android:theme="@style/Theme.VieTravel">
    <!-- Optional: Thêm intent-filter để có thể launch trực tiếp -->
    <intent-filter>
        <action android:name="android.intent.action.MAIN" />
        <category android:name="android.intent.category.LAUNCHER" />
    </intent-filter>
</activity>

<activity
    android:name=".admin.ui.main.AdminMainActivity"
    android:exported="false"
    android:label="Admin Panel"
    android:theme="@style/Theme.VieTravel" />
```

⚠️ **LƯU Ý**: Có 2 launcher icons sẽ xuất hiện (User App và Admin App). Sau khi test xong có thể xóa intent-filter của AdminLoginActivity.

### 3. Build & Run

```bash
# Sync Gradle
./gradlew clean build

# Install to device/emulator
./gradlew installDebug
```

---

## 🧪 TEST SCENARIOS

### TEST 1: Admin Login ✅

**Steps:**
1. Launch Admin app
2. Nhập email: `admin@vietravel.com`
3. Nhập password: `admin123456`
4. Click "ĐĂNG NHẬP ADMIN"

**Expected Result:**
- ✅ Đăng nhập thành công
- ✅ Chuyển đến AdminMainActivity
- ✅ Drawer menu hiển thị 4 options + Logout

**Test với non-admin account:**
1. Đăng nhập với email user thường
2. ❌ Should show: "Bạn không có quyền Admin!"
3. ❌ Should logout automatically

---

### TEST 2: Navigation Drawer ✅

**Steps:**
1. Sau khi login admin
2. Click hamburger icon (☰) hoặc swipe from left
3. Kiểm tra menu items:
   - Quản lý Địa điểm
   - Kiểm duyệt Review
   - Quản lý User
   - Gửi Thông báo
   - Đăng xuất

**Expected Result:**
- ✅ Drawer opens smoothly
- ✅ Click item → Fragment changes
- ✅ Toolbar title updates
- ✅ Click Logout → Back to login screen

---

### TEST 3: Place Management (Người 1) 🔷

#### Test 3.1: View All Places
**Steps:**
1. Navigate to "Quản lý Địa điểm"
2. Kiểm tra RecyclerView hiển thị

**Expected Result:**
- ✅ Load danh sách places từ Firestore
- ✅ Hiển thị cả approved và pending
- ✅ Mỗi item show: name, address, status badge

#### Test 3.2: Filter Places
**Steps:**
1. Click filter "All" / "Pending" / "Approved"

**Expected Result:**
- ✅ List updates theo filter
- ✅ Pending places có badge màu vàng

#### Test 3.3: Add New Place
**Steps:**
1. Click FAB (+)
2. Fill form:
   - Name: "Test Place"
   - Description: "Test description"
   - Address: "Test address"
   - Province, District, Category
   - Upload 2-3 images
3. Click "Lưu"

**Expected Result:**
- ✅ Place được thêm vào Firestore
- ✅ Ảnh được upload lên Storage
- ✅ List refresh và hiển thị place mới

#### Test 3.4: Edit Place
**Steps:**
1. Click vào 1 place
2. Sửa tên thành "Updated Place"
3. Thêm 1 ảnh mới
4. Click "Lưu"

**Expected Result:**
- ✅ Place được update
- ✅ Ảnh mới được thêm vào gallery

#### Test 3.5: Delete Place
**Steps:**
1. Long press hoặc click menu (⋮)
2. Select "Xóa"
3. Confirm dialog → Yes

**Expected Result:**
- ✅ Show confirm dialog
- ✅ Place bị xóa khỏi Firestore
- ✅ List refresh

#### Test 3.6: Approve Pending Place
**Steps:**
1. Filter "Pending"
2. Click "Duyệt" button

**Expected Result:**
- ✅ Field `approved` → `true`
- ✅ Place di chuyển sang "Approved"
- ✅ Hiển thị trong User App

---

### TEST 4: Review Moderation (Người 2) 🔶

#### Test 4.1: View Pending Reviews
**Steps:**
1. Navigate to "Kiểm duyệt Review"
2. Filter "Pending"

**Expected Result:**
- ✅ Hiển thị reviews có `status = "pending"`
- ✅ Show: user avatar, username, rating, comment
- ✅ Show place name

#### Test 4.2: Approve Single Review
**Steps:**
1. Click "Approve" button (✓) trên 1 review

**Expected Result:**
- ✅ Status → "approved"
- ✅ Review hiển thị trong User App
- ✅ Rating_avg của place được update

#### Test 4.3: Reject Single Review
**Steps:**
1. Click "Reject" button (✗)

**Expected Result:**
- ✅ Status → "rejected"
- ✅ Review không hiển thị trong User App

#### Test 4.4: Bulk Approve
**Steps:**
1. Check 3-5 reviews
2. Click "Approve Selected" ở bottom bar

**Expected Result:**
- ✅ Tất cả selected reviews → approved
- ✅ Show success message

#### Test 4.5: Delete Review
**Steps:**
1. Click delete icon
2. Confirm

**Expected Result:**
- ✅ Review bị xóa khỏi Firestore

---

### TEST 5: User Management (Người 2) 🔶

#### Test 5.1: View All Users
**Steps:**
1. Navigate to "Quản lý User"

**Expected Result:**
- ✅ Hiển thị tất cả users
- ✅ Show: avatar, username, email, role, status, points
- ✅ Status badge với màu sắc:
  - Active: xanh
  - Locked: đỏ
  - Hidden: xám

#### Test 5.2: Search User
**Steps:**
1. Type vào SearchView: "test@gmail.com"

**Expected Result:**
- ✅ Filter users matching email/username

#### Test 5.3: Lock User
**Steps:**
1. Click menu (⋮) → Lock
2. Confirm

**Expected Result:**
- ✅ Status → "locked"
- ✅ User không thể login vào User App
- ✅ Badge đổi màu đỏ

#### Test 5.4: Unlock User
**Steps:**
1. Filter "Locked"
2. Click menu → Unlock

**Expected Result:**
- ✅ Status → "active"
- ✅ User có thể login lại

#### Test 5.5: Hide User (Soft Delete)
**Steps:**
1. Click menu → Hide

**Expected Result:**
- ✅ Status → "hidden"
- ✅ User không hiển thị trong Ranking

#### Test 5.6: Restore Hidden User
**Steps:**
1. Filter "Hidden"
2. Click menu → Restore

**Expected Result:**
- ✅ Status → "active"

#### Test 5.7: Update Points
**Steps:**
1. Click menu → Update Points
2. Enter: 1000
3. Save

**Expected Result:**
- ✅ Field `points` được update
- ✅ Rank có thể thay đổi

#### Test 5.8: Delete User Permanently
**Steps:**
1. Click menu → Delete
2. Confirm with password or 2FA (optional)

**Expected Result:**
- ✅ User document bị xóa
- ✅ Favorites, visited_places cũng bị xóa

---

### TEST 6: Notification (Người 3) 🔵

#### Test 6.1: View Notification History
**Steps:**
1. Navigate to "Gửi Thông báo"

**Expected Result:**
- ✅ Hiển thị danh sách notifications đã gửi
- ✅ Show: title, message preview, target, date

#### Test 6.2: Send to All Users
**Steps:**
1. Click FAB (+)
2. Fill form:
   - Title: "Khuyến mãi mùa hè"
   - Message: "Giảm giá 20% tất cả tour"
   - Target: "All users"
3. Click "Gửi"

**Expected Result:**
- ✅ Notification saved to Firestore
- ✅ Show success toast
- ✅ Notification hiển thị trong history
- ⚠️ FCM push (optional if implemented)

#### Test 6.3: Send to Specific Users
**Steps:**
1. Click FAB
2. Target: "Specific users"
3. Enter UIDs: "uid1,uid2,uid3"
4. Send

**Expected Result:**
- ✅ Saved với `target_user_ids`
- ✅ Only those users receive (if FCM implemented)

#### Test 6.4: Delete from History
**Steps:**
1. Click delete icon trên 1 notification

**Expected Result:**
- ✅ Document bị xóa khỏi Firestore

---

## 🐛 COMMON ISSUES & FIXES

### Issue 1: "Không thể đăng nhập Admin"
**Fix:**
- Kiểm tra Firestore: field `role` phải = `"admin"`
- Check Firebase Console Authentication: user có tồn tại không

### Issue 2: "RecyclerView không hiển thị data"
**Fix:**
- Check Logcat cho Firestore errors
- Verify Firestore Rules cho phép đọc
- Check adapter.updateData() được gọi chưa

### Issue 3: "Upload ảnh bị lỗi"
**Fix:**
- Check Firebase Storage Rules
- Verify permission READ_EXTERNAL_STORAGE
- Check file path và URI

### Issue 4: "Crash khi click navigation items"
**Fix:**
- Ensure all Fragments được import đúng
- Check fragment_container ID match trong XML

---

## 📊 CHECKLIST HOÀN THÀNH

### Người 1 - Places:
- [ ] View all places
- [ ] Filter (All/Pending/Approved)
- [ ] Search places
- [ ] Add new place with images
- [ ] Edit place
- [ ] Delete place
- [ ] Approve pending place
- [ ] Error handling & loading states

### Người 2 - Reviews & Users:
**Reviews:**
- [ ] View all/pending reviews
- [ ] Approve single
- [ ] Reject single
- [ ] Bulk approve/reject
- [ ] Delete review
- [ ] Show user & place info

**Users:**
- [ ] View all users
- [ ] Search by email/username
- [ ] Filter by status
- [ ] Lock/Unlock
- [ ] Hide/Restore
- [ ] Update points
- [ ] Delete user

### Người 3 - Notifications:
- [ ] View history
- [ ] Send to all users
- [ ] Send to specific users
- [ ] Form validation
- [ ] Delete from history
- [ ] (Optional) FCM integration

---

## 🚀 DEPLOYMENT CHECKLIST

Before releasing:
- [ ] Remove AdminLoginActivity launcher intent-filter
- [ ] Update Firestore Security Rules
- [ ] Test with real data (>100 places, reviews, users)
- [ ] Performance test (RecyclerView scroll)
- [ ] Error handling cho tất cả API calls
- [ ] Add loading indicators
- [ ] Add empty state views
- [ ] Test trên nhiều devices
- [ ] Screenshot & video demo

---

**Good luck testing! 🎉**
