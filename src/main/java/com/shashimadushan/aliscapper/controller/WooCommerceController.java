package com.shashimadushan.aliscapper.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shashimadushan.aliscapper.security.JwtUtil;
import com.shashimadushan.aliscapper.service.WooCommerceProductService;
import com.shashimadushan.aliscapper.service.WooCommerceService;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/woocommerce")
public class WooCommerceController {

    private final WooCommerceService wooCommerceService;
    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper;

    private final WooCommerceProductService wooCommerceProductService;

    public WooCommerceController(WooCommerceService wooCommerceService, JwtUtil jwtUtil, ObjectMapper objectMapper, WooCommerceProductService wooCommerceProductService) {
        this.wooCommerceService = wooCommerceService;
        this.jwtUtil = jwtUtil;
        this.objectMapper = objectMapper;
        this.wooCommerceProductService = wooCommerceProductService;

    }


    @PostMapping(value = "/import-product", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> importProductJson(@RequestParam String storeId,
                                                  @RequestBody String productJson,
                                                  @RequestHeader("Authorization") String token) {
        try {
            String username = jwtUtil.extractUsername(token.substring(7));
            JSONObject wooCommerceProduct = wooCommerceProductService.convertAliExpressToWooCommerce(productJson);

            if (wooCommerceProduct.isEmpty()) {
                return ResponseEntity.badRequest().body("Failed to convert product JSON.");
            }
//            return new ResponseEntity<>(wooCommerceProduct.toString(), HttpStatus.OK);
            return wooCommerceService.importProductToWooCommerce(username, storeId, wooCommerceProduct);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error processing JSON: " + e.getMessage());
        }
    }
}