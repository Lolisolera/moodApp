package com.lola.moodapp.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Service
public class PexelsClient {

    private final WebClient pexelsWebClient;

    public PexelsClient(@Qualifier("pexelsWebClient") WebClient pexelsWebClient) {
        this.pexelsWebClient = pexelsWebClient;
    }

    public String fetchMoodImage(String mood) {
        try {
            // Search for an image based on the user's mood
            return pexelsWebClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/search")
                            .queryParam("query", mood)
                            .queryParam("per_page", 1)
                            .build())
                    .retrieve()
                    .bodyToMono(String.class)
                    .block(); // Blocking for simplicity (fine for small API calls)
        } catch (WebClientResponseException e) {
            System.err.println("Error fetching from Pexels API: " + e.getMessage());
            return null;
        }
    }
}
