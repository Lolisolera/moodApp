package com.lola.moodapp.service;

import com.lola.moodapp.dto.MoodResponse;
import com.lola.moodapp.dto.TrackDto;
import org.springframework.stereotype.Service;

@Service
public class MoodService {

    public MoodResponse analyzeMood(String mood) {
        // TODO: connect to PexelsClient and DeezerClient later

        // For now, return a simple placeholder response
        MoodResponse response = new MoodResponse();
        response.setMood(mood);
        response.setImageUrl("https://via.placeholder.com/600x400.png?text=" + mood + "+vibes");
        response.setTrack(new TrackDto("Sample Song", "Sample Artist", "https://sample-preview.mp3"));

        return response;
    }
}
