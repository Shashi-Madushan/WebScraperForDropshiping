package com.shashimadushan.aliscapper.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shashimadushan.aliscapper.model.Product;
import com.shashimadushan.aliscapper.model.Response;
import com.shashimadushan.aliscapper.model.Description;
import com.shashimadushan.aliscapper.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/scrape")
@CrossOrigin(origins = "*")
public class ExtentionDataController {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private ProductService productService;

    @PostMapping("/receive")
    public Response receiveScrapedData(@RequestBody Map<String, Object> scrapedData) {
        try {
            // Convert Map to JSON String (for logging)
            String jsonData = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(scrapedData);
            System.out.println("Received Scraped Data as JSON:");
            System.out.println(jsonData);

            // Convert Map to Product model
            Product product = mapToProduct(scrapedData);

            // Save product to MongoDB
            productService.saveProduct(product);

            // Return success response
            Response response = new Response();
            response.setStatus("success");
            response.setMessage("Data received and saved successfully!");
            return response;
        } catch (Exception e) {
            e.printStackTrace();
            Response response = new Response();
            response.setStatus("error");
            response.setMessage("Failed to process data!");
            return response;
        }
    }

    // Method to convert scraped data Map to Product object
    private Product mapToProduct(Map<String, Object> scrapedData) {
        Product product = new Product();
        product.setProductID((String) scrapedData.get("productID"));
        product.setProductName((String) scrapedData.get("productName"));
        product.setCurrentPrice((String) scrapedData.get("currentPrice"));
        product.setOriginalPrice((String) scrapedData.get("originalPrice"));
        product.setDiscount((String) scrapedData.get("discount"));
        product.setRating((String) scrapedData.get("rating"));
        product.setSoldCount((String) scrapedData.get("soldCount"));

        // Convert lists correctly
        product.setImageLinks((List<String>) scrapedData.get("imageLinks"));
        product.setVideoLinks((List<String>) scrapedData.get("videoLinks"));

        // Convert specifications map correctly
        product.setSpecifications((Map<String, String>) scrapedData.get("specifications"));

        // Convert description data
        List<Map<String, Object>> descriptionData = (List<Map<String, Object>>) scrapedData.get("description");
        List<Description> descriptions = descriptionData.stream()
            .map(data -> new Description((Map<String, String>) data.get("attributes"), (List<String>) data.get("images"), (String) data.get("text")))
            .toList();
        product.setDescription(descriptions);

        return product;
    }
}