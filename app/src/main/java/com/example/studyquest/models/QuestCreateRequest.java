package com.example.studyquest.models;

public class QuestCreateRequest {
    public String title;
    public String description;
    public int reward_xp;

    public QuestCreateRequest(String title, String description, int reward_xp) {
        this.title = title;
        this.description = description;
        this.reward_xp = reward_xp;
    }
}

