package com.example.studyquest.models;

public class Quest {
    public int id;
    public String title;
    public String description;
    public int reward_xp;
    public boolean completed;

    @Override
    public String toString() {
        return "#" + id + " " + title + " (" + reward_xp + " XP)" +
                (completed ? " [DONE]" : "");
    }
}

