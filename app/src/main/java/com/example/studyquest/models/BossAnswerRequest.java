package com.example.studyquest.models;

public class BossAnswerRequest {
    public String user;
    public Integer choice_idx;

    public BossAnswerRequest(String user, Integer choice_idx) {
        this.user = user;
        this.choice_idx = choice_idx;
    }
}