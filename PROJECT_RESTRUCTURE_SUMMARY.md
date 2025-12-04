# 📂 CẤU TRÚC THƯ MỤC ĐÃ TỔ CHỨC LẠI

## ✅ CÁC THAY ĐỔI ĐÃ THỰC HIỆN

### 1. **Sửa lỗi chính tả**
- ❌ `data/repositorys/` → ✅ `data/repositories/`

### 2. **Tạo Core Layer**
```
core/
├── base/                    (Base classes - sẽ thêm sau)
├── constants/               (✅ ĐÃ TẠO)
│   ├── AppConstants.java
│   ├── FirebaseConstants.java
│   └── IntentConstants.java
└── utils/                   (✅ DI CHUYỂN từ root)
    ├── PlaceUtils.java
    ├── ProvinceUtils.java
    └── UserUtils.java
```

### 3. **Tổ chức Data Layer theo Domain**
```
data/
├── models/                  (✅ PHÂN LOẠI)
│   ├── user/
│   │   ├── User.java
│   │   ├── Favorite.java
│   │   └── VisitedPlace.java
│   ├── place/
│   │   ├── Place.java
│   │   ├── Category.java
│   │   ├── Province.java
│   │   └── District.java
│   └── review/
│       └── Review.java
│
└── repositories/            (✅ PHÂN LOẠI)
    ├── auth/
    │   └── AuthRepository.java
    ├── place/
    │   └── PlaceRepository.java
    └── review/
        └── ReviewRepository.java
```

### 4. **Tổ chức Features (UI) Layer**
```
features/                    (✅ MỚI - đổi tên từ ui/)
└── ui/
    ├── adapters/            (✅ DI CHUYỂN)
    │   ├── PlaceAdapter.java
    │   ├── RankingAdapter.java
    │   ├── ReviewAdapter.java
    │   └── ReviewPlaceAdapter.java
    │
    ├── auth/
    │   ├── LoginActivity.java
    │   ├── RegisterActivity.java
    │   └── AuthViewModel.java
    │
    ├── main/
    │   ├── MainActivity.java
    │   ├── MainViewModel.java
    │   ├── discovery/
    │   ├── ranking/
    │   ├── journey/
    │   ├── account/
    │   ├── chatbot/
    │   └── dialog/
    │
    ├── detail/
    │   ├── DetailActivity.java
    │   └── DetailViewModel.java
    │
    └── search/
        ├── SearchActivity.java
        └── SearchViewModel.java
```

### 5. **Admin Module (Không đổi)**
```
admin/
├── core/                    (Sẽ thêm sau)
├── data/
│   ├── models/
│   │   └── Notification.java
│   └── repositories/
│       ├── AdminPlaceRepository.java
│       ├── AdminReviewRepository.java
│       ├── AdminUserRepository.java
│       └── AdminNotificationRepository.java
│
├── ui/
│   ├── auth/
│   ├── main/
│   ├── places/             (NGƯỜI 1)
│   ├── reviews/            (NGƯỜI 2)
│   ├── users/              (NGƯỜI 2)
│   └── notifications/      (NGƯỜI 3)
│
└── utils/
    └── AdminAuthUtils.java
```

---

## 📊 CẤU TRÚC HOÀN CHỈNH

```
com.group6.vietravel/
│
├── 🏗️ core/                      [FOUNDATION LAYER]
│   ├── base/                     (Base classes)
│   ├── constants/                ✅ Constants
│   └── utils/                    ✅ Utility classes
│
├── 💾 data/                      [DATA LAYER]
│   ├── models/                   ✅ Organized by domain
│   │   ├── user/
│   │   ├── place/
│   │   └── review/
│   └── repositories/             ✅ Organized by domain
│       ├── auth/
│       ├── place/
│       └── review/
│
├── 🎨 features/                  [FEATURE LAYER]
│   └── ui/
│       ├── adapters/             ✅ Centralized adapters
│       ├── auth/
│       ├── main/
│       ├── detail/
│       └── search/
│
└── 🔐 admin/                     [ADMIN MODULE]
    ├── data/
    ├── ui/
    └── utils/
```

---

## 🎯 LỢI ÍCH

### ✅ **Trước khi refactor:**
```
❌ repositorys (sai chính tả)
❌ models flat (khó tìm)
❌ repositories flat
❌ adapters ở root level
❌ ui/ không rõ ràng
❌ không có constants
```

### ✅ **Sau khi refactor:**
```
✅ repositories (đúng chính tả)
✅ models theo domain (user/place/review)
✅ repositories theo domain
✅ adapters trong features/ui/adapters
✅ features/ rõ ràng hơn
✅ core/constants/ đầy đủ
```

---

## 🔄 CÁC FILE CẦN CẬP NHẬT IMPORT

Sau khi refactor, cần update imports trong các file:

### **1. Files sử dụng models:**
```java
// CŨ:
import com.group6.vietravel.data.models.User;
import com.group6.vietravel.data.models.Place;
import com.group6.vietravel.data.models.Review;

// MỚI:
import com.group6.vietravel.data.models.user.User;
import com.group6.vietravel.data.models.place.Place;
import com.group6.vietravel.data.models.review.Review;
```

### **2. Files sử dụng repositories:**
```java
// CŨ:
import com.group6.vietravel.data.repositories.AuthRepository;
import com.group6.vietravel.data.repositories.PlaceRepository;

// MỚI:
import com.group6.vietravel.data.repositories.auth.AuthRepository;
import com.group6.vietravel.data.repositories.place.PlaceRepository;
```

### **3. Files sử dụng UI classes:**
```java
// CŨ:
import com.group6.vietravel.ui.auth.LoginActivity;
import com.group6.vietravel.adapters.PlaceAdapter;

// MỚI:
import com.group6.vietravel.features.ui.auth.LoginActivity;
import com.group6.vietravel.features.ui.adapters.PlaceAdapter;
```

### **4. Files sử dụng utils:**
```java
// CŨ:
import com.group6.vietravel.utils.PlaceUtils;

// MỚI:
import com.group6.vietravel.core.utils.PlaceUtils;
```

---

## 🚀 BƯỚC TIẾP THEO

### **Priority 1: Fix Imports (BẮT BUỘC)**
Chạy Find & Replace trong Android Studio:

1. **Models imports:**
   - Find: `import com.group6.vietravel.data.models.User;`
   - Replace: `import com.group6.vietravel.data.models.user.User;`
   - (Tương tự cho Place, Review, Category, Province, District, Favorite, VisitedPlace)

2. **Repositories imports:**
   - Find: `import com.group6.vietravel.data.repositories.AuthRepository;`
   - Replace: `import com.group6.vietravel.data.repositories.auth.AuthRepository;`
   - (Tương tự cho PlaceRepository, ReviewRepository)

3. **UI imports:**
   - Find: `import com.group6.vietravel.ui.`
   - Replace: `import com.group6.vietravel.features.ui.`

4. **Adapters imports:**
   - Find: `import com.group6.vietravel.adapters.`
   - Replace: `import com.group6.vietravel.features.ui.adapters.`

5. **Utils imports:**
   - Find: `import com.group6.vietravel.utils.`
   - Replace: `import com.group6.vietravel.core.utils.`

### **Priority 2: Thêm Base Classes (KHI CÓ THỜI GIAN)**
```
core/base/
├── BaseActivity.java
├── BaseFragment.java
├── BaseViewModel.java
└── BaseAdapter.java
```

### **Priority 3: Tổ chức Layouts (KHI CÓ THỜI GIAN)**
```
res/layout/
├── user/
│   ├── activity/
│   ├── fragment/
│   └── item/
└── admin/
    ├── activity/
    ├── fragment/
    └── item/
```

---

## 📝 GHI CHÚ

- ✅ Tất cả thay đổi đã được track bằng `git mv`
- ✅ Không mất code
- ⚠️ CẦN rebuild project sau khi fix imports
- ⚠️ CẦN test lại toàn bộ app

---

## 🆘 TROUBLESHOOTING

### **Lỗi: Cannot resolve symbol**
→ Chưa fix imports, chạy Find & Replace như hướng dẫn trên

### **Lỗi: Class not found**
→ Clean & Rebuild project:
```bash
./gradlew clean build
```

### **Lỗi: Git conflicts**
→ Đã dùng `git mv`, không có conflict

---

**✅ CẤU TRÚC ĐÃ ĐƯỢC TỔ CHỨC LẠI LOGIC VÀ CHUẨN HƠN!**
