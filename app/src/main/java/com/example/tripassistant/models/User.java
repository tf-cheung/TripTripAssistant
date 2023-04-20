package com.example.tripassistant.models;

public class User {
    private String email;
    private String username;

    private String userId;

    public User() {
        // Required no-argument constructor for Firebase
    }
    public User(String email, String username) {
        this.email = email;
        this.username = username;
    }

    public User(String userId, String username, String email  ) {
        this.email = email;
        this.username = username;
        this.userId=userId;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

}
