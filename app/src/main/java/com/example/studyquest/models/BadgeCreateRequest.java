package com.example.studyquest.models;

public class BadgeCreateRequest {
    public String name;
    public String description;
    public int min_xp;

    public BadgeCreateRequest(String name, String description, int min_xp) {
        this.name = name;
        this.description = description;
        this.min_xp = min_xp;
    }
}

