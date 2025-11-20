package com.example.studyquest.models;

public class ProgressStats {
    public String user;
    public int total_sessions;
    public int total_minutes;
    public int total_xp;
    public double avg_minutes_per_day;

    @Override
    public String toString() {
        return "User: " + user +
                "\nTotal sessions: " + total_sessions +
                "\nTotal minutes: " + total_minutes +
                "\nTotal XP: " + total_xp +
                "\nAvg minutes/day: " + avg_minutes_per_day;
    }
}

