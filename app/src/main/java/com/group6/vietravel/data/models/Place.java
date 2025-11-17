package com.group6.vietravel.data.models;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.DocumentId;
public class Place implements Parcelable {
    @DocumentId
    private String placeId;

    private String name;
    private String description;
    private String address;
    private double latitude;
    private double longitude;
    private String price_range;
    private double rating_avg;
    private String category_id;
    private boolean approved;
    private String google_place_id;
    private String cached_image_url;

    // Đống này chỉ là phần code bắt buộc của Parcelable để đi đóng gói object này thôi
    protected Place(Parcel in) {
        placeId = in.readString();
        name = in.readString();
        description = in.readString();
        address = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
        price_range = in.readString();
        rating_avg = in.readDouble();
        category_id = in.readString();
        approved = in.readByte() != 0;
        google_place_id = in.readString();
        cached_image_url = in.readString();
    }

    public static final Creator<Place> CREATOR = new Creator<Place>() {
        @Override
        public Place createFromParcel(Parcel in) {
            return new Place(in);
        }

        @Override
        public Place[] newArray(int size) {
            return new Place[size];
        }
    };

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(placeId);
        dest.writeString(name);
        dest.writeString(description);
        dest.writeString(address);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeString(price_range);
        dest.writeDouble(rating_avg);
        dest.writeString(category_id);
        dest.writeByte((byte) (approved ? 1 : 0));
        dest.writeString(google_place_id);
        dest.writeString(cached_image_url);
    }
    //code bắt buộc của Parcelable

    public Place() {
    }
    public String getPlaceId() {
        return placeId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getAddress() {
        return address;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getPrice_range() {
        return price_range;
    }

    public double getRating_avg() {
        return rating_avg;
    }

    public String getCategory_id() {
        return category_id;
    }

    public boolean isApproved() {
        return approved;
    }

    public String getGoogle_place_id() {
        return google_place_id;
    }
    public String getCached_image_url() {
        return cached_image_url;
    }

    public void setCached_image_url(String cached_image_url) {
        this.cached_image_url = cached_image_url;
    }

    @Override
    public int describeContents() {
        return 0;
    }
}

