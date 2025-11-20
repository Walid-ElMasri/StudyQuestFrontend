package com.example.studyquest.models;

public class BossAnswerResponse {
    public String status;
    public int boss_hp;
    public int user_hp;
    public int score;

    @Override
    public String toString() {
        return "Status: " + status +
                "\nBoss HP: " + boss_hp +
                "\nYour HP: " + user_hp +
                "\nScore: " + score;
    }
}

