package com.group6.vietravel.data.models;

import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.ServerTimestamp;
import java.util.Date;
public class VisitedPlace {
    @DocumentId
    private String placeId;
    private String note;

    @ServerTimestamp
    private Date visited_at;

    public VisitedPlace() {
    }

    public String getPlaceId() {
        return placeId;
    }

    public String getNote() {
        return note;
    }

    public Date getVisited_at() {
        return visited_at;
    }
}
