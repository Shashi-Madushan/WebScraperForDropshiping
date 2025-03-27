package com.shashimadushan.aliscapper.controller;

import com.shashimadushan.aliscapper.service.DescriptionService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/description")
@RequiredArgsConstructor
public class DescriptionController {
    private DescriptionService discriptionServise;

    @PostMapping("/generateDescription")
    public String generateProductDescription(@RequestBody Map<String, Object> request) {
        String userPrompt = (String) request.get("userPrompt");
        Map<String, Object> productData = (Map<String, Object>) request.get("productData");
        return discriptionServise.generateProductDescription(userPrompt, productData);
    }
}
