package com.shashimadushan.aliscapper.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shashimadushan.aliscapper.dto.ProductDTO;
import com.shashimadushan.aliscapper.model.Product;
import com.shashimadushan.aliscapper.model.Response;
import com.shashimadushan.aliscapper.model.Description;
import com.shashimadushan.aliscapper.security.JwtUtil;
import com.shashimadushan.aliscapper.service.ProductService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/scrape")
@CrossOrigin(origins = "*")
public class ExtentionDataController {

    private ModelMapper modelMapper = new ModelMapper();
    @Autowired
    private ProductService productService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/receive")
    public Response receiveScrapedData(@RequestHeader("Authorization") String authHeader, @RequestBody Map<String, Object> scrapedData) {
        try {
            String token = authHeader.substring(7);

            String username = jwtUtil.extractUsername(token);

            Product product = mapToProduct(scrapedData);
            product.setUserName(username);
            ProductDTO productDTO = modelMapper.map(product, ProductDTO.class);
            productService.saveProduct(productDTO);

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

        // Handle description data - could be either an object or an array
        Object descriptionObj = scrapedData.get("description");
        List<Description> descriptions = new ArrayList<>();

        if (descriptionObj instanceof List) {
            // Handle when description is a list
            List<Map<String, Object>> descriptionList = (List<Map<String, Object>>) descriptionObj;
            descriptions = descriptionList.stream()
                    .map(data -> new Description( (String) data.get("text"),(List<String>) data.get("images")))
                    .toList();
        } else if (descriptionObj instanceof Map) {
            // Handle when description is a single object
            Map<String, Object> descriptionMap = (Map<String, Object>) descriptionObj;
            descriptions.add(new Description(

                    (String) descriptionMap.get("text"),(List<String>) descriptionMap.get("images")));
        }

        product.setDescription(descriptions);

        return product;
    }
}