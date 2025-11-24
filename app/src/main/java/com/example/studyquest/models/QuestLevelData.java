package com.example.studyquest.models;

public class QuestLevelData {
    public String user;
    public int current_level;
    public int total_xp;
    public int xp_to_next_level;

    @Override
    public String toString() {
        return "User: " + user +
                "\nLevel: " + current_level +
                "\nXP: " + total_xp +
                "\nXP to next level: " + xp_to_next_level;
    }
}
