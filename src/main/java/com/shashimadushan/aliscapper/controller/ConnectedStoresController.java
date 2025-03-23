package com.shashimadushan.aliscapper.controller;

import com.shashimadushan.aliscapper.dto.ConnectedStoreDto;
import com.shashimadushan.aliscapper.dto.ConnectedStoreResponseDto;
import com.shashimadushan.aliscapper.service.ConnectedStoreService;
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
@RequestMapping("/api/v1/stores")
@RequiredArgsConstructor
@Slf4j
public class ConnectedStoresController {

    private final ConnectedStoreService connectedStoreService;


    @PostMapping
    public ResponseEntity<ConnectedStoreResponseDto> connectStore(@RequestBody ConnectedStoreDto storeDto) {
        log.info("Received request to connect new store: {}", storeDto.getStoreName());
        ConnectedStoreResponseDto connectedStore = connectedStoreService.connectStore(storeDto);
        return new ResponseEntity<>(connectedStore, HttpStatus.CREATED);
    }


    @GetMapping
    public ResponseEntity<List<ConnectedStoreResponseDto>> getAllConnectedStores() {
        log.info("Retrieving all connected stores");
        List<ConnectedStoreResponseDto> stores = connectedStoreService.getAllStores();
        return ResponseEntity.ok(stores);
    }


    @GetMapping("/{storeId}")
    public ResponseEntity<ConnectedStoreResponseDto> getConnectedStore(@PathVariable String storeId) {
        log.info("Retrieving store with ID: {}", storeId);
        ConnectedStoreResponseDto store = connectedStoreService.getStoreById(storeId);
        return ResponseEntity.ok(store);
    }


    @PutMapping("/{storeId}")
    public ResponseEntity<ConnectedStoreResponseDto> updateConnectedStore(
            @PathVariable String storeId,
            @RequestBody ConnectedStoreDto storeDto) {
        log.info("Updating store with ID: {}", storeId);
        ConnectedStoreResponseDto updatedStore = connectedStoreService.updateStore(storeId, storeDto);
        return ResponseEntity.ok(updatedStore);
    }


    @DeleteMapping("/{storeId}")
    public ResponseEntity<Void> deleteConnectedStore(@PathVariable String storeId) {
        log.info("Deleting store with ID: {}", storeId);
        connectedStoreService.deleteStore(storeId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{storeId}/verify")
    public ResponseEntity<Boolean> verifyStoreConnection(@PathVariable String storeId) {
        log.info("Verifying connection for store with ID: {}", storeId);
        boolean isConnected = connectedStoreService.verifyStoreConnection(storeId);
        return ResponseEntity.ok(isConnected);
    }
}