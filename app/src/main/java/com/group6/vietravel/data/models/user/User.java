package com.group6.vietravel.data.models.user;
import com.google.firebase.firestore.ServerTimestamp;
import java.util.Date;

// Model cho collection "users" trên Firestore
public class User {

    private String uid;
    private String username;
    private String email;
    private String role;
    private String avatar_url;
    private long points;
    private String status;

    @ServerTimestamp
    private Date created_at;

    public User() {
    }

    // Constructor để tạo người dùng mới
    public User(String uid, String username, String email) {
        this.uid = uid;
        this.username = username;
        this.email = email;
        this.role = "user";
        this.avatar_url = "";
        this.points = 0;
        this.status = "active";
    }

    public String getUid() { return uid; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getRole() { return role; }
    public String getAvatar_url() { return avatar_url; }
    public long getPoints() { return points; }
    public String getStatus() { return status; }
    public Date getCreated_at() { return created_at; }

    public void setAvatar_url(String avatar_url) {
        this.avatar_url = avatar_url;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public void setEmail(String email) {
        this.email = email;
    }
}