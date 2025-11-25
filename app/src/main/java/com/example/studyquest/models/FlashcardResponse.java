package com.example.studyquest.models;

import java.util.List;

public class FlashcardResponse {
    private List<FlashcardItem> questions;
    private String error;
    private String details;

    public List<FlashcardItem> getQuestions() {
        return questions;
    }

    public String getError() {
        return error;
    }

    public String getDetails() {
        return details;
    }
}
