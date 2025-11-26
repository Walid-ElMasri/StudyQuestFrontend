package com.example.studyquest.models;

public class BossStartResponse {
    public String message;
    public String user;
    public Integer timer_seconds;
    public Integer lives;
    public CurrentQuestion current_question;

    public static class CurrentQuestion {
        public Integer number;
        public Integer total;
        public String question;
        public java.util.List<String> choices;
    }
}