package com.example.studyquest.models;

public class Avatar {
    public String user;
    public String avatar_style;

    @Override
    public String toString() {
        return "User: " + user + "\nAvatar: " + avatar_style;
    }
}

