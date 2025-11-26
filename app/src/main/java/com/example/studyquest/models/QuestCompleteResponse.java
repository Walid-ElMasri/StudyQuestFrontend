package com.example.studyquest.models;

public class QuestCompleteResponse {
    public String status;
    public int new_level;
    public int new_total_xp;

    @Override
    public String toString() {
        return "Status: " + status +
                "\nNew level: " + new_level +
                "\nNew total XP: " + new_total_xp;
    }
}
