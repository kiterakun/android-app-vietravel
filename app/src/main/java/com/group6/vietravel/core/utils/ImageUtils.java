package com.group6.vietravel.core.utils;

import android.net.Uri;
import android.util.Log;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class ImageUtils {
    private final FirebaseStorage storage;
    private final StorageReference storageRef;
    private final FirebaseFirestore db;

    public interface OnUploadImage{
        public void onSuccess(String s);
    }

    public ImageUtils(){
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        db = FirebaseFirestore.getInstance();
    }
    public void uploadImage(Uri imageUri, OnUploadImage onUploadImage) {
        // Tạo tên file duy nhất
        String fileName = "images/" + System.currentTimeMillis() + ".jpg";
        StorageReference imageRef = storageRef.child(fileName);

        imageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot ->
                        imageRef.getDownloadUrl().addOnSuccessListener(downloadUri -> {

                            String downloadUrl = downloadUri.toString();
                            Log.d("IMG", "Uploaded: " + downloadUrl);

                            // Gọi hàm lưu xuống Firestore
                            saveImageUrlToFirestore(downloadUrl);
                            onUploadImage.onSuccess(downloadUrl);
                        })
                )
                .addOnFailureListener(e ->
                        Log.e("IMG", "Upload fail", e)
                );
    }

    private void saveImageUrlToFirestore(String url) {

        Map<String, Object> data = new HashMap<>();
        data.put("image", url);

        db.collection("users").document("userId")
                .update(data)
                .addOnSuccessListener(aVoid -> Log.d("FS", "Saved URL"))
                .addOnFailureListener(e -> Log.e("FS", "Error", e));
    }

}
