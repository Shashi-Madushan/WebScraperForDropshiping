package com.shashimadushan.aliscapper.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.shashimadushan.aliscapper.dto.DailyProductCountDTO;
import com.shashimadushan.aliscapper.dto.ProductCountDTO;
import com.shashimadushan.aliscapper.dto.ProductDTO;
import com.shashimadushan.aliscapper.model.Description;
import com.shashimadushan.aliscapper.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.shashimadushan.aliscapper.model.Product;
import com.shashimadushan.aliscapper.service.ProductService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    @Autowired
    private ProductService productService;

    @Autowired
    private JwtUtil jwtUtils;

    @GetMapping
    public List<ProductDTO> getUserProducts(@RequestHeader("Authorization") String token) {
        String userId = jwtUtils.extractUsername(token.replace("Bearer ", ""));
        return productService.getUserProducts(userId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProductById(@PathVariable String id, @RequestHeader("Authorization") String token) {
        String userId = jwtUtils.extractUsername(token.replace("Bearer ", ""));
        return productService.getProductById(id, userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/create")
    public Product createProduct(@RequestBody ProductDTO productDto, @RequestHeader("Authorization") String token) {
        String username = jwtUtils.extractUsername(token.replace("Bearer ", ""));
        productDto.setUserName(username);
        return productService.saveProduct(productDto);
    }

    @DeleteMapping("/{id}")
    public String deleteProduct(@PathVariable String id, @RequestHeader("Authorization") String token) {
        String userId = jwtUtils.extractUsername(token.substring(7));
        productService.deleteProduct(id, userId);
        return "Product deleted successfully!";
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable String id, @RequestBody Map<String, Object> requestBody, @RequestHeader("Authorization") String token) {

        String userId = jwtUtils.extractUsername(token.replace("Bearer ", ""));

        // Handle the description field specially
        Object descriptionObj = requestBody.get("description");
        List<Description> descriptionList = new ArrayList<>();

        if (descriptionObj instanceof Map) {
            Map<String, Object> descMap = (Map<String, Object>) descriptionObj;
            Description description = new Description();

            // Set text if available
            if (descMap.containsKey("text")) {
                description.setText(descMap.get("text").toString());
            }

            // Set images if available
            if (descMap.containsKey("images") && descMap.get("images") instanceof List) {
                description.setImages((List<String>) descMap.get("images"));
            } else {
                description.setImages(new ArrayList<>());
            }

            descriptionList.add(description);
        }

        // Replace the description object with our properly formatted list
        requestBody.put("description", descriptionList);

        // Convert Map to ProductDTO
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule()); // For handling LocalDate
        ProductDTO productDto = objectMapper.convertValue(requestBody, ProductDTO.class);

        // Ensure we're not overriding the user ID
        productDto.setUserName(userId);
        System.out.println("ProductDTO: " + productDto.toString());

        Product updatedProduct = productService.updateProduct(id, productDto, userId);

        if (updatedProduct == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(updatedProduct);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/all")
    public ResponseEntity<List<ProductDTO>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @GetMapping("/count")
    public ResponseEntity<ProductCountDTO> getUserProductCount(@RequestHeader("Authorization") String token) {
        String userId = jwtUtils.extractUsername(token.replace("Bearer ", ""));
        long count = productService.getUserProductCount(userId);
        return ResponseEntity.ok(new ProductCountDTO(userId, count));
    }

    @GetMapping("/daily-count")
    public ResponseEntity<List<DailyProductCountDTO>> getUserDailyProductCount(@RequestHeader("Authorization") String token) {
        String userId = jwtUtils.extractUsername(token.replace("Bearer ", ""));
        List<DailyProductCountDTO> dailyCounts = productService.getUserDailyProductCount(userId);
        return ResponseEntity.ok(dailyCounts);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/all-users/daily-count")
    public ResponseEntity<Map<String, List<DailyProductCountDTO>>> getAllUsersDailyProductCount() {
        Map<String, List<DailyProductCountDTO>> allUsersDailyCounts = productService.getAllUsersDailyProductCount();
        return ResponseEntity.ok(allUsersDailyCounts);
    }
}
