package com.group6.vietravel.utils;

import android.util.Log;

import com.google.firebase.firestore.FirebaseFirestore;
import com.group6.vietravel.data.models.place.Place;


public class PlaceUtils {

    public interface OnPlaceLoadedCallback {
        void onPlaceLoaded(Place place);
        void onError(Exception e);
    }

    public static void getPlaceById(String placeId, OnPlaceLoadedCallback callback) {

        if (placeId == null || placeId.isEmpty()) {
            Log.e("PlaceUtils", "placeId bị null hoặc rỗng");
            return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("places").document(placeId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Place place = documentSnapshot.toObject(Place.class);

                        callback.onPlaceLoaded(place);
                    } else {
                        Log.w("PlaceUtils", "Không tìm thấy địa điểm với ID: " + placeId);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Lỗi kết nối: " + e.getMessage());
                    callback.onError(e);
                });
    }
}
