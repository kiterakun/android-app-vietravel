package com.group6.vietravel.data.models;

import com.google.firebase.firestore.DocumentId;

public class Category {

    @DocumentId
    private String categoryId;
    private String name;
    private String description;

    public Category() {}

    public Category(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
