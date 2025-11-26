package com.example.studyquest.models;

import com.google.gson.annotations.SerializedName;

public class ProgressItem {
    public String timestamp;

    // Backend sometimes returns duration as "duration_minutes" — map both so minutes show up.
    @SerializedName(value = "minutes", alternate = {"duration_minutes", "duration"})
    public int minutes;

    public String subject;
    public int xp;

    @Override
    public String toString() {
        return timestamp + " - " + subject +
                " (" + minutes + " min, " + xp + " XP)";
    }
}
