package com.example.studyquest.models;

import java.util.List;

public class BossAnswerResponse {
    public Boolean correct;
    public String feedback;
    public Integer lives;
    public Integer score;
    public Integer timer_remaining;
    public NextQuestion next_question;

    public static class NextQuestion {
        public Integer number;
        public Integer total;
        public String question;
        public List<String> choices;
    }

    public String status;
    public Integer xp_reward;
    public Integer total_questions;
    public Integer lives_remaining;
    public Boolean ended;
}