package com.lola.moodapp.service;

import com.lola.moodapp.dto.MoodResponse;
import com.lola.moodapp.dto.TrackDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class MoodService {

    private final PexelsClient pexelsClient;
    private final DeezerClient deezerClient;
    private final ObjectMapper objectMapper;

    @Autowired
    public MoodService(PexelsClient pexelsClient, DeezerClient deezerClient, ObjectMapper objectMapper) {
        this.pexelsClient = pexelsClient;
        this.deezerClient = deezerClient;
        this.objectMapper = objectMapper;
    }

    public MoodResponse analyzeMood(String mood) {
        try {
            // Fetch final image URL from Pexels (no need to parse JSON)
            String imageUrl = pexelsClient.fetchMoodImage(mood);

            // Fetch track JSON from Deezer
            String deezerResponse = deezerClient.fetchMoodTrack(mood);
            JsonNode deezerJson = objectMapper.readTree(deezerResponse);

            // Extract first track info (if available)
            JsonNode firstTrack = deezerJson.path("data").path(0);
            String trackTitle = firstTrack.path("title").asText("");
            String artistName = firstTrack.path("artist").path("name").asText("");
            String previewUrl = firstTrack.path("preview").asText("");

            if (trackTitle.isEmpty() || artistName.isEmpty() || previewUrl.isEmpty()) {
                trackTitle = "a mystery tune 🎧";
                artistName = "no perfect match found";
            }

            TrackDto track = new TrackDto(trackTitle, artistName, previewUrl);

            // Return combined response
            MoodResponse response = new MoodResponse();
            response.setMood(mood);
            response.setImageUrl(imageUrl);
            response.setTrack(track);

            return response;

        } catch (Exception e) {
            throw new RuntimeException("Error analyzing mood: " + e.getMessage(), e);
        }
    }
}

// This file is the logic of the application. It connects to the 2 APIs and makes them to return one combined response to the user.
// Gets image JSON from Pexels, gets song JSON from Deezer, Parses raw JSON strings into usable data,
//...Combines both results into one response object for the controller