package com.group6.vietravel.core.constants;

/**
 * Application-wide constants
 */
public class AppConstants {
    
    // App info
    public static final String APP_NAME = "VieTravel";
    public static final String APP_VERSION = "1.0.0";
    
    // Date formats
    public static final String DATE_FORMAT_DEFAULT = "dd/MM/yyyy";
    public static final String DATE_FORMAT_FULL = "dd/MM/yyyy HH:mm:ss";
    public static final String DATE_FORMAT_TIME = "HH:mm";
    
    // Request codes
    public static final int REQUEST_CODE_LOCATION_PERMISSION = 1001;
    public static final int REQUEST_CODE_CAMERA_PERMISSION = 1002;
    public static final int REQUEST_CODE_STORAGE_PERMISSION = 1003;
    public static final int REQUEST_CODE_PICK_IMAGE = 2001;
    public static final int REQUEST_CODE_TAKE_PHOTO = 2002;
    
    // Pagination
    public static final int PAGE_SIZE = 20;
    public static final int INITIAL_LOAD_SIZE = 40;
    
    // Cache
    public static final long CACHE_EXPIRY_TIME = 5 * 60 * 1000; // 5 minutes
    public static final int MAX_CACHE_SIZE = 50 * 1024 * 1024; // 50 MB
    
    // Validation
    public static final int MIN_PASSWORD_LENGTH = 6;
    public static final int MAX_PASSWORD_LENGTH = 50;
    public static final int MIN_USERNAME_LENGTH = 3;
    public static final int MAX_USERNAME_LENGTH = 30;
    
    // Map
    public static final float DEFAULT_ZOOM = 15f;
    public static final float MAX_ZOOM = 20f;
    public static final float MIN_ZOOM = 5f;
    
    // Rating
    public static final float MIN_RATING = 1.0f;
    public static final float MAX_RATING = 5.0f;
    
    private AppConstants() {
        // Private constructor to prevent instantiation
    }
}
