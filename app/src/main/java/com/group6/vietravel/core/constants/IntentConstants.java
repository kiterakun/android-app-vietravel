package com.group6.vietravel.core.constants;

/**
 * Intent extra keys and action constants
 */
public class IntentConstants {
    
    // Extra keys
    public static final String EXTRA_PLACE_ID = "extra_place_id";
    public static final String EXTRA_PLACE = "extra_place";
    public static final String EXTRA_USER_ID = "extra_user_id";
    public static final String EXTRA_USER = "extra_user";
    public static final String EXTRA_REVIEW_ID = "extra_review_id";
    public static final String EXTRA_REVIEW = "extra_review";
    public static final String EXTRA_CATEGORY_ID = "extra_category_id";
    public static final String EXTRA_PROVINCE_ID = "extra_province_id";
    public static final String EXTRA_SEARCH_QUERY = "extra_search_query";
    public static final String EXTRA_FILTER_TYPE = "extra_filter_type";
    public static final String EXTRA_IS_EDIT_MODE = "extra_is_edit_mode";
    
    // Result codes
    public static final int RESULT_CODE_PLACE_ADDED = 3001;
    public static final int RESULT_CODE_PLACE_UPDATED = 3002;
    public static final int RESULT_CODE_PLACE_DELETED = 3003;
    public static final int RESULT_CODE_REVIEW_ADDED = 3004;
    public static final int RESULT_CODE_REVIEW_UPDATED = 3005;
    
    // Actions
    public static final String ACTION_REFRESH_DATA = "com.group6.vietravel.ACTION_REFRESH_DATA";
    public static final String ACTION_LOGOUT = "com.group6.vietravel.ACTION_LOGOUT";
    public static final String ACTION_NOTIFICATION = "com.group6.vietravel.ACTION_NOTIFICATION";
    
    private IntentConstants() {
        // Private constructor to prevent instantiation
    }
}
