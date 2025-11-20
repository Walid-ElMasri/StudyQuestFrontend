package com.example.studyquest.models;

public class ProgressLogResponse {
    public String status;
    public int gained_xp;
    public int new_streak;

    @Override
    public String toString() {
        return "Status: " + status +
                "\nGained XP: " + gained_xp +
                "\nNew streak: " + new_streak;
    }
}

