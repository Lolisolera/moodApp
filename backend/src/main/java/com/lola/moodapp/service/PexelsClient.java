package com.lola.moodapp.service;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.RestClientResponseException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
public class PexelsClient {

    @Value("${pexels.api.key}")
    private String pexelsApiKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostConstruct
    public void init() {
        // Don’t log the whole key in real life; this is just to prove which source is used.
        System.out.println("🔑 Pexels API key loaded (len=" + (pexelsApiKey == null ? 0 : pexelsApiKey.length())
                + ", prefix=" + (pexelsApiKey == null ? "null" : pexelsApiKey.substring(0, Math.min(6, pexelsApiKey.length()))) + ")");
    }

    /**
     * Fetch a representative image URL for a mood using Pexels Search.
     */
    public String fetchMoodImage(String mood) {
        try {
            String q = URLEncoder.encode(mood, StandardCharsets.UTF_8);
            String url = "https://api.pexels.com/v1/search?query=" + q + "&per_page=1";

            HttpHeaders headers = new HttpHeaders();
            // ✅ Pexels expects the raw API key (NO "Bearer ")
            headers.set("Authorization", pexelsApiKey);
            headers.set("Accept", "application/json");
            headers.set("User-Agent", "MoodApp/1.0");

            HttpEntity<Void> entity = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    url, HttpMethod.GET, entity, String.class
            );

            if (response.getStatusCode() != HttpStatus.OK || response.getBody() == null) {
                throw new RuntimeException("Failed to fetch image from Pexels. Status: " + response.getStatusCode());
            }

            JsonNode json = objectMapper.readTree(response.getBody());
            JsonNode photos = json.path("photos");

            if (photos.isArray() && photos.size() > 0) {
                // prefer a decent-sized image
                String large = photos.get(0).path("src").path("large").asText("");
                if (!large.isEmpty()) return large;
                return photos.get(0).path("src").path("original").asText("");
            }

            return "https://via.placeholder.com/600x400?text=No+Image+Found";

        } catch (RestClientResponseException e) {
            // Shows Pexels’ error body (useful for 401/403)
            String body = e.getResponseBodyAsString();
            throw new RuntimeException("Error fetching mood image: " + e.getRawStatusCode() + " " + e.getStatusText()
                    + (body != null ? (": \"" + body + "\"") : ""), e);
        } catch (Exception e) {
            throw new RuntimeException("Error fetching mood image: " + e.getMessage(), e);
        }
    }
}
