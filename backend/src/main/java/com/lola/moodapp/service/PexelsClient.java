package com.lola.moodapp.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class PexelsClient {

    private final WebClient webClient;

    @Value("${pexels.api.key}")
    private String apiKey; // injected after bean creation

    public PexelsClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder
                .baseUrl("https://api.pexels.com")
                .build();
    }

    @PostConstruct
    public void checkApiKey() {
        if (apiKey == null || apiKey.isBlank()) {
            System.err.println("Missing Pexels API key! Please check application.properties.");
        } else {
            System.out.println("Pexels API key successfully loaded.");
        }
    }

    public String fetchMoodImage(String mood) {
        if (mood == null || mood.isBlank()) {
            System.err.println("⚠️ Mood query cannot be empty.");
            return "https://via.placeholder.com/600x400.png?text=Invalid+mood";
        }

        try {
            String jsonResponse = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/v1/search")
                            .queryParam("query", mood)
                            .queryParam("per_page", 1)
                            .build())
                    .header("Authorization", apiKey)
                    .retrieve()
                    .bodyToMono(String.class)
                    .onErrorResume(e -> {
                        System.err.println("❌ Error calling Pexels API: " + e.getMessage());
                        return Mono.just("{}");
                    })
                    .block();

            if (jsonResponse == null || jsonResponse.isBlank()) {
                System.err.println("⚠️ Empty response from Pexels API.");
                return getFallbackImage(mood);
            }

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(jsonResponse);
            JsonNode firstPhoto = root.path("photos").path(0);

            String imageUrl = firstPhoto.path("src").path("large").asText(null);

            if (imageUrl == null || imageUrl.isEmpty()) {
                System.err.println("⚠️ No image found for mood: " + mood);
                return getFallbackImage(mood);
            }

            return imageUrl;

        } catch (Exception e) {
            System.err.println("❌ Exception while fetching Pexels image: " + e.getMessage());
            return getFallbackImage(mood);
        }
    }

    private String getFallbackImage(String mood) {
        return "https://via.placeholder.com/600x400.png?text=" + mood + "+vibes";
    }
}
