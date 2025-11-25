package com.example.studyquest.models;

import java.util.List;

public class AvailableQuestsResponse {

    private List<Quest> available_quests;
    private Integer remaining;
    private String message; // when no quests left

    public List<Quest> getAvailable_quests() {
        return available_quests;
    }

    public Integer getRemaining() {
        return remaining;
    }

    public String getMessage() {
        return message;
    }
}
