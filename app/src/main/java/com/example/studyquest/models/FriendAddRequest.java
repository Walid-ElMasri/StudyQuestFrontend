package com.example.studyquest.models;

public class FriendAddRequest {
    public String user;
    public String friend_username;

    public FriendAddRequest(String user, String friend_username) {
        this.user = user;
        this.friend_username = friend_username;
    }
}

