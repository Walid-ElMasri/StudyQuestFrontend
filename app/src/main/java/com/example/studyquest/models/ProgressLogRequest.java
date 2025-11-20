package com.example.studyquest.models;

public class ProgressLogRequest {
    public String user;
    public int minutes;
    public String subject;

    public ProgressLogRequest(String user, int minutes, String subject) {
        this.user = user;
        this.minutes = minutes;
        this.subject = subject;
    }
}
