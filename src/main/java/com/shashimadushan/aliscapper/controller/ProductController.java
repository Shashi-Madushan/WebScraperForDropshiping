package com.shashimadushan.aliscapper.controller;


import com.shashimadushan.aliscapper.dto.ProductDTO;
import com.shashimadushan.aliscapper.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.shashimadushan.aliscapper.model.Product;
import com.shashimadushan.aliscapper.service.ProductService;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "*")
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

    @PostMapping("/create")
    public Product createProduct(@RequestBody ProductDTO productDto, @RequestHeader("Authorization") String token) {
        String username = jwtUtils.extractUsername(token.replace("Bearer ", ""));
        productDto.setUserName(username);
        return productService.saveProduct(productDto);
    }

    @DeleteMapping("/{id}")
    public String deleteProduct(@PathVariable String id, @RequestHeader("Authorization") String token) {
        String userId = jwtUtils.extractUsername(token.replace("Bearer ", ""));
        productService.deleteProduct(id, userId);
        return "Product deleted successfully!";
    }
    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/all")
    public ResponseEntity<List<ProductDTO>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }
}
