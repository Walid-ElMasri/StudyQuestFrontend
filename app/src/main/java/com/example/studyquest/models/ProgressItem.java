package com.example.studyquest.models;

import com.google.gson.annotations.SerializedName;

public class ProgressItem {
    public String timestamp;

    // Backend sometimes returns duration as "duration_minutes" — map both so minutes show up.
    @SerializedName(value = "minutes", alternate = {"duration_minutes", "duration"})
    public int minutes;

    public String subject;
    public int xp;

    public static final int XP_PER_MINUTE = 2;

    @Override
    public String toString() {
        return timestamp + " - " + subject +
                " (" + minutes + " min, " + getXpWithFallback() + " XP)";
    }

    public int getXpWithFallback() {
        return Math.max(xp, minutes * XP_PER_MINUTE);
    }
}
