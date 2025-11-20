package com.example.studyquest.models;

public class UserCreateRequest {
    public String username;
    public String email;

    public UserCreateRequest(String username, String email) {
        this.username = username;
        this.email = email;
    }
}
