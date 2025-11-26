package com.example.studyquest.models;

public class BossStatusResponse {
    public String status;
    public int remaining_seconds;
    public int boss_hp;
    public int user_hp;

    @Override
    public String toString() {
        return "Status: " + status +
                "\nTime left: " + remaining_seconds + "s" +
                "\nBoss HP: " + boss_hp +
                "\nYour HP: " + user_hp;
    }
}

