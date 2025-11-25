package com.example.studyquest.models;

public class FlashcardRequest {

    // must match backend: text + required
    public String text;
    public int required;

    // constructor with ONLY text (default required = 10)
    public FlashcardRequest(String text) {
        this.text = text;
        this.required = 10;
    }

    // constructor with text + required (if you ever want custom number)
    public FlashcardRequest(String text, int required) {
        this.text = text;
        this.required = required;
    }
}
