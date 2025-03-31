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
import java.util.logging.Logger;

@Service
public class DescriptionService {
    private static final Logger logger = Logger.getLogger(DescriptionService.class.getName());

    @Value("${api.url}")
    private String apiUrl;

    @Value("${api.key}")
    private String apiKey;

    @Value("${site.url:http://localhost:8080}")
    private String siteUrl;

    @Value("${site.name:AliScapper}")
    private String siteName;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public DescriptionService(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    public String generateProductDescription(String userPrompt, Map<String, Object> productData) {
        logger.info("Generating product description for: " + productData.get("title"));
        logger.info("API URL: " + apiUrl);

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(apiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("HTTP-Referer", siteUrl); // Site URL for rankings on openrouter.ai
        headers.set("X-Title", siteName);     // Site title for rankings on openrouter.ai

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "deepseek/deepseek-r1:free");
        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(Map.of("role", "system", "content", "You are an AI-powered product description generator for WooCommerce."));

        // Format the user message with product data and prompt
        StringBuilder userMessage = new StringBuilder();
        userMessage.append("Product Data:\n");
        userMessage.append("Title: ").append(productData.get("title")).append("\n");

        if (productData.containsKey("features") && productData.get("features") instanceof List) {
            userMessage.append("Features:\n");
            List<?> features = (List<?>) productData.get("features");
            for (Object feature : features) {
                userMessage.append("- ").append(feature).append("\n");
            }
        }

        userMessage.append("\nUser Prompt: ").append(userPrompt);
        userMessage.append("\n\nPlease generate a compelling and engaging product description based on the above information.");

        messages.add(Map.of("role", "user", "content", userMessage.toString()));
        requestBody.put("messages", messages);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            logger.info("Sending request to OpenRouter API with DeepSeek model");
            ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.POST, entity, String.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                try {
                    String responseBody = response.getBody();


                    if (responseBody == null || responseBody.isEmpty()) {
                        return "Error: Empty response from API";
                    }

                    JsonNode root = objectMapper.readTree(responseBody);

                    // Check if choices array exists and has elements
                    JsonNode choicesNode = root.path("choices");
                    if (choicesNode.isMissingNode() || choicesNode.isEmpty()) {
                        logger.warning("API response doesn't contain 'choices' array or it's empty");
                        return "Error: API response doesn't contain expected data structure. Response: " + responseBody;
                    }

                    // Safely get the first choice
                    JsonNode firstChoice = choicesNode.get(0);
                    if (firstChoice == null) {
                        logger.warning("First choice is null");
                        return "Error: Could not get the first choice from API response";
                    }

                    // Safely navigate to message and content
                    JsonNode messageNode = firstChoice.path("message");
                    if (messageNode.isMissingNode()) {
                        logger.warning("Message node is missing");
                        return "Error: API response doesn't contain 'message' field";
                    }

                    JsonNode contentNode = messageNode.path("content");
                    if (contentNode.isMissingNode()) {
                        logger.warning("Content node is missing");
                        return "Error: API response doesn't contain 'content' field";
                    }

                    String content = contentNode.asText();
                    logger.info("Successfully extracted content from API response");
                    return content;

                } catch (Exception e) {
                    logger.severe("Error parsing response: " + e.getMessage());
                    e.printStackTrace();
                    return "Error parsing response: " + e.getMessage() + "\nResponse body: " + response.getBody();
                }
            } else {
                logger.warning("API returned non-OK status: " + response.getStatusCode());
                return "Error: " + response.getStatusCode() + "\nResponse body: " + response.getBody();
            }
        } catch (Exception e) {
            logger.severe("Error making API request: " + e.getMessage());
            e.printStackTrace();
            return "Error making API request: " + e.getMessage();
        }
    }
}