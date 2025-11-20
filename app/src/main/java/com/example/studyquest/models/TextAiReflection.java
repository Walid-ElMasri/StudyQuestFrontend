package com.example.studyquest.models;

public class TextAiReflection {
    public String timestamp;
    public String prompt;
    public String feedback;

    @Override
    public String toString() {
        return timestamp + "\nQ: " + prompt + "\nA: " + feedback;
    }
}

