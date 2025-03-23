package com.shashimadushan.aliscapper.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shashimadushan.aliscapper.dto.ProductDTO;
import com.shashimadushan.aliscapper.model.Description;
import com.shashimadushan.aliscapper.security.JwtUtil;
import com.shashimadushan.aliscapper.service.ProductService;
import com.shashimadushan.aliscapper.service.WebScraperService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/scrape")
public class InAppWebScraperController {
    private static final Logger logger = LoggerFactory.getLogger(InAppWebScraperController.class);

    private final ProductService productService;
    private final WebScraperService webScraperService;
    private final JwtUtil jwtUtils;

    public InAppWebScraperController(WebScraperService webScraperService, ProductService productService, JwtUtil jwtUtils) {
        this.webScraperService = webScraperService;
        this.productService = productService;
        this.jwtUtils = jwtUtils;
    }

    @PostMapping("/aliexpress")
    public ResponseEntity<?> scrapeAliExpress(@RequestHeader("Authorization") String token, @RequestParam String url) {

        try {
            String userName = jwtUtils.extractUsername(token.substring(7));
            Map<String, Object> productData = webScraperService.scrapeAliExpressProduct(url);

            if (productData == null || productData.containsKey("error")) {
                String errorMessage = productData != null ? (String) productData.get("error") : "Failed to scrape product data";
                logger.error("Scraping failed: {}", errorMessage);
                return ResponseEntity.badRequest().body(errorMessage);
            }

            ProductDTO productDTO = new ProductDTO();
            productDTO.setProductID(getStringValue(productData, "productID"));
            productDTO.setProductName(getStringValue(productData, "productName"));
            productDTO.setCurrentPrice(getStringValue(productData, "currentPrice"));
            productDTO.setOriginalPrice(getStringValue(productData, "originalPrice"));
            productDTO.setDiscount(getStringValue(productData, "discount"));
            productDTO.setRating(getStringValue(productData, "rating"));
            productDTO.setSoldCount(getStringValue(productData, "soldCount"));

            // Handle images safely
            @SuppressWarnings("unchecked")
            Map<String, String> images = (Map<String, String>) productData.get("images");
            productDTO.setImageLinks(images != null ? new ArrayList<>(images.values()) : new ArrayList<>());

            // Handle videos safely
            @SuppressWarnings("unchecked")
            Map<String, String> videos = (Map<String, String>) productData.get("videos");
            productDTO.setVideoLinks(videos != null ? new ArrayList<>(videos.values()) : new ArrayList<>());

            // Handle description safely
            processDescription(productDTO, productData);

            // Handle specifications safely
            @SuppressWarnings("unchecked")
            Map<String, String> specifications = (Map<String, String>) productData.get("specifications");
            productDTO.setSpecifications(specifications != null ? specifications : Collections.emptyMap());

            productDTO.setUserName(userName);
            productDTO.setCreationDate(LocalDate.now());

            productService.saveProduct(productDTO);
            return ResponseEntity.ok().build();

        } catch (Exception e) {
            logger.error("Error processing scrape request", e);
            return ResponseEntity.internalServerError().body("Failed to process scrape request: " + e.getMessage());
        }
    }

    private String getStringValue(Map<String, Object> data, String key) {
        Object value = data.get(key);
        return value != null ? value.toString() : "";
    }

    private void processDescription(ProductDTO productDTO, Map<String, Object> productData) {
        try {
            String descriptionJson = getStringValue(productData, "description");
            if (descriptionJson != null && !descriptionJson.isEmpty()) {
                ObjectMapper objectMapper = new ObjectMapper();
                Map<String, List<String>> descriptionMap = objectMapper.readValue(
                    descriptionJson,
                    new TypeReference<Map<String, List<String>>>() {}
                );

                List<Description> descriptions = new ArrayList<>();
                List<String> texts = descriptionMap.getOrDefault("text", Collections.emptyList());
                for (String text : texts) {
                    Description desc = new Description();
                    desc.setText(text);
                    descriptions.add(desc);
                }
                productDTO.setDescription(descriptions);
            } else {
                productDTO.setDescription(new ArrayList<>());
            }
        } catch (JsonProcessingException e) {
            logger.error("Error processing description JSON", e);
            productDTO.setDescription(new ArrayList<>());
        }
    }
}