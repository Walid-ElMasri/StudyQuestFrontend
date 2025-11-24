package com.example.studyquest.models;

public class Friend {
    public String user;
    public String friend_username;
    public String status;
    public String since;
    public int id;

    @Override
    public String toString() {
        return friend_username + " (" + status + ") since " + since;
    }
}