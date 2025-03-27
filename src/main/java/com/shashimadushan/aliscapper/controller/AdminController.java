package com.shashimadushan.aliscapper.controller;

import com.shashimadushan.aliscapper.dto.ProductDTO;
import com.shashimadushan.aliscapper.dto.UserDTO;
import com.shashimadushan.aliscapper.service.ProductService;
import com.shashimadushan.aliscapper.service.UserDataService;
import com.shashimadushan.aliscapper.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasAuthority('ADMIN')") // Restricts all methods in this class to ADMIN only
public class AdminController {
    @Autowired
    private  UserService userService;
    @Autowired
    private  ProductService productService;
    @Autowired
    private UserDataService userDataService;


    // 1️⃣ Get All Users
    @GetMapping("/users")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        return ResponseEntity.ok(userDataService.getAllUsers());
    }

    // 2️⃣ Delete a User by ID
    @DeleteMapping("/users/{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable String userId) {
        userDataService.deleteUser(userId);
        return ResponseEntity.ok("User deleted successfully.");
    }

    // 3️⃣ Change User Role (USER ↔ ADMIN)
    @PostMapping("/users/{userId}/change-role")
    public ResponseEntity<String> changeUserRole(@PathVariable Long userId, @RequestParam String role) {
//        userService.changeUserRole(userId, role);
        return ResponseEntity.ok("User role updated to " + role);
    }

    // 4️⃣ Get All Products (All users' products)
    @GetMapping("/products")
    public ResponseEntity<List<ProductDTO>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    // 5️⃣ Delete a Product
    @DeleteMapping("/products/{productId}")
    public ResponseEntity<String> deleteProduct(@PathVariable String productId) {
        productService.deleteProduct(productId);
        return ResponseEntity.ok("Product deleted successfully.");
    }

    // 6️⃣ System Statistics (Total Users, Products)
    @GetMapping("/stats")
    public ResponseEntity<?> getSystemStats() {
//        private String status;
        return ResponseEntity.ok("system stats");
    }
}
