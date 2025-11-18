package com.example.studyquest.models;

public class BossStartResponse {
    public String boss_name;
    public int boss_hp;
    public int user_hp;
    public int reward_xp;

    @Override
    public String toString() {
        return "Boss: " + boss_name +
                "\nBoss HP: " + boss_hp +
                "\nYour HP: " + user_hp +
                "\nReward XP: " + reward_xp;
    }
}

