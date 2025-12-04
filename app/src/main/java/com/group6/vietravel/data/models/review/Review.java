package com.group6.vietravel.data.models.review;

import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.PropertyName;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class Review {
//    @DocumentId
    private String reviewId;
    private String comment;

    @PropertyName("place_id")
    private String place_id;

    private float rating;
    private String status;

    @PropertyName("user_id")
    private String user_id;

    @ServerTimestamp
    private Date created_at;

    public Review(){}

    public Review(String comment, String place_id, float rating, String status, String user_id){
        this.comment = comment;
        this.place_id = place_id;
        this.rating = rating;
        this.status = status;
        this.user_id = user_id;
    }

    public String getReviewId() {
        return reviewId;
    }
    public void setReviewId(String reviewId) {
        this.reviewId = reviewId;
    }

    @PropertyName("place_id")
    public String getPlaceId() {
        return place_id;
    }

    @PropertyName("place_id")
    public void setPlaceId(String place_id) {
        this.place_id = place_id;
    }

    @PropertyName("user_id")
    public String getUserId() {
        return user_id;
    }

    @PropertyName("user_id")
    public void setUserId(String user_id) {
        this.user_id = user_id;
    }

    public String getComment() {
        return comment;
    }
    public void setComment(String comment) {
        this.comment = comment;
    }

    public float getRating() {
        return rating;
    }
    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }

    public Date getCreated_at() {
        return created_at;
    }
    public void setCreated_at(Date created_at) {
        this.created_at = created_at;
    }
}