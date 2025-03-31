package com.shashimadushan.aliscapper.controller;

import com.shashimadushan.aliscapper.dto.ProductDTO;
import com.shashimadushan.aliscapper.security.JwtUtil;
import com.shashimadushan.aliscapper.service.WooCommerceService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/woocommerce")
public class WooCommerceController {
    private final WooCommerceService wooCommerceService;
    private final JwtUtil jwtUtil;
    public WooCommerceController(WooCommerceService wooCommerceService, JwtUtil jwtUtil) {
        this.wooCommerceService = wooCommerceService;
        this.jwtUtil = jwtUtil;
    }


    @PostMapping("/import-product")
    public ResponseEntity<String> importProduct(@RequestParam String storeId, @RequestBody ProductDTO productData , @RequestHeader("Authorization") String token) {
        String username = jwtUtil.extractUsername(token.substring(7));
        System.out.println(username);
        System.out.println( storeId + " " + productData);
        return wooCommerceService.importProductToWooCommerce(username,storeId, productData);
    }
}