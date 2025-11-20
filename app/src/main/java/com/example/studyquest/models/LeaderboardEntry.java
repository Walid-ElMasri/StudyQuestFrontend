package com.example.studyquest.models;

public class LeaderboardEntry {
    public String username;
    public int total_xp;
    public int rank;

    @Override
    public String toString() {
        return "#" + rank + " " + username + " - " + total_xp + " XP";
    }
}

