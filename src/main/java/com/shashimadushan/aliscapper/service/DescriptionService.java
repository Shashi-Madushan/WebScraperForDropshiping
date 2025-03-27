package com.shashimadushan.aliscapper.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DescriptionService {
    @Value("${api.url}")
    private String apiUrl;

    @Value("${api.key}")
    private String apiKey;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public DescriptionService(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    public String generateProductDescription(String userPrompt, Map<String, Object> productData) {
        // Using the injected apiUrl, or falling back to OpenRouter URL if not configured


        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(apiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);
//        headers.set("HTTP-Referer", "<YOUR_SITE_URL>"); // Optional
//        headers.set("X-Title", "<YOUR_SITE_NAME>");     // Optional

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "openai/gpt-4o");
        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(Map.of("role", "system", "content", "You are an AI-powered product description generator for WooCommerce."));
        messages.add(Map.of("role", "user", "content", "Product Data: " + productData.toString() + "\nUser Prompt: " + userPrompt));
        requestBody.put("messages", messages);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.POST, entity, String.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                try {
                    JsonNode root = objectMapper.readTree(response.getBody());
                    return root.path("choices").get(0).path("message").path("content").asText();
                } catch (Exception e) {
                    e.printStackTrace();
                    return "Error parsing response: " + e.getMessage();
                }
            } else {
                return "Error: " + response.getStatusCode();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Error making API request: " + e.getMessage();
        }
    }
}