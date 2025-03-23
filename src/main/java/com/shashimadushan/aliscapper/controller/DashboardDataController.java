package com.shashimadushan.aliscapper.controller;

import com.shashimadushan.aliscapper.dto.ProductCountDTO;
import com.shashimadushan.aliscapper.dto.DailyProductCountDTO;
import com.shashimadushan.aliscapper.dto.ConnectedStoreResponseDto;
import com.shashimadushan.aliscapper.dto.DashboardDataDTO;
import com.shashimadushan.aliscapper.security.JwtUtil;
import com.shashimadushan.aliscapper.service.ProductService;
import com.shashimadushan.aliscapper.service.ConnectedStoreService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardDataController {

    @Autowired
    private ProductService productService;

    @Autowired
    private JwtUtil jwtUtils;

    @Autowired
    private ConnectedStoreService connectedStoreService;

    @GetMapping
    public ResponseEntity<DashboardDataDTO> getDashboardData(@RequestHeader("Authorization") String token) {
        String userId = jwtUtils.extractUsername(token.replace("Bearer ", ""));

        long productCount = productService.getUserProductCount(userId);
        List<DailyProductCountDTO> dailyProductCounts = productService.getUserDailyProductCount(userId);
        List<ConnectedStoreResponseDto> connectedStores = connectedStoreService.getAllStores();

        DashboardDataDTO dashboardData = new DashboardDataDTO(
            new ProductCountDTO(userId, productCount),
            dailyProductCounts,
            connectedStores
        );

        return ResponseEntity.ok(dashboardData);
    }
}