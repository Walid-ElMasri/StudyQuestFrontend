package com.example.studyquest.models;

public class Quest {
    public int id;
    public String name;
    public String description;
    public int xp_reward;
    public boolean completed;
    public String quest_type;


    @Override
    public String toString() {
        return "#" + id + " " + name + " (" + xp_reward + " XP)" +
                (completed ? " [DONE]" : "");
    }
}
