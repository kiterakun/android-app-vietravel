package com.group6.vietravel.data.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.PropertyName;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Place implements Parcelable {

    @DocumentId
    private String placeId;

    private String name;
    private String description;
    private String address;
    private double latitude;
    private double longitude;

    @PropertyName("price_range")
    private String priceRange;

    @PropertyName("category_id")
    private String categoryId;

    private boolean approved;

    @PropertyName("rating_avg")
    private float ratingAvg;

    @PropertyName("rating_count")
    private int ratingCount;

    @PropertyName("phone_number")
    private String phoneNumber;

    @PropertyName("website_uri")
    private String websiteUri;

    @PropertyName("gallery_urls")
    private List<String> galleryUrls;

    @PropertyName("opening_hours")
    private List<String> openingHours;

    @ServerTimestamp
    @PropertyName("created_at")
    private Date createdAt;

    @ServerTimestamp
    @PropertyName("updated_at")
    private Date updatedAt;

    public Place() {
        this.galleryUrls = new ArrayList<>();
        this.openingHours = new ArrayList<>();
    }

    protected Place(Parcel in) {
        placeId = in.readString();
        name = in.readString();
        description = in.readString();
        address = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
        priceRange = in.readString();
        categoryId = in.readString();
        approved = in.readByte() != 0;
        ratingAvg = in.readFloat();
        ratingCount = in.readInt();
        phoneNumber = in.readString();
        websiteUri = in.readString();
        galleryUrls = in.createStringArrayList();
        openingHours = in.createStringArrayList();

        // Đọc Date từ long (timestamp)
        long createdTime = in.readLong();
        createdAt = createdTime == -1 ? null : new Date(createdTime);
        long updatedTime = in.readLong();
        updatedAt = updatedTime == -1 ? null : new Date(updatedTime);
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(placeId);
        dest.writeString(name);
        dest.writeString(description);
        dest.writeString(address);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeString(priceRange);
        dest.writeString(categoryId);
        dest.writeByte((byte) (approved ? 1 : 0));
        dest.writeFloat(ratingAvg);
        dest.writeInt(ratingCount);
        dest.writeString(phoneNumber);
        dest.writeString(websiteUri);
        dest.writeStringList(galleryUrls);
        dest.writeStringList(openingHours);

        // Ghi Date dưới dạng long
        dest.writeLong(createdAt != null ? createdAt.getTime() : -1);
        dest.writeLong(updatedAt != null ? updatedAt.getTime() : -1);
    }

    @Override
    public int describeContents() {
        return 0;
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

    public String getPlaceId() { return placeId; }
    public void setPlaceId(String placeId) { this.placeId = placeId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }

    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }

    @PropertyName("price_range")
    public String getPriceRange() { return priceRange; }
    @PropertyName("price_range")
    public void setPriceRange(String priceRange) { this.priceRange = priceRange; }

    @PropertyName("category_id")
    public String getCategoryId() { return categoryId; }
    @PropertyName("category_id")
    public void setCategoryId(String categoryId) { this.categoryId = categoryId; }

    public boolean isApproved() { return approved; }
    public void setApproved(boolean approved) { this.approved = approved; }

    @PropertyName("rating_avg")
    public float getRatingAvg() { return ratingAvg; }
    @PropertyName("rating_avg")
    public void setRatingAvg(float ratingAvg) { this.ratingAvg = ratingAvg; }

    @PropertyName("rating_count")
    public int getRatingCount() { return ratingCount; }
    @PropertyName("rating_count")
    public void setRatingCount(int ratingCount) { this.ratingCount = ratingCount; }

    @PropertyName("phone_number")
    public String getPhoneNumber() { return phoneNumber; }
    @PropertyName("phone_number")
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    @PropertyName("website_uri")
    public String getWebsiteUri() { return websiteUri; }
    @PropertyName("website_uri")
    public void setWebsiteUri(String websiteUri) { this.websiteUri = websiteUri; }

    @PropertyName("gallery_urls")
    public List<String> getGalleryUrls() { return galleryUrls; }
    @PropertyName("gallery_urls")
    public void setGalleryUrls(List<String> galleryUrls) { this.galleryUrls = galleryUrls; }

    @PropertyName("opening_hours")
    public List<String> getOpeningHours() { return openingHours; }
    @PropertyName("opening_hours")
    public void setOpeningHours(List<String> openingHours) { this.openingHours = openingHours; }

    @PropertyName("created_at")
    public Date getCreatedAt() { return createdAt; }
    @PropertyName("created_at")
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    @PropertyName("updated_at")
    public Date getUpdatedAt() { return updatedAt; }
    @PropertyName("updated_at")
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }

    public String getThumbnailUrl() {
        if (galleryUrls != null && !galleryUrls.isEmpty()) {
            return galleryUrls.get(0);
        }
        return null;
    }
}