package com.shashimadushan.aliscapper.controller;

import com.shashimadushan.aliscapper.model.Review;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api")
public class ReviewController {

    private static final Logger logger = Logger.getLogger(ReviewController.class.getName());

    @GetMapping("/reviews")
    public ResponseEntity<List<Review>> getReviews(@RequestParam String productId) {

        String apiUrl = "https://feedback.aliexpress.com/pc/searchEvaluation.do?productId="+productId+"&lang=en_US&page=1&pageSize=20&filter=all&sort=complex_default";
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.getForEntity(apiUrl, String.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            List<Review> reviews = extractReviews(response.getBody());
            reviews.forEach(review -> logger.info(review.toString()));
            return ResponseEntity.ok(reviews);
        } else {
            return ResponseEntity.status(response.getStatusCode()).body(Collections.emptyList());
        }
    }

    private List<Review> extractReviews(String jsonResponse) {
        List<Review> reviewList = new ArrayList<>();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(jsonResponse);

            JsonNode reviewsNode = rootNode.path("data").path("evaViewList");

            for (JsonNode reviewNode : reviewsNode) {
                Review review = new Review();
                review.setBuyerName(reviewNode.path("buyerName").asText());
                review.setBuyerCountry(reviewNode.path("buyerCountry").asText());
                review.setFeedback(reviewNode.path("buyerTranslationFeedback").asText());
                review.setEvalDate(reviewNode.path("evalDate").asText());

                List<String> images = new ArrayList<>();
                for (JsonNode imgNode : reviewNode.path("images")) {
                    images.add(imgNode.asText());
                }
                review.setImages(images);

                reviewList.add(review);
            }

        } catch (Exception e) {
            logger.severe("Error extracting reviews: " + e.getMessage());
        }
        return reviewList;
    }
}