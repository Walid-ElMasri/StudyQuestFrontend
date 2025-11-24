package com.example.studyquest.models;

public class QuestCompleteResponse {
    public String message;
    public int earned_xp;
    public int total_xp;
    public int current_level;

    @Override
    public String toString() {
        return "Message: " + message +
                "\nEarned XP: " + earned_xp +
                "\nTotal XP: " + total_xp +
                "\nCurrent level: " + current_level;
    }
}
