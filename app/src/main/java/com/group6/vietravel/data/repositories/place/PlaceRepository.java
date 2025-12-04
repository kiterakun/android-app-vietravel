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
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.group6.vietravel.data.models.place.Place;
import com.group6.vietravel.data.models.user.Favorite;
import com.group6.vietravel.data.models.user.VisitedPlace;

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

    private final FirebaseFirestore db;
    private final FirebaseAuth auth;
    private final MutableLiveData<List<Place>> allPlacesLiveData;
    private final MutableLiveData<List<Place>> favoritePlacesLiveData;
    private final MutableLiveData<List<Place>> visitedPlacesLiveData;

    private final MutableLiveData<List<Place>> searchPlacesLiveData;
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
        searchPlacesLiveData = new MutableLiveData<>();
        storage = FirebaseStorage.getInstance();

        if (!Places.isInitialized()) {
            Places.initialize(context.getApplicationContext(), "AIzaSyDolciswVBQOtQxLZ-ykg8qu5m6ZkUk5S0");
        }
        placesClient = Places.createClient(context.getApplicationContext());

        fetchAllPlaces();
        fetchFavoritePlaces();
        fetchVisitedPlaces();
    }

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
    public LiveData<List<Place>> getSearchPlaces() {
        return searchPlacesLiveData;
    }

    public void fetchAllPlaces() {
        db.collection("places")
                .whereEqualTo("approved", true)
                .orderBy("created_at", Query.Direction.DESCENDING)
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

    public void fetchFavoritePlaces() {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            favoritePlacesLiveData.postValue(new ArrayList<>()); // Trả về list rỗng
            return;
        }

        String userId = user.getUid();

        db.collection("users").document(userId).collection("favorites")
                .orderBy("added_at", Query.Direction.DESCENDING)
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

    public void fetchVisitedPlaces() {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            visitedPlacesLiveData.postValue(new ArrayList<>());
            return;
        }
        String userId = user.getUid();

        db.collection("users").document(userId).collection("visited_places")
                .orderBy("visited_at", Query.Direction.DESCENDING)
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

    public void savePlace(Place place){
        if(place.getPlaceId()!= null && !place.getPlaceId().isEmpty()) {
            db.collection("places").document(place.getPlaceId()).set(place);
        }
        else{
            db.collection("places").add(place);
        }
    }

    public void searchPlaces(String nameKeyword, String categoryId, String province, String district) {
        com.google.firebase.firestore.Query query = db.collection("places")
                .whereEqualTo("approved", true);

        if (categoryId != null && !categoryId.isEmpty() && !"all".equals(categoryId)) {
            query = query.whereEqualTo("category_id", categoryId);
        }

        if (province != null && !province.isEmpty() && !"all".equals(province)) {
            query = query.whereEqualTo("province", province);
        }

        if (district != null && !district.isEmpty() && !"all".equals(district)) {
            query = query.whereEqualTo("district", district);
        }

        if (nameKeyword != null && !nameKeyword.isEmpty()) {
            String endKeyword = nameKeyword + "\uf8ff";

            query = query.orderBy("name")
                    .startAt(nameKeyword)
                    .endAt(endKeyword);
        } else {
            query = query.orderBy("updated_at", com.google.firebase.firestore.Query.Direction.DESCENDING);
        }

        query.get().addOnSuccessListener(querySnapshot -> {
            if (querySnapshot != null && !querySnapshot.isEmpty()) {
                List<Place> places = querySnapshot.toObjects(Place.class);
                Log.v(TAG, "Thành công ");
                searchPlacesLiveData.postValue(places);
            } else {
                searchPlacesLiveData.postValue(new ArrayList<>());
                Log.v(TAG, "Rỗng ");
            }
        }).addOnFailureListener(e -> {
            Log.e(TAG, "Lỗi tìm kiếm: ", e);
            searchPlacesLiveData.postValue(new ArrayList<>());
        });
    }

    public LiveData<List<Place>> fetchRankedPlaces(String province, String district, String categoryId, int limit) {
        MutableLiveData<List<Place>> resultLiveData = new MutableLiveData<>();

        com.google.firebase.firestore.Query query = db.collection("places")
                .whereEqualTo("approved", true);

        if (province != null && !province.isEmpty() && !"all".equals(province)) {
            query = query.whereEqualTo("province", province);
        }

        query = query.orderBy("rating_avg", com.google.firebase.firestore.Query.Direction.DESCENDING);

        long fetchLimit = limit;
        if ((district != null && !"all".equals(district)) || (categoryId != null && !"all".equals(categoryId))) {
            fetchLimit = 50;
        }

        query.limit(fetchLimit);

        query.get().addOnSuccessListener(snapshot -> {
            if (snapshot == null || snapshot.isEmpty()) {
                resultLiveData.postValue(new ArrayList<>());
                return;
            }

            List<Place> rawList = snapshot.toObjects(Place.class);
            List<Place> filteredList = new ArrayList<>();

            for (Place p : rawList) {
                boolean matchDistrict = true;
                boolean matchCategory = true;

                if (district != null && !district.isEmpty() && !"all".equals(district)) {
                    if (p.getDistrict() == null || !p.getDistrict().equals(district)) {
                        matchDistrict = false;
                    }
                }

                if (categoryId != null && !categoryId.isEmpty() && !"all".equals(categoryId)) {
                    if (p.getCategoryId() == null || !p.getCategoryId().equals(categoryId)) {
                        matchCategory = false;
                    }
                }

                if (matchDistrict && matchCategory) {
                    filteredList.add(p);
                }

                if (filteredList.size() >= limit) {
                    break;
                }
            }
            resultLiveData.postValue(filteredList);

        }).addOnFailureListener(e -> {
            Log.e(TAG, "Lỗi fetch ranking: ", e);
            resultLiveData.postValue(new ArrayList<>());
        });

        return resultLiveData;
    }



}