package com.learnify.Model;

public class UserModel {

    private String name;
    private String email;
    private String password;
    private String profile;

    // No-argument constructor for Firebase
    public UserModel() {
        // Firebase requires this constructor for deserialization
    }

    // Constructor with all fields
    public UserModel(String name, String email, String password ,String profile) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.profile = profile;
    }

    // Getters
    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getProfile() {
        return profile;
    }

}