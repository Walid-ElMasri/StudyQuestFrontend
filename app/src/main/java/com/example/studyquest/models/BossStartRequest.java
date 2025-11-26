package com.example.studyquest.models;

public class BossStartRequest {
    public String user;
    public String difficulty;
    public Integer total_questions;
    public Integer time_limit_seconds;

    public BossStartRequest(String user, String difficulty, Integer total_questions, Integer time_limit_seconds) {
        this.user = user;
        this.difficulty = difficulty;
        this.total_questions = total_questions;
        this.time_limit_seconds = time_limit_seconds;
    }

    public BossStartRequest(String user) {
        this.user = user;
    }
}