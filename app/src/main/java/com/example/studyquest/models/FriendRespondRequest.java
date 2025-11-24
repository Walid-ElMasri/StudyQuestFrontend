package com.example.studyquest.models;

public class FriendRespondRequest {
    public String user;
    public String friend_username;
    public String action;

    public FriendRespondRequest(String user, String friend_username, String action) {
        this.user = user;
        this.friend_username = friend_username;
        this.action = action;
    }
}