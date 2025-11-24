package com.example.studyquest.models;

public class LeaderboardEntry {
    public String user;  // was "username"
    public int total_xp;
    public int id;       // was "rank"

    @Override
    public String toString() {
        return "#" + id + " " + user + " - " + total_xp + " XP";
    }
}

