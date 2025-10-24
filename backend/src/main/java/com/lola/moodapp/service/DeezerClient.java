package com.lola.moodapp.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Service
public class DeezerClient {

    private final WebClient deezerWebClient;

    public DeezerClient(@Qualifier("deezerWebClient") WebClient deezerWebClient) {
        this.deezerWebClient = deezerWebClient;
    }

    public String fetchMoodTrack(String mood) {
        try {
            // Search for a track related to the user's mood
            return deezerWebClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/search")
                            .queryParam("q", mood)
                            .build())
                    .retrieve()
                    .bodyToMono(String.class)
                    .block(); // Temporarily halts execution until the response arrives (simpler than async for now)
        } catch (WebClientResponseException e) {
            System.err.println("Error fetching from Deezer API: " + e.getMessage());
            return null;
        }
    }
}
