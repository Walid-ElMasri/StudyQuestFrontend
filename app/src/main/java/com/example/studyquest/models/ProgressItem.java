package com.example.studyquest.models;

public class ProgressItem {
    public String timestamp;
    public int minutes;
    public String subject;
    public int xp;

    @Override
    public String toString() {
        return timestamp + " - " + subject +
                " (" + minutes + " min, " + xp + " XP)";
    }
}
