package com.shashimadushan.aliscapper.controller;

import com.shashimadushan.aliscapper.dto.ConnectedStoreDto;
import com.shashimadushan.aliscapper.dto.ConnectedStoreResponseDto;
import com.shashimadushan.aliscapper.security.JwtUtil;
import com.shashimadushan.aliscapper.service.StoreService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;

/**
 * Controller for managing connected e-commerce stores
 * Handles operations like connecting new stores, retrieving store information,
 * updating store credentials, and removing connected stores
 */
@RestController
@RequestMapping("/api/stores")
@RequiredArgsConstructor
@Slf4j
public class ConnectedStoresController {

    private final StoreService storeService;
    private final JwtUtil jwtUtils;

    @PostMapping
    public ResponseEntity<ConnectedStoreResponseDto> connectStore(@RequestBody ConnectedStoreDto storeDto ,@RequestHeader("Authorization") String token ) {
        log.info("Received request to connect new store: {}", storeDto.getStoreName());
        String username = jwtUtils.extractUsername(token.substring(7));
        ConnectedStoreResponseDto connectedStore = storeService.connectStore(storeDto,username);
        return new ResponseEntity<>(connectedStore, HttpStatus.CREATED);
    }


    @GetMapping
    public ResponseEntity<List<ConnectedStoreResponseDto>> getAllConnectedStores(@RequestHeader("Authorization") String token) {
        System.out.println("Retrieving all connected stores");
        String username = jwtUtils.extractUsername(token.substring(7));
        List<ConnectedStoreResponseDto> stores = storeService.getAllStores(username);
        return ResponseEntity.ok(stores);
    }


    @GetMapping("/{storeId}")
    public ResponseEntity<ConnectedStoreResponseDto> getConnectedStore(@PathVariable String storeId) {
        log.info("Retrieving store with ID: {}", storeId);
        ConnectedStoreResponseDto store = storeService.getStoreById(storeId);
        return ResponseEntity.ok(store);
    }


    @PutMapping("/{storeId}")
    public ResponseEntity<ConnectedStoreResponseDto> updateConnectedStore(
            @PathVariable String storeId,
            @RequestBody ConnectedStoreDto storeDto) {
        log.info("Updating store with ID: {}", storeId);
        ConnectedStoreResponseDto updatedStore = storeService.updateStore(storeId, storeDto);
        return ResponseEntity.ok(updatedStore);
    }


    @DeleteMapping("/{storeId}")
    public ResponseEntity<Void> deleteConnectedStore(@PathVariable String storeId) {
        log.info("Deleting store with ID: {}", storeId);
        storeService.deleteStore(storeId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{storeId}/verify")
    public ResponseEntity<Boolean> verifyStoreConnection(@PathVariable String storeId) {
        log.info("Verifying connection for store with ID: {}", storeId);
        boolean isConnected = storeService.verifyStoreConnection(storeId);
        return ResponseEntity.ok(isConnected);
    }
}