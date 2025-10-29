package com.lola.moodapp;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MoodAppApplicationTests {

    @Value("${pexels.api.key:}")
    private String pexelsApiKey;

    @Test
    void contextLoads() {
        // Basic sanity check to ensure Spring context loads
        assertTrue(true);
    }

    @Test
    void pexelsApiKeyIsLoadedFromEnvironment() {
        System.out.println("🔍 Testing Pexels API key loading...");
        assertNotNull(pexelsApiKey, "Pexels API key should not be null");
        assertFalse(pexelsApiKey.isEmpty(), "Pexels API key should not be empty");
        assertTrue(pexelsApiKey.length() > 10, "Pexels API key should be at least 10 characters long");
        System.out.println("✅ Pexels API key loaded successfully! Prefix: " + pexelsApiKey.substring(0, 6));
    }
}
