package com.example.studyquest.models;

public class TextAiRequest {
    public String user;
    public String prompt;

    public TextAiRequest(String user, String prompt) {
        this.user = user;
        this.prompt = prompt;
    }
}
