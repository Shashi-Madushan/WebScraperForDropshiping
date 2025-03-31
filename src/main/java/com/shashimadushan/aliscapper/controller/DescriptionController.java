package com.shashimadushan.aliscapper.controller;

import com.shashimadushan.aliscapper.service.DescriptionService;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/description")

public class DescriptionController {
    @Autowired
    private DescriptionService descriptionService;

    @PostMapping("/generateDescription")
    public ResponseEntity<Map<String, String>> generateProductDescription(@RequestBody Map<String, Object> request) {
        String userPrompt = (String) request.get("userPrompt");
        Map<String, Object> specifications = (Map<String, Object>) request.get("productData");
        String productName = (String) request.get("productName");

        // Create a combined product data map with both specifications and product name
        Map<String, Object> productData = new HashMap<>(specifications);
        productData.put("title", productName);




        String generatedDescription = descriptionService.generateProductDescription(userPrompt, productData);

        Map<String, String> response = new HashMap<>();
        response.put("description", generatedDescription);
        return ResponseEntity.ok(response);
    }

}
