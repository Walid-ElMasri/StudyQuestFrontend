package com.example.studyquest.models;

public class Badge {
    public String name;
    public String description;
    public int min_xp;

    @Override
    public String toString() {
        return name + " (" + min_xp + " XP) - " + description;
    }
}

