package com.lola.moodapp.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient pexelsWebClient(
            @Value("${pexels.api.key:}") String pexelsKey,
            @Value("${my.pexels.apikey.encoded:}") String encodedKey) {

        String apiKey = (pexelsKey != null && !pexelsKey.isBlank()) ? pexelsKey : null;

        if ((apiKey == null || apiKey.isBlank()) && encodedKey != null && !encodedKey.isBlank()) {
            try {
                byte[] decoded = Base64.getDecoder().decode(encodedKey.trim());
                apiKey = new String(decoded, StandardCharsets.UTF_8).trim();
            } catch (IllegalArgumentException e) {
                // keep apiKey null if decode fails
            }
        }

        // if still null, set empty string to avoid a literal "null" header being sent
        String finalKey = (apiKey == null) ? "" : apiKey;

        return WebClient.builder()
                .baseUrl("https://api.pexels.com/v1")
                .defaultHeader(HttpHeaders.AUTHORIZATION, finalKey)
                .build();
    }

    @Bean
    public WebClient deezerWebClient() {
        return WebClient.builder()
                .baseUrl("https://api.deezer.com")
                .build();
    }
}
