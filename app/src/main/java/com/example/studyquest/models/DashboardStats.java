package com.example.studyquest.models;

public class DashboardStats {
    public String user;
    public int total_xp;
    public int level;
    public int completed_quests;
    public int active_streak_days;

    @Override
    public String toString() {
        return "User: " + user +
                "\nTotal XP: " + total_xp +
                "\nLevel: " + level +
                "\nCompleted quests: " + completed_quests +
                "\nStreak: " + active_streak_days + " days";
    }
}
