package com.lola.moodapp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient pexelsWebClient() {
        return WebClient.builder()
                .baseUrl("https://api.pexels.com/v1")
                .defaultHeader("Authorization", System.getenv("PEXELS_API_KEY"))
                .build();
    }

    @Bean
    public WebClient deezerWebClient() {
        return WebClient.builder()
                .baseUrl("https://api.deezer.com")
                .build();
    }
}
