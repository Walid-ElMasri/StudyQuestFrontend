package com.example.studyquest.models;

public class FriendAddRequest {
    public String from_user;
    public String to_user;

    public FriendAddRequest(String from_user, String to_user) {
        this.from_user = from_user;
        this.to_user = to_user;
    }
}

