<div align="center">

# 🌍 VieTravel - Ứng Dụng Du Lịch Việt Nam

<img src="app/src/main/ic_launcher-playstore.png" alt="VieTravel Logo" width="150" height="150" style="border-radius: 25px;">

### 📱 Ứng dụng Android hỗ trợ khám phá và trải nghiệm du lịch Việt Nam

![Android](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)
![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Firebase](https://img.shields.io/badge/Firebase-FFCA28?style=for-the-badge&logo=firebase&logoColor=black)
![Google Maps](https://img.shields.io/badge/Google%20Maps-4285F4?style=for-the-badge&logo=googlemaps&logoColor=white)
![Gemini AI](https://img.shields.io/badge/Gemini%20AI-8E75B2?style=for-the-badge&logo=googlegemini&logoColor=white)
![Groq AI](https://img.shields.io/badge/Groq%20AI-FF6F61?style=for-the-badge&logo=groq&logoColor=white)

![Min SDK](<https://img.shields.io/badge/Min%20SDK-24%20(Android%207.0)-brightgreen?style=flat-square>)
![Target SDK](https://img.shields.io/badge/Target%20SDK-36-blue?style=flat-square)
![Version](https://img.shields.io/badge/Version-1.0.0-orange?style=flat-square)
![License](https://img.shields.io/badge/License-MIT-yellow?style=flat-square)
![Status](https://img.shields.io/badge/Trạng%20thái-Đang%20phát%20triển-red?style=flat-square)

---

**VieTravel** là ứng dụng di động Android được phát triển nhằm hỗ trợ người dùng khám phá, tìm kiếm và lên kế hoạch cho các chuyến du lịch tại Việt Nam. Ứng dụng tích hợp trí tuệ nhân tạo (Gemini AI) để tư vấn du lịch thông minh, cùng với Google Maps để dẫn đường và hiển thị vị trí các địa điểm.

[Tính năng](#-tính-năng-chính) •
[Công nghệ](#%EF%B8%8F-công-nghệ-sử-dụng) •
[Cài đặt](#-cài-đặt-và-chạy-dự-án) •
[Cơ sở dữ liệu](#-cấu-trúc-cơ-sở-dữ-liệu-firebase) •
[Nhóm thực hiện](#-thông-tin-nhóm-thực-hiện)

</div>

---

## 📑 Mục Lục

- [Giới thiệu](#-giới-thiệu)
- [Tính năng chính](#-tính-năng-chính)
- [Công nghệ sử dụng](#%EF%B8%8F-công-nghệ-sử-dụng)
- [Kiến trúc dự án](#-kiến-trúc-dự-án)
- [Cấu trúc thư mục](#-cấu-trúc-thư-mục)
- [Cài đặt và chạy dự án](#-cài-đặt-và-chạy-dự-án)
- [Cấu trúc cơ sở dữ liệu Firebase](#-cấu-trúc-cơ-sở-dữ-liệu-firebase)
- [Thông tin đồ án](#-thông-tin-đồ-án)
- [Thông tin nhóm thực hiện](#-thông-tin-nhóm-thực-hiện)
- [Trạng thái dự án](#-trạng-thái-dự-án)
- [Hướng phát triển tương lai](#-hướng-phát-triển-tương-lai)
- [Giấy phép](#-giấy-phép)

---

## 📖 Giới Thiệu

**VieTravel** là đồ án môn học Phát triển ứng dụng di động, được thực hiện bởi **Nhóm 6**. Ứng dụng được xây dựng trên nền tảng Android bằng ngôn ngữ Java, sử dụng Firebase làm backend và tích hợp nhiều dịch vụ của Google.

### 🎯 Mục tiêu đồ án

- 🗺️ Xây dựng ứng dụng hỗ trợ người dùng **khám phá các địa điểm du lịch** trên khắp Việt Nam
- 🔍 Cung cấp công cụ **tìm kiếm, lọc và xem chi tiết** thông tin các địa điểm
- ⭐ Cho phép người dùng **đánh giá, bình luận** và chia sẻ trải nghiệm du lịch
- ❤️ Hỗ trợ **lưu địa điểm yêu thích** và quản lý lịch sử chuyến đi
- 🤖 Tích hợp **AI Chatbot** (Gemini AI) tư vấn du lịch thông minh
- 📍 Tích hợp **Google Maps** hiển thị vị trí và chỉ đường đến địa điểm
- 🏆 Hệ thống **xếp hạng người dùng** dựa trên điểm tích lũy
- 🛡️ Xây dựng **trang quản trị** (Admin Panel) để quản lý nội dung ứng dụng

---

## ✨ Tính Năng Chính

### 👤 Dành cho Người dùng (User)

| Tính năng                    | Mô tả                                                                           |
| ---------------------------- | ------------------------------------------------------------------------------- |
| 🔐 **Đăng ký / Đăng nhập**   | Xác thực người dùng qua Firebase Authentication (Email/Password)                |
| 🔍 **Tìm kiếm địa điểm**     | Tìm kiếm theo tên, tỉnh/thành phố, quận/huyện với bộ lọc nâng cao               |
| 🏖️ **Duyệt & Khám phá**      | Xem danh sách địa điểm du lịch theo danh mục, tỉnh thành, đề xuất               |
| 📋 **Xem chi tiết địa điểm** | Thông tin đầy đủ: mô tả, hình ảnh, giờ mở cửa, số điện thoại, website, đánh giá |
| ❤️ **Lưu yêu thích**         | Lưu và quản lý danh sách các địa điểm yêu thích                                 |
| 📝 **Đánh giá & Bình luận**  | Viết đánh giá, chấm điểm (1-5 sao) cho các địa điểm đã ghé thăm                 |
| 🗺️ **Google Maps**           | Xem vị trí địa điểm trên bản đồ, chỉ đường từ vị trí hiện tại                   |
| 🤖 **AI Chatbot**            | Trò chuyện với Gemini AI để nhận tư vấn, gợi ý du lịch thông minh               |
| 🏆 **Bảng xếp hạng**         | Hệ thống điểm thưởng và xếp hạng người dùng (Gold, Emerald, Master)             |
| 📍 **Lịch sử chuyến đi**     | Theo dõi và quản lý các địa điểm đã ghé thăm                                    |
| 👤 **Quản lý hồ sơ**         | Chỉnh sửa thông tin cá nhân, thay đổi avatar, đổi mật khẩu                      |
| 🔔 **Thông báo**             | Nhận thông báo từ hệ thống quản trị                                             |

### 🛡️ Dành cho Quản trị viên (Admin)

| Tính năng                  | Mô tả                                               |
| -------------------------- | --------------------------------------------------- |
| 📍 **Quản lý địa điểm**    | Thêm, sửa, xóa, duyệt địa điểm du lịch kèm hình ảnh |
| 👥 **Quản lý người dùng**  | Xem, khóa/mở khóa tài khoản người dùng              |
| ⭐ **Kiểm duyệt đánh giá** | Duyệt, từ chối hoặc xóa các đánh giá từ người dùng  |
| 📢 **Gửi thông báo**       | Tạo và gửi thông báo đến toàn bộ người dùng         |

---

## 🛠️ Công Nghệ Sử Dụng

### 📱 Frontend (Android)

| Công nghệ                                                                                                                | Phiên bản   | Mô tả                         |
| ------------------------------------------------------------------------------------------------------------------------ | ----------- | ----------------------------- |
| ![Java](https://img.shields.io/badge/Java-ED8B00?style=flat-square&logo=openjdk&logoColor=white)                         | JDK 11      | Ngôn ngữ lập trình chính      |
| ![Android](https://img.shields.io/badge/Android%20SDK-3DDC84?style=flat-square&logo=android&logoColor=white)             | API 24 - 36 | Nền tảng phát triển           |
| ![Material](https://img.shields.io/badge/Material%20Design-757575?style=flat-square&logo=materialdesign&logoColor=white) | 3           | Thiết kế giao diện người dùng |
| View Binding                                                                                                             | -           | Liên kết giao diện            |
| Navigation Component                                                                                                     | -           | Điều hướng giữa các màn hình  |
| LiveData & ViewModel                                                                                                     | -           | Quản lý dữ liệu theo vòng đời |

### ☁️ Backend (Firebase)

| Dịch vụ                                                                                                             | Mô tả                                         |
| ------------------------------------------------------------------------------------------------------------------- | --------------------------------------------- |
| ![Firestore](https://img.shields.io/badge/Cloud%20Firestore-FFCA28?style=flat-square&logo=firebase&logoColor=black) | Cơ sở dữ liệu NoSQL thời gian thực            |
| ![Auth](https://img.shields.io/badge/Firebase%20Auth-FFCA28?style=flat-square&logo=firebase&logoColor=black)        | Xác thực người dùng (Email/Password)          |
| ![Storage](https://img.shields.io/badge/Firebase%20Storage-FFCA28?style=flat-square&logo=firebase&logoColor=black)  | Lưu trữ hình ảnh (avatar, địa điểm, đánh giá) |

### 🔌 API & Thư viện bên thứ ba

| Thư viện / API           | Phiên bản | Mô tả                              |
| ------------------------ | --------- | ---------------------------------- |
| Google Maps SDK          | 19.2.0    | Hiển thị bản đồ và vị trí          |
| Google Places API        | 5.1.1     | Tìm kiếm và gợi ý địa điểm         |
| Google Location Services | 21.3.0    | Lấy vị trí người dùng              |
| Gemini AI SDK            | 0.9.0     | Tích hợp AI chatbot tư vấn du lịch |
| Glide                    | 5.0.5     | Tải và cache hình ảnh hiệu quả     |
| OkHttp                   | 5.3.2     | HTTP client cho API tìm đường đi   |
| Gson                     | 2.13.2    | Xử lý dữ liệu JSON                 |
| Firebase BoM             | 34.6.0    | Quản lý phiên bản Firebase         |

### 🏗️ Kiến trúc & Patterns

| Pattern                | Mô tả                     |
| ---------------------- | ------------------------- |
| **MVVM**               | Model - View - ViewModel  |
| **Repository Pattern** | Tách biệt tầng dữ liệu    |
| **ViewBinding**        | Type-safe view references |
| **LiveData Observer**  | Reactive UI updates       |

---

## 🏗 Kiến Trúc Dự Án

```
┌─────────────────────────────────────────────────┐
│                    UI Layer                      │
│  ┌──────────┐  ┌──────────┐  ┌───────────────┐  │
│  │ Activity │  │ Fragment │  │    Adapter     │  │
│  └────┬─────┘  └────┬─────┘  └───────────────┘  │
│       │              │                           │
│  ┌────▼──────────────▼─────┐                     │
│  │       ViewModel         │ ◄── LiveData        │
│  └────────────┬────────────┘                     │
├───────────────┼─────────────────────────────────┤
│               │     Data Layer                   │
│  ┌────────────▼────────────┐                     │
│  │      Repository         │                     │
│  └──┬─────────┬────────┬───┘                     │
│     │         │        │                         │
│  ┌──▼──┐  ┌──▼──┐  ┌──▼──┐                      │
│  │ Auth│  │Fire-│  │Stor-│                       │
│  │     │  │store│  │age  │                       │
│  └─────┘  └─────┘  └─────┘                      │
├─────────────────────────────────────────────────┤
│              Firebase Backend                    │
└─────────────────────────────────────────────────┘
```

---

## 📁 Cấu Trúc Thư Mục

```
📦 android-app-vietravel
├── 📄 build.gradle.kts                    # Cấu hình Gradle root
├── 📄 settings.gradle.kts                 # Cấu hình project settings
├── 📄 gradle.properties                   # Gradle properties
├── 📄 README.md                           # Tài liệu dự án
│
└── 📂 app/
    ├── 📄 build.gradle.kts                # Cấu hình module app
    ├── 📄 google-services.json            # Cấu hình Firebase (không commit)
    │
    └── 📂 src/main/
        ├── 📄 AndroidManifest.xml         # Manifest ứng dụng
        ├── 🖼️ ic_launcher-playstore.png   # Icon ứng dụng
        │
        ├── 📂 assets/
        │   └── 📄 data.json              # Dữ liệu tỉnh/thành & quận/huyện VN
        │
        ├── 📂 java/com/group6/vietravel/
        │   │
        │   ├── 📂 admin/                 # 🛡️ Module Quản trị viên
        │   │   ├── 📂 data/
        │   │   │   ├── 📂 models/        # Models cho Admin
        │   │   │   │   └── Notification.java
        │   │   │   └── 📂 repositories/  # Repositories cho Admin
        │   │   │       ├── AdminNotificationRepository.java
        │   │   │       ├── AdminPlaceRepository.java
        │   │   │       ├── AdminReviewRepository.java
        │   │   │       └── AdminUserRepository.java
        │   │   ├── 📂 ui/
        │   │   │   ├── 📂 main/          # Màn hình chính Admin
        │   │   │   ├── 📂 notifications/ # Quản lý thông báo
        │   │   │   ├── 📂 places/        # Quản lý địa điểm
        │   │   │   ├── 📂 reviews/       # Kiểm duyệt đánh giá
        │   │   │   └── 📂 users/         # Quản lý người dùng
        │   │   └── 📂 utils/             # Tiện ích Admin
        │   │
        │   ├── 📂 core/                  # ⚙️ Module Core
        │   │   ├── 📂 constants/         # Hằng số ứng dụng
        │   │   │   ├── AppConstants.java
        │   │   │   ├── FirebaseConstants.java
        │   │   │   └── IntentConstants.java
        │   │   └── 📂 utils/             # Tiện ích dùng chung
        │   │       ├── GeminiUtils.java   # Tích hợp Gemini AI
        │   │       ├── GroqUtils.java     # Tích hợp Groq AI
        │   │       ├── ImageUtils.java    # Xử lý hình ảnh
        │   │       ├── PlaceUtils.java    # Tiện ích địa điểm
        │   │       ├── ProvinceUtils.java # Tiện ích tỉnh/thành
        │   │       └── UserUtils.java     # Tiện ích người dùng
        │   │
        │   ├── 📂 data/                  # 💾 Tầng dữ liệu
        │   │   ├── 📂 models/            # Data Models
        │   │   │   ├── 📂 ai/            # AI Models
        │   │   │   │   ├── AiResponse.java
        │   │   │   │   └── ChatMessage.java
        │   │   │   ├── 📂 notification/
        │   │   │   │   └── Notification.java
        │   │   │   ├── 📂 place/
        │   │   │   │   ├── Category.java
        │   │   │   │   ├── District.java
        │   │   │   │   ├── Place.java
        │   │   │   │   └── Province.java
        │   │   │   ├── 📂 review/
        │   │   │   │   └── Review.java
        │   │   │   └── 📂 user/
        │   │   │       ├── Favorite.java
        │   │   │       ├── User.java
        │   │   │       └── VisitedPlace.java
        │   │   └── 📂 repositories/      # Data Repositories
        │   │       ├── 📂 auth/          # Xác thực
        │   │       ├── 📂 notification/  # Thông báo
        │   │       ├── 📂 place/         # Địa điểm
        │   │       ├── 📂 review/        # Đánh giá
        │   │       └── 📂 user/          # Người dùng
        │   │
        │   └── 📂 features/             # 🎨 Tầng giao diện
        │       ├── 📂 ui/auth/           # Đăng nhập / Đăng ký
        │       │   ├── AuthViewModel.java
        │       │   ├── LoginActivity.java
        │       │   └── RegisterActivity.java
        │       └── 📂 user/ui/
        │           ├── 📂 adapters/      # RecyclerView Adapters
        │           ├── 📂 detail/        # Chi tiết địa điểm
        │           ├── 📂 main/          # Màn hình chính
        │           │   ├── 📂 account/   # Tài khoản
        │           │   ├── 📂 chatbot/   # AI Chatbot
        │           │   ├── 📂 dialog/    # Bottom Sheets & Dialogs
        │           │   ├── 📂 discovery/ # Khám phá
        │           │   ├── 📂 journey/   # Hành trình
        │           │   │   ├── 📂 favoriteLocation/
        │           │   │   ├── 📂 historyJourney/
        │           │   │   └── 📂 myEvaluation/
        │           │   └── 📂 ranking/   # Bảng xếp hạng
        │           └── 📂 search/        # Tìm kiếm
        │
        └── 📂 res/                       # 🎨 Resources
            ├── 📂 layout/               # XML Layouts
            ├── 📂 drawable/             # Icons & Drawables
            ├── 📂 color/                # Color Selectors
            ├── 📂 values/               # Strings, Colors, Themes
            ├── 📂 menu/                 # Menu Resources
            ├── 📂 navigation/           # Navigation Graphs
            └── 📂 xml/                  # Backup Rules
```

---

## 🚀 Cài Đặt Và Chạy Dự Án

### 📋 Yêu cầu hệ thống

| Yêu cầu         | Chi tiết                                   |
| --------------- | ------------------------------------------ |
| **IDE**         | Android Studio Hedgehog (2023.1.1) trở lên |
| **JDK**         | Java Development Kit 11+                   |
| **Android SDK** | API Level 24 (Android 7.0) trở lên         |
| **Gradle**      | 8.x (được quản lý bởi Gradle Wrapper)      |
| **RAM**         | Tối thiểu 8GB (khuyến nghị 16GB)           |
| **Ổ cứng**      | Tối thiểu 10GB trống                       |

### 📝 Các bước cài đặt

#### Bước 1️⃣ — Clone Repository

```bash
git clone https://github.com/your-username/android-app-vietravel.git
cd android-app-vietravel
```

#### Bước 2️⃣ — Thiết lập Firebase Console

1. Truy cập [Firebase Console](https://console.firebase.google.com/)
2. Tạo project mới hoặc sử dụng project có sẵn
3. Thêm ứng dụng Android với package name: `com.group6.vietravel`
4. Kích hoạt các dịch vụ sau:
   - ✅ **Authentication** → Bật phương thức **Email/Password**
   - ✅ **Cloud Firestore** → Tạo database ở chế độ **Production**
   - ✅ **Storage** → Thiết lập rules cho upload hình ảnh

#### Bước 3️⃣ — Cấu hình file `google-services.json`

1. Tải file `google-services.json` từ Firebase Console
2. Đặt file vào thư mục `app/`:

```
📦 android-app-vietravel
└── 📂 app/
    ├── 📄 google-services.json  ← Đặt tại đây
    └── 📄 build.gradle.kts
```

#### Bước 4️⃣ — Cấu hình Google Maps API Key

1. Truy cập [Google Cloud Console](https://console.cloud.google.com/)
2. Tạo API Key và kích hoạt các API:
   - ✅ Maps SDK for Android
   - ✅ Places API
   - ✅ Directions API
3. Mở file `AndroidManifest.xml` và cập nhật API key:

```xml
<meta-data
    android:name="com.google.android.geo.API_KEY"
    android:value="YOUR_GOOGLE_MAPS_API_KEY" />
```

#### Bước 5️⃣ — Cấu hình Gemini AI (Tùy chọn)

1. Truy cập [Google AI Studio](https://aistudio.google.com/)
2. Tạo API Key cho Gemini
3. Cập nhật key trong file cấu hình tương ứng

#### Bước 6️⃣ — Build và chạy dự án

1. Mở project bằng **Android Studio**
2. Đợi Gradle sync hoàn tất
3. Kết nối thiết bị Android hoặc khởi động Android Emulator
4. Nhấn **Run ▶️** hoặc sử dụng phím tắt `Shift + F10`

```bash
# Hoặc build từ command line
./gradlew assembleDebug
./gradlew installDebug
```

> ⚠️ **Lưu ý:** File `google-services.json` **KHÔNG** được commit lên Git vì lý do bảo mật. Mỗi thành viên nhóm cần tự tải file này từ Firebase Console.

---

## 🗄 Cấu Trúc Cơ Sở Dữ Liệu Firebase

Ứng dụng sử dụng **Cloud Firestore** với cấu trúc các collection như sau:

### 📊 Sơ đồ Database

```
🔥 Firestore Database
│
├── 📁 users/                          # Người dùng
│   └── 📄 {uid}
│       ├── uid: string                # ID người dùng (Firebase Auth UID)
│       ├── username: string           # Tên hiển thị
│       ├── email: string              # Email đăng ký
│       ├── role: string               # Vai trò ("user" | "admin")
│       ├── avatar_url: string         # URL ảnh đại diện
│       ├── points: number             # Điểm tích lũy
│       ├── status: string             # Trạng thái ("active" | "inactive" | "locked")
│       ├── created_at: timestamp      # Ngày tạo tài khoản
│       │
│       ├── 📁 favorites/             # Địa điểm yêu thích (sub-collection)
│       │   └── 📄 {placeId}
│       │       └── added_at: timestamp
│       │
│       └── 📁 visited_places/        # Địa điểm đã ghé thăm (sub-collection)
│           └── 📄 {placeId}
│               ├── note: string
│               └── visited_at: timestamp
│
├── 📁 places/                         # Địa điểm du lịch
│   └── 📄 {placeId}
│       ├── name: string               # Tên địa điểm
│       ├── description: string        # Mô tả chi tiết
│       ├── address: string            # Địa chỉ
│       ├── province: string           # Tỉnh/Thành phố
│       ├── district: string           # Quận/Huyện
│       ├── latitude: number           # Vĩ độ
│       ├── longitude: number          # Kinh độ
│       ├── price_range: string        # Khoảng giá
│       ├── category_id: string        # ID danh mục
│       ├── approved: boolean          # Trạng thái duyệt
│       ├── rating_avg: number         # Điểm đánh giá trung bình
│       ├── rating_count: number       # Số lượt đánh giá
│       ├── phone_number: string       # Số điện thoại
│       ├── website_uri: string        # Website
│       ├── gallery_urls: array        # Danh sách URL hình ảnh
│       ├── opening_hours: array       # Giờ mở cửa
│       ├── created_at: timestamp      # Ngày tạo
│       └── updated_at: timestamp      # Ngày cập nhật
│
├── 📁 categories/                     # Danh mục địa điểm
│   └── 📄 {categoryId}
│       ├── name: string               # Tên danh mục
│       └── description: string        # Mô tả
│
├── 📁 reviews/                        # Đánh giá
│   └── 📄 {reviewId}
│       ├── user_id: string            # ID người đánh giá
│       ├── place_id: string           # ID địa điểm
│       ├── rating: number             # Số sao (1.0 - 5.0)
│       ├── comment: string            # Nội dung bình luận
│       ├── status: string             # Trạng thái ("pending" | "approved" | "rejected")
│       └── created_at: timestamp      # Ngày tạo
│
└── 📁 notifications/                  # Thông báo
    └── 📄 {notificationId}
        ├── title: string              # Tiêu đề
        ├── message: string            # Nội dung
        └── timestamp: number          # Thời gian gửi
```

### 📦 Firebase Storage

```
🗂️ Firebase Storage
│
├── 📂 places/                         # Hình ảnh địa điểm
│   └── 📂 {placeId}/
│       └── 🖼️ {image_name}.jpg
│
├── 📂 avatars/                        # Ảnh đại diện người dùng
│   └── 🖼️ {uid}.jpg
│
└── 📂 reviews/                        # Hình ảnh đánh giá
    └── 📂 {reviewId}/
        └── 🖼️ {image_name}.jpg
```

---

## 📚 Thông Tin Đồ Án

<div align="center">

| Thông tin                   | Chi tiết                    |
| --------------------------- | --------------------------- |
| 📘 **Môn học**              | Phát triển ứng dụng di động |
| 🏫 **Trường**               | HCMUS                       |
| 🏛️ **Khoa**                 | Công nghệ Thông tin         |
| 👨‍🏫 **Giảng viên hướng dẫn** | Ngô Ngọc Đăng Khoa          |
| 📅 **Học kỳ**               | 1                           |
| 📆 **Năm học**              | 2025 - 2026                 |

</div>

---

## 👥 Thông Tin Nhóm Thực Hiện

<div align="center">

### 🏷️ Nhóm 6

| STT | Họ và Tên              |   MSSV   |            Vai trò             |
| :-: | ---------------------- | :------: | :----------------------------: |
|  1  | Phạm Vương Quân        | 23120341 | 👑 Nhóm trưởng, Lập trình viên |
|  2  | Thái Thiên Phú         | 23120327 |       👨‍💻 Lập trình viên        |
|  3  | Huỳnh Lê Minh Triết    | 23120380 |       👨‍💻 Lập trình viên        |
|  4  | Nguyễn Ngọc Nhân Trọng | 23120382 |       👨‍💻 Lập trình viên        |

</div>

---

## 📊 Trạng Thái Dự Án

<div align="center">

| Module                                |  Trạng thái   | Tiến độ |
| ------------------------------------- | :-----------: | :-----: |
| 🔐 Authentication (Đăng nhập/Đăng ký) | ✅ Hoàn thành |  100%   |
| 🏖️ Khám phá & Duyệt địa điểm          | ✅ Hoàn thành |  100%   |
| 🔍 Tìm kiếm & Bộ lọc                  | ✅ Hoàn thành |  100%   |
| 📋 Chi tiết địa điểm                  | ✅ Hoàn thành |  100%   |
| ❤️ Yêu thích & Đã ghé thăm            | ✅ Hoàn thành |  100%   |
| ⭐ Đánh giá & Bình luận               | ✅ Hoàn thành |  100%   |
| 🗺️ Tích hợp Google Maps               | ✅ Hoàn thành |  100%   |
| 🤖 AI Chatbot (Gemini, Grok)          | ✅ Hoàn thành |  100%   |
| 🏆 Bảng xếp hạng                      | ✅ Hoàn thành |  100%   |
| 👤 Quản lý hồ sơ                      | ✅ Hoàn thành |  100%   |
| 🔔 Thông báo                          | ✅ Hoàn thành |  100%   |
| 🛡️ Admin Panel                        | ✅ Hoàn thành |  100%   |

</div>

---

## 🔮 Hướng Phát Triển Tương Lai

- [ ] 🌐 **Đa ngôn ngữ** — Hỗ trợ tiếng Anh, tiếng Trung, tiếng Nhật cho du khách quốc tế
- [ ] 📅 **Lập lịch trình chi tiết** — Cho phép người dùng tạo lịch trình du lịch theo ngày
- [ ] 💬 **Chat & Cộng đồng** — Tạo cộng đồng chia sẻ trải nghiệm giữa người dùng
- [ ] 📸 **Chia sẻ mạng xã hội** — Chia sẻ địa điểm và đánh giá lên Facebook, Instagram
- [ ] 💳 **Đặt vé & Thanh toán** — Tích hợp đặt vé tham quan, thanh toán trực tuyến
- [ ] 🔔 **Push Notification nâng cao** — Thông báo đẩy qua Firebase Cloud Messaging
- [ ] 📶 **Chế độ Offline** — Lưu cache dữ liệu để sử dụng khi không có internet
- [ ] 🎙️ **Tìm kiếm bằng giọng nói** — Hỗ trợ tìm kiếm địa điểm bằng giọng nói
- [ ] 🌦️ **Tích hợp thời tiết** — Hiển thị thông tin thời tiết tại địa điểm du lịch
- [ ] 📊 **Dashboard thống kê** — Thống kê chi tiết cho Admin (biểu đồ, báo cáo)

---

## 📄 Giấy Phép

Dự án này được cấp phép theo giấy phép **MIT License**.

```
MIT License

Copyright (c) 2025 Group 6 - VieTravel

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```

---

<div align="center">

### ⭐ Nếu dự án hữu ích, hãy cho chúng tôi một Star! ⭐

![Made with Love](https://img.shields.io/badge/Made%20with-❤️-red?style=for-the-badge)
![Made in Vietnam](https://img.shields.io/badge/Made%20in-🇻🇳%20Vietnam-red?style=for-the-badge)

**© 2025 Nhóm 6 — VieTravel. All rights reserved.**

[⬆ Về đầu trang](#-vietravel---ứng-dụng-du-lịch-việt-nam)

</div>
