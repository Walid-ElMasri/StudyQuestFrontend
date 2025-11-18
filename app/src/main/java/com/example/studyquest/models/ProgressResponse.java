package com.example.studyquest.models;

public class ProgressResponse {
    public String user;
    public int level;
    public int current_xp;
    public int next_level_xp;
    public int streak_days;

    @Override
    public String toString() {
        return "User: " + user +
                "\nLevel: " + level +
                "\nXP: " + current_xp + "/" + next_level_xp +
                "\nStreak: " + streak_days;
    }
}
