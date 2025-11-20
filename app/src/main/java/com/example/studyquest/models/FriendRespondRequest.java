package com.example.studyquest.models;

public class FriendRespondRequest {
    public String from_user;
    public String to_user;
    public String action; // "accept", "decline", "block"

    public FriendRespondRequest(String from_user, String to_user, String action) {
        this.from_user = from_user;
        this.to_user = to_user;
        this.action = action;
    }
}

