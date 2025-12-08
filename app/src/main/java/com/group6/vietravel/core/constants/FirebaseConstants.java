package com.group6.vietravel.core.constants;

/**
 * Firebase-related constants
 */
public class FirebaseConstants {
    
    // Collections
    public static final String COLLECTION_USERS = "users";
    public static final String COLLECTION_PLACES = "places";
    public static final String COLLECTION_REVIEWS = "reviews";
    public static final String COLLECTION_CATEGORIES = "categories";
    public static final String COLLECTION_FAVORITES = "favorites";
    public static final String COLLECTION_VISITED_PLACES = "visited_places";
    public static final String COLLECTION_NOTIFICATIONS = "notifications";
    
    // User fields
    public static final String FIELD_USER_ID = "uid";
    public static final String FIELD_USER_EMAIL = "email";
    public static final String FIELD_USER_USERNAME = "username";
    public static final String FIELD_USER_ROLE = "role";
    public static final String FIELD_USER_STATUS = "status";
    public static final String FIELD_USER_POINTS = "points";
    public static final String FIELD_USER_AVATAR = "avatar_url";
    public static final String FIELD_USER_CREATED_AT = "created_at";
    
    // Place fields
    public static final String FIELD_PLACE_ID = "place_id";
    public static final String FIELD_PLACE_NAME = "name";
    public static final String FIELD_PLACE_CATEGORY = "category";
    public static final String FIELD_PLACE_PROVINCE = "province";
    public static final String FIELD_PLACE_RATING = "rating";
    public static final String FIELD_PLACE_CREATED_AT = "created_at";
    
    // Review fields
    public static final String FIELD_REVIEW_ID = "review_id";
    public static final String FIELD_REVIEW_USER_ID = "user_id";
    public static final String FIELD_REVIEW_PLACE_ID = "place_id";
    public static final String FIELD_REVIEW_RATING = "rating";
    public static final String FIELD_REVIEW_CONTENT = "content";
    public static final String FIELD_REVIEW_STATUS = "status";
    public static final String FIELD_REVIEW_CREATED_AT = "created_at";
    
    // User roles
    public static final String ROLE_USER = "user";
    public static final String ROLE_ADMIN = "admin";
    
    // User status
    public static final String STATUS_ACTIVE = "active";
    public static final String STATUS_INACTIVE = "inactive";
    public static final String STATUS_LOCKED = "locked";
    
    // Review status
    public static final String REVIEW_STATUS_PENDING = "pending";
    public static final String REVIEW_STATUS_APPROVED = "approved";
    public static final String REVIEW_STATUS_REJECTED = "rejected";
    
    // Storage paths
    public static final String STORAGE_PLACES = "places/";
    public static final String STORAGE_AVATARS = "avatars/";
    public static final String STORAGE_REVIEWS = "reviews/";
    
    private FirebaseConstants() {
        // Private constructor to prevent instantiation
    }
}
