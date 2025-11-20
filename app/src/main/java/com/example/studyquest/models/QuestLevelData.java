package com.example.studyquest.models;

public class QuestLevelData {
    public String user;
    public int level;
    public int current_xp;
    public int xp_for_next;

    @Override
    public String toString() {
        return "User: " + user +
                "\nLevel: " + level +
                "\nXP: " + current_xp + "/" + xp_for_next;
    }
}

