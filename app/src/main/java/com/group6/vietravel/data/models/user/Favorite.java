package com.group6.vietravel.data.models.user;
import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.ServerTimestamp;
import java.util.Date;
public class Favorite {
    @DocumentId
    private String placeId;

    @ServerTimestamp
    private Date added_at;

    public Favorite() {
    }

    public String getPlaceId() {
        return placeId;
    }

    public Date getAdded_at() {
        return added_at;
    }
}
