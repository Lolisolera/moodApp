package com.lola.moodapp.dto;

public class MoodRequest {

    private String mood;

    // --- Getter and Setter ---
    public String getMood() {
        return mood;
    }

    public void setMood(String mood) {
        this.mood = mood;
    }
}
// These 3 DTO java classes (Data Transfer Objects) connect the backend responses.
// These are simple Java classes used to move data between the frontend and backend.
// This in particular recieves the user input from the frontend and returns a JSON object, e.g.
// e.g { "mood": "calm and dreamy" }