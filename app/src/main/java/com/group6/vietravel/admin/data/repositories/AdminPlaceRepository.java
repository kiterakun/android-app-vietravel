package com.group6.vietravel.admin.data.repositories;

import android.net.Uri;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.group6.vietravel.data.models.place.Place;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AdminPlaceRepository {
    
    private static final String TAG = "AdminPlaceRepository";
    private static AdminPlaceRepository instance;
    
    private final FirebaseFirestore db;
    private final FirebaseStorage storage;
    private final MutableLiveData<List<Place>> allPlacesLiveData;
    private final MutableLiveData<List<Place>> pendingPlacesLiveData;
    private final MutableLiveData<Boolean> operationSuccessLiveData;
    private final MutableLiveData<String> errorLiveData;
    
    private AdminPlaceRepository() {
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        allPlacesLiveData = new MutableLiveData<>();
        pendingPlacesLiveData = new MutableLiveData<>();
        operationSuccessLiveData = new MutableLiveData<>();
        errorLiveData = new MutableLiveData<>();
    }
    
    public static synchronized AdminPlaceRepository getInstance() {
        if (instance == null) {
            instance = new AdminPlaceRepository();
        }
        return instance;
    }
    
    public LiveData<List<Place>> getAllPlaces() {
        return allPlacesLiveData;
    }
    
    public LiveData<List<Place>> getPendingPlaces() {
        return pendingPlacesLiveData;
    }
    
    public LiveData<Boolean> getOperationSuccess() {
        return operationSuccessLiveData;
    }
    
    public LiveData<String> getError() {
        return errorLiveData;
    }
    
    // Fetch all places (approved and pending)
    public void fetchAllPlaces() {
        db.collection("places")
            .orderBy("created_at", Query.Direction.DESCENDING)
            .addSnapshotListener((value, error) -> {
                if (error != null) {
                    Log.w(TAG, "Error fetching places", error);
                    errorLiveData.postValue("Lỗi tải danh sách địa điểm");
                    return;
                }
                if (value != null) {
                    List<Place> places = value.toObjects(Place.class);
                    allPlacesLiveData.postValue(places);
                }
            });
    }
    
    // Fetch pending places (waiting approval)
    public void fetchPendingPlaces() {
        db.collection("places")
            .whereEqualTo("approved", false)
            .orderBy("created_at", Query.Direction.DESCENDING)
            .addSnapshotListener((value, error) -> {
                if (error != null) {
                    Log.w(TAG, "Error fetching pending places", error);
                    return;
                }
                if (value != null) {
                    List<Place> places = value.toObjects(Place.class);
                    pendingPlacesLiveData.postValue(places);
                }
            });
    }
    
    // Add new place
    public void addPlace(Place place, List<Uri> imageUris) {
        if (imageUris != null && !imageUris.isEmpty()) {
            uploadImages(imageUris, urls -> {
                place.setGalleryUrls(urls);
                savePlace(place);
            });
        } else {
            savePlace(place);
        }
    }
    
    // Update place
    public void updatePlace(Place place, List<Uri> newImageUris) {
        if (newImageUris != null && !newImageUris.isEmpty()) {
            uploadImages(newImageUris, urls -> {
                List<String> existingUrls = place.getGalleryUrls();
                if (existingUrls == null) {
                    existingUrls = new ArrayList<>();
                }
                existingUrls.addAll(urls);
                place.setGalleryUrls(existingUrls);
                savePlace(place);
            });
        } else {
            savePlace(place);
        }
    }
    
    // Delete place
    public void deletePlace(String placeId) {
        db.collection("places").document(placeId)
            .delete()
            .addOnSuccessListener(aVoid -> {
                Log.d(TAG, "Place deleted successfully");
                operationSuccessLiveData.postValue(true);
            })
            .addOnFailureListener(e -> {
                Log.e(TAG, "Error deleting place", e);
                errorLiveData.postValue("Lỗi xóa địa điểm: " + e.getMessage());
            });
    }
    
    // Approve place
    public void approvePlace(String placeId) {
        db.collection("places").document(placeId)
            .update("approved", true)
            .addOnSuccessListener(aVoid -> {
                Log.d(TAG, "Place approved");
                operationSuccessLiveData.postValue(true);
            })
            .addOnFailureListener(e -> {
                Log.e(TAG, "Error approving place", e);
                errorLiveData.postValue("Lỗi duyệt địa điểm");
            });
    }
    
    // Reject place (delete or mark as rejected)
    public void rejectPlace(String placeId) {
        deletePlace(placeId);
    }
    
    // Helper: Save place to Firestore
    private void savePlace(Place place) {
        if (place.getPlaceId() != null && !place.getPlaceId().isEmpty()) {
            // Update existing
            db.collection("places").document(place.getPlaceId())
                .set(place)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Place updated successfully");
                    operationSuccessLiveData.postValue(true);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error updating place", e);
                    errorLiveData.postValue("Lỗi cập nhật: " + e.getMessage());
                });
        } else {
            // Add new
            db.collection("places")
                .add(place)
                .addOnSuccessListener(documentReference -> {
                    Log.d(TAG, "Place added successfully");
                    operationSuccessLiveData.postValue(true);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error adding place", e);
                    errorLiveData.postValue("Lỗi thêm địa điểm: " + e.getMessage());
                });
        }
    }
    
    // Helper: Upload images to Firebase Storage
    private void uploadImages(List<Uri> imageUris, OnImagesUploadedListener listener) {
        List<String> uploadedUrls = new ArrayList<>();
        int[] uploadCount = {0};
        
        for (Uri uri : imageUris) {
            String fileName = "place_images/" + UUID.randomUUID().toString() + ".jpg";
            StorageReference ref = storage.getReference().child(fileName);
            
            ref.putFile(uri)
                .addOnSuccessListener(taskSnapshot -> 
                    ref.getDownloadUrl().addOnSuccessListener(downloadUri -> {
                        uploadedUrls.add(downloadUri.toString());
                        uploadCount[0]++;
                        
                        if (uploadCount[0] == imageUris.size()) {
                            listener.onUploaded(uploadedUrls);
                        }
                    })
                )
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error uploading image", e);
                    errorLiveData.postValue("Lỗi upload ảnh");
                });
        }
    }
    
    interface OnImagesUploadedListener {
        void onUploaded(List<String> urls);
    }

    public void resetStatus() {
        operationSuccessLiveData.postValue(false);
        errorLiveData.postValue(null);
    }
}
