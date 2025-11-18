package com.group6.vietravel.data.repositorys;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.group6.vietravel.data.models.Place;
import com.group6.vietravel.data.models.Favorite;
import com.group6.vietravel.data.models.VisitedPlace;

import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;
import android.content.Context;
import android.graphics.Bitmap;
import com.google.android.libraries.places.api.model.PhotoMetadata;

import java.io.ByteArrayOutputStream;

import com.google.android.libraries.places.api.net.FetchPhotoRequest;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class PlaceRepository {

    private static final String TAG = "PlaceRepository";
    private static PlaceRepository instance;

    // Các kết nối đến Firebase
    private final FirebaseFirestore db;
    private final FirebaseAuth auth;
    private final MutableLiveData<List<Place>> allPlacesLiveData;
    private final MutableLiveData<List<Place>> favoritePlacesLiveData;
    private final MutableLiveData<List<Place>> visitedPlacesLiveData;
    private final PlacesClient placesClient;
    private final FirebaseStorage storage;

    // Constructor (private để đảm bảo là Singleton)
    private PlaceRepository(Context context) {
        db = FirebaseFirestore.getInstance();
        db.disableNetwork()
                .addOnSuccessListener(aVoid->FirebaseFirestore.getInstance().enableNetwork());

        auth = FirebaseAuth.getInstance();
        allPlacesLiveData = new MutableLiveData<>();
        favoritePlacesLiveData = new MutableLiveData<>();
        visitedPlacesLiveData = new MutableLiveData<>();
        storage = FirebaseStorage.getInstance();

        if (!Places.isInitialized()) {
            // Thay "YOUR_API_KEY" bằng API key của bạn
            Places.initialize(context.getApplicationContext(), "AIzaSyDolciswVBQOtQxLZ-ykg8qu5m6ZkUk5S0");
        }
        placesClient = Places.createClient(context.getApplicationContext());

        fetchAllPlaces();
        fetchFavoritePlaces();
        fetchVisitedPlaces();
    }

    // Phương thức để lấy thể hiện (instance) duy nhất của Repository
    public static synchronized PlaceRepository getInstance(Context context) {
        if (instance == null) {
            instance = new PlaceRepository(context.getApplicationContext());
        }
        return instance;
    }

    public LiveData<List<Place>> getAllPlaces() {
        return allPlacesLiveData;
    }

    public LiveData<List<Place>> getFavoritePlaces() {
        return favoritePlacesLiveData;
    }

    public LiveData<List<Place>> getVisitedPlaces() {
        return visitedPlacesLiveData;
    }

    // Lấy TẤT CẢ địa điểm
    public void fetchAllPlaces() {
        db.collection("places")
                .whereEqualTo("approved", true) // Chỉ lấy các địa điểm đã duyệt
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.w(TAG, "Lỗi lắng nghe 'places'.", error);
                        return;
                    }
                    if (value != null) {
                        List<Place> places = value.toObjects(Place.class);
                        allPlacesLiveData.postValue(places);
                    }
                });
    }

    // 2. Lấy các địa điểm YÊU THÍCH của người dùng hiện tại
    public void fetchFavoritePlaces() {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            favoritePlacesLiveData.postValue(new ArrayList<>()); // Trả về list rỗng
            return;
        }

        String userId = user.getUid();

        db.collection("users").document(userId).collection("favorites")
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.w(TAG, "Lỗi lắng nghe 'favorites'.", error);
                        return;
                    }
                    if (value != null) {
                        // Lấy ra danh sách ID
                        List<String> favoriteIds = new ArrayList<>();
                        for (DocumentSnapshot doc : value.getDocuments()) {
                            favoriteIds.add(doc.getId());
                        }

                        fetchPlacesByIds(favoriteIds, favoritePlacesLiveData);
                    }
                });
    }

    // 3. Lấy các địa điểm đã ghé qua của người dùng hiện tại
    public void fetchVisitedPlaces() {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            visitedPlacesLiveData.postValue(new ArrayList<>());
            return;
        }
        String userId = user.getUid();

        db.collection("users").document(userId).collection("visited_places")
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.w(TAG, "Lỗi lắng nghe 'visited_places'.", error);
                        return;
                    }
                    if (value != null) {
                        // Lấy ra danh sách ID
                        List<String> visitedIds = value.getDocuments().stream()
                                .map(DocumentSnapshot::getId)
                                .collect(Collectors.toList());

                        fetchPlacesByIds(visitedIds, visitedPlacesLiveData);
                    }
                });
    }

    // Phương thức private hỗ trợ: Lấy chi tiết Places từ một danh sách IDs
    private void fetchPlacesByIds(List<String> ids, MutableLiveData<List<Place>> liveData) {
        if (ids == null || ids.isEmpty()) {
            liveData.postValue(new ArrayList<>()); // Trả về list rỗng nếu không có ID
            return;
        }

        db.collection("places")
                .whereIn(FieldPath.documentId(), ids) // Truy vấn
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Place> places = queryDocumentSnapshots.toObjects(Place.class);
                    liveData.postValue(places);
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Lỗi khi lấy chi tiết places bằng IDs.", e);
                    liveData.postValue(new ArrayList<>());
                });
    }

    /**
     * Gọi API Google Places để lấy ảnh đầu tiên của một địa điểm
     * @param googlePlaceId ID từ Firestore
     * @return LiveData chứa ảnh Bitmap
     */
    public LiveData<Bitmap> fetchPhotoForPlace(String googlePlaceId) {
        MutableLiveData<Bitmap> photoBitmapLiveData = new MutableLiveData<>();

        if (googlePlaceId == null || googlePlaceId.isEmpty()) {
            photoBitmapLiveData.postValue(null);
            return photoBitmapLiveData;
        }

        final List<com.google.android.libraries.places.api.model.Place.Field> placeFields =
                Arrays.asList(com.google.android.libraries.places.api.model.Place.Field.PHOTO_METADATAS);


        final FetchPlaceRequest placeRequest = FetchPlaceRequest.newInstance(googlePlaceId, placeFields);

        placesClient.fetchPlace(placeRequest).addOnSuccessListener((response) -> {

            final com.google.android.libraries.places.api.model.Place place = response.getPlace();

            final List<PhotoMetadata> metadata = place.getPhotoMetadatas();
            if (metadata == null || metadata.isEmpty()) {
                Log.w(TAG, "Không tìm thấy ảnh cho placeId: " + googlePlaceId);
                photoBitmapLiveData.postValue(null);
                return;
            }

            final PhotoMetadata photoMetadata = metadata.get(0);

            final FetchPhotoRequest photoRequest = FetchPhotoRequest.builder(photoMetadata)
                    .setMaxWidth(500) // Tùy chỉnh kích thước bạn muốn
                    .setMaxHeight(300)
                    .build();

            placesClient.fetchPhoto(photoRequest).addOnSuccessListener((fetchPhotoResponse) -> {
                Bitmap bitmap = fetchPhotoResponse.getBitmap();

                // G. ĐẨY KẾT QUẢ VÀO LIVEDATA
                photoBitmapLiveData.postValue(bitmap);

            }).addOnFailureListener((exception) -> {
                Log.e(TAG, "Lỗi khi tải ảnh bitmap: " + exception.getMessage());
                photoBitmapLiveData.postValue(null);
            });

        }).addOnFailureListener((exception) -> {
            Log.e(TAG, "Lỗi khi lấy PhotoMetadata (Lần 1): " + exception.getMessage());
            photoBitmapLiveData.postValue(null);
        });

        return photoBitmapLiveData;
    }

    public void adminSaveNewPlace(Place placeToSave, String googlePlaceId) {
        StorageReference storageRef = storage.getReference();

        // 1. Tạo một tên tệp duy nhất cho ảnh (ví dụ: places/place_id.jpg)
        String imageFileName = placeToSave.getPlaceId() + ".jpg";

        StorageReference imageRef = storageRef.child("place_images/" + imageFileName);

        // 2. Gọi API Google để lấy Bitmap
        fetchPhotoForPlace(googlePlaceId).observeForever(bitmap -> {
            if (bitmap != null) {
                // 3. Nén Bitmap và chuẩn bị tải lên
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 85, baos);
                byte[] data = baos.toByteArray();

                UploadTask uploadTask = imageRef.putBytes(data);

                // 4. Tải lên Storage
                uploadTask.addOnSuccessListener(taskSnapshot -> {
                    // 5. Lấy Download URL
                    imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        String downloadUrl = uri.toString();

                        // 6. CẬP NHẬT URL VÀO ĐỐI TƯỢNG
                        placeToSave.setCached_image_url(downloadUrl);

                        // 7. LƯU VÀO FIRESTORE
                        savePlaceToFirestore(placeToSave);
                    });
                });
            } else {
                // Nếu không có ảnh, vẫn lưu
                savePlaceToFirestore(placeToSave);
            }
        });
    }

    private void savePlaceToFirestore(Place place) {
        // Logic ghi 'place' vào collection "places"
        db.collection("places").document(place.getPlaceId()).set(place)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Lưu địa điểm thành công!"))
                .addOnFailureListener(e -> Log.e(TAG, "Lỗi lưu Firestore", e));
    }


    public void addFavorite(String placeId) {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            Log.e("addFavorite","Người dùng chưa đăng nhập");
            return;
        }

        String userId = user.getUid();

        Favorite newFavorite = new Favorite();

        DocumentReference favoriteRef = db.collection("users").document(userId)
                .collection("favorites").document(placeId);

        favoriteRef.set(newFavorite);

    }

    public void removeFavorite(String placeId) {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            Log.e("removeFavorite","Người dùng chưa đăng nhập");
            return;
        }

        String userId = user.getUid();

        DocumentReference favoriteRef = db.collection("users").document(userId)
                .collection("favorites").document(placeId);

        favoriteRef.delete();

    }

    public void addVisited(String placeId) {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            Log.e("addVisited","Người dùng chưa đăng nhập");
            return;
        }

        String userId = user.getUid();

        VisitedPlace newVisitedPlace = new VisitedPlace();

        DocumentReference favoriteRef = db.collection("users").document(userId)
                .collection("visited_places").document(placeId);

        favoriteRef.set(newVisitedPlace);

    }


    public void removeVisited(String placeId) {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            Log.e("removeVisited","Người dùng chưa đăng nhập");
            return;
        }

        String userId = user.getUid();

        DocumentReference favoriteRef = db.collection("users").document(userId)
                .collection("visited_places").document(placeId);

        favoriteRef.delete();

    }
}