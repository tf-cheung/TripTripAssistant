package com.example.tripassistant.models;

public class User {
    private String email, username;

    public User() {
        // Required no-argument constructor for Firebase
    }

    public User(String email, String username) {
        this.email = email;
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }
}
