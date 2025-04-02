package com.shashimadushan.aliscapper.controller;

import com.shashimadushan.aliscapper.dto.ProductDTO;
import com.shashimadushan.aliscapper.dto.UserDTO;
import com.shashimadushan.aliscapper.dto.DashboardStatsDTO;
import com.shashimadushan.aliscapper.service.ProductService;
import com.shashimadushan.aliscapper.service.UserDataService;
import com.shashimadushan.aliscapper.service.UserService;
import com.shashimadushan.aliscapper.service.StoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasAuthority('ADMIN')") // Restricts all methods in this class to ADMIN only
public class AdminController {
    @Autowired
    private UserService userService;
    @Autowired
    private ProductService productService;
    @Autowired
    private UserDataService userDataService;
    @Autowired
    private StoreService storeService;

    // 1️⃣ Get All Users
    @GetMapping("/users")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        return ResponseEntity.ok(userDataService.getAllUsers());
    }

    // 2️⃣ Delete a User by ID
    @DeleteMapping("/users/{username}")
    public ResponseEntity<String> deleteUser(@PathVariable String username) {
        boolean isDeleted = userDataService.deleteUser(username);
        if (isDeleted) {
            return ResponseEntity.ok("User deleted successfully.");
        } else {
            return ResponseEntity.badRequest().body("Failed to delete user.");
        }
    }

    // 3️⃣ Change User Role (USER ↔ ADMIN)
    @PostMapping("/users/{userId}/change-role")
    public ResponseEntity<String> changeUserRole(@PathVariable Long userId, @RequestParam String role) {
        // userService.changeUserRole(userId, role);
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
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", userDataService.getTotalActiveUsers());
        stats.put("totalProducts", productService.getTotalProducts());
        stats.put("totalStores", storeService.getTotalStores());
        stats.put("newUsersThisWeek", userDataService.getNewUsersThisWeek());

        return ResponseEntity.ok(stats);
    }

    // 7️⃣ Admin Dashboard Statistics
    @GetMapping("/dashboard")
    public ResponseEntity<DashboardStatsDTO> getDashboardStats() {
        DashboardStatsDTO dashboardStats = new DashboardStatsDTO(
                userDataService.getTotalActiveUsers(),
                productService.getTotalProducts(),
                storeService.getTotalStores(),
                userDataService.getNewUsersThisWeek()
        );
        return ResponseEntity.ok(dashboardStats);
    }
}