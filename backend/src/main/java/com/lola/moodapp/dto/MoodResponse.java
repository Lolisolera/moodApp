package com.lola.moodapp.dto;

public class MoodResponse {
    private String mood;
    private String imageUrl;
    private TrackDto track;

    public MoodResponse() {}

    public String getMood() {
        return mood;
    }

    public void setMood(String mood) {
        this.mood = mood;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public TrackDto getTrack() {
        return track;
    }

    public void setTrack(TrackDto track) {
        this.track = track;
    }
}
