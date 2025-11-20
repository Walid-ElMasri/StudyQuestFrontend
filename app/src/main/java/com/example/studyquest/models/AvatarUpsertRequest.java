package com.example.studyquest.models;

public class AvatarUpsertRequest {
    public String user;
    public String avatar_style;

    public AvatarUpsertRequest(String user, String avatar_style) {
        this.user = user;
        this.avatar_style = avatar_style;
    }
}
