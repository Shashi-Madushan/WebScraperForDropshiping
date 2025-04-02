package com.shashimadushan.aliscapper.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shashimadushan.aliscapper.dto.ProductDTO;
import com.shashimadushan.aliscapper.exception.WooCommerceException;
import com.shashimadushan.aliscapper.model.ConnectedStore;
import com.shashimadushan.aliscapper.model.User;
import com.shashimadushan.aliscapper.repo.ConnectedStoreRepository;
import com.shashimadushan.aliscapper.repo.UserRepository;

import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Service
public class WooCommerceService {
    private static final Logger logger = LoggerFactory.getLogger(WooCommerceService.class);

    private final UserRepository userRepository;
    private final ConnectedStoreRepository connectedStoreRepository;
    private final ObjectMapper objectMapper;

    public WooCommerceService(UserRepository userRepository, ConnectedStoreRepository connectedStoreRepository, ObjectMapper objectMapper) {
        this.userRepository = userRepository;
        this.connectedStoreRepository = connectedStoreRepository;
        this.objectMapper = objectMapper;
    }

    public ResponseEntity<String> importProductToWooCommerce(String username, String storeId, JSONObject woocomerceproduct) {

        try {
            // Validate input parameters
            if (username == null || storeId == null || woocomerceproduct == null) {

                throw new WooCommerceException("Username, storeId, and productJson cannot be null");
            }


            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new WooCommerceException("User not found: " + username));


            // Find store

            ConnectedStore connectedStore = connectedStoreRepository.findById(storeId)
                    .orElseThrow(() -> new WooCommerceException("Store not found with ID: " + storeId));


            System.out.println("Calling sendProductToWooCommerce");
            return sendProductToWooCommerce(connectedStore, woocomerceproduct);

        } catch (WooCommerceException e) {

            logger.error("WooCommerce import error: {}", e.getMessage());
            return ResponseEntity.status(org.springframework.http.HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Unexpected error during WooCommerce import", e);
            return ResponseEntity.status(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred: " + e.getMessage());
        }
    }

    private ResponseEntity<String> sendProductToWooCommerce(ConnectedStore store, JSONObject productJson) throws WooCommerceException {
//        // Clean up price values by removing commas
//        if (productJson.has("regular_price")) {
//            String regularPrice = productJson.getString("regular_price").replace(",", "");
//            productJson.put("regular_price", regularPrice);
//        }
//
//        if (productJson.has("sale_price")) {
//            String salePrice = productJson.getString("sale_price").replace(",", "");
//            productJson.put("sale_price", salePrice);
//        }
        String url = store.getStoreUrl() + "/wp-json/wc/v3/products";
        System.out.println("Sending product to WooCommerce API URL: " + url);
        System.out.println("Using API Key: " + store.getApiKey().substring(0, 4) + "...");

        try (CloseableHttpClient client = HttpClients.createDefault()) {

            HttpPost request = new HttpPost(url);

            // Set authentication and headers
            String auth = store.getApiKey() + ":" + store.getApiSecret();
            String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.UTF_8));
            request.setHeader("Authorization", "Basic " + encodedAuth);
            request.setHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE);


            // Set request body
            request.setEntity(new StringEntity(String.valueOf(productJson), StandardCharsets.UTF_8));


            // Execute request

            try (CloseableHttpResponse response = client.execute(request)) {
                int statusCode = response.getStatusLine().getStatusCode();
                System.out.println("Received response with status code: " + statusCode);

                String responseBody = org.apache.http.util.EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
                System.out.println("Response body length: " + responseBody.length());
                System.out.println("Response body preview: " + (responseBody.length() > 100 ? responseBody.substring(0, 100) + "..." : responseBody));

                if (statusCode >= HttpStatus.SC_OK && statusCode < HttpStatus.SC_MULTIPLE_CHOICES) {
                    System.out.println("Successful response");
                    return ResponseEntity.status(org.springframework.http.HttpStatus.valueOf(statusCode)).body(responseBody);
                } else {
                    System.out.println("Error response from WooCommerce API");
                    logger.error("WooCommerce API error: {} - {}", statusCode, responseBody);
                    return ResponseEntity.status(org.springframework.http.HttpStatus.valueOf(statusCode))
                            .body("WooCommerce API error: " + responseBody);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("Error communicating with WooCommerce API", e);
            throw new WooCommerceException("Error communicating with WooCommerce API: " + e.getMessage());
        }
    }
}