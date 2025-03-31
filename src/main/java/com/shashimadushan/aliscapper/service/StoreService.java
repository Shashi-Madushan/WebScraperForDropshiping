package com.shashimadushan.aliscapper.service;

import com.shashimadushan.aliscapper.dto.ConnectedStoreDto;
import com.shashimadushan.aliscapper.dto.ConnectedStoreResponseDto;
import com.shashimadushan.aliscapper.model.ConnectedStore;


import com.shashimadushan.aliscapper.repo.ConnectedStoreRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementation of the ConnectedStoreService interface for MongoDB
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class StoreService {

    private final ConnectedStoreRepository storeRepository;
    private final ConnectedStoreRepository connectedStoreRepository;


    public ConnectedStoreResponseDto connectStore(ConnectedStoreDto storeDto ,String username) {
        log.info("Connecting new store: {}", storeDto.getStoreName());



        if (storeRepository.existsByStoreNameAndUsername(storeDto.getStoreName(), username)) {
            System.out.println(("A store with this name is already connected"));
        }

        boolean connectionSuccessful = testConnection(storeDto);
        if (!connectionSuccessful) {
            System.out.println(("Failed to connect to store. Please check your credentials."));
        }

        // Create and save the new store
        ConnectedStore store = mapToDocument(storeDto);
        store.setUsername(username);
        store.setConnected(true);
        store.setConnectedAt(LocalDateTime.now());

        ConnectedStore savedStore = storeRepository.save(store);
        log.info("Successfully connected store with ID: {}", savedStore.getId());

        return mapToResponseDto(savedStore);
    }


    public List<ConnectedStoreResponseDto> getAllStores( String username) {

        List<ConnectedStore> stores = storeRepository.findByUsername(username);

        return stores.stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }


    public ConnectedStoreResponseDto getStoreById(String storeId) {
        ConnectedStore store = findStoreById(storeId);
        return mapToResponseDto(store);
    }

    public ConnectedStoreResponseDto updateStore(String storeId, ConnectedStoreDto storeDto) {
        ConnectedStore existingStore = findStoreById(storeId);

        // Update store properties
        existingStore.setStoreName(storeDto.getStoreName());
        existingStore.setStoreUrl(storeDto.getStoreUrl());
        existingStore.setPlatformType(storeDto.getPlatformType());
        existingStore.setApiKey(storeDto.getApiKey());
        existingStore.setApiSecret(storeDto.getApiSecret());
        existingStore.setAccessToken(storeDto.getAccessToken());


        // Test connection with updated credentials
        boolean connectionSuccessful = testConnection(storeDto);
        existingStore.setConnected(connectionSuccessful);

        ConnectedStore updatedStore = storeRepository.save(existingStore);
        log.info("Updated store with ID: {}", updatedStore.getId());

        return mapToResponseDto(updatedStore);
    }

    public void deleteStore(String storeId) {
        ConnectedStore store = findStoreById(storeId);
        storeRepository.delete(store);
        log.info("Deleted store with ID: {}", storeId);
    }

    public boolean verifyStoreConnection(String storeId) {
        ConnectedStore store = findStoreById(storeId);

        ConnectedStoreDto storeDto = ConnectedStoreDto.builder()
                .storeName(store.getStoreName())
                .storeUrl(store.getStoreUrl())
                .platformType(store.getPlatformType())
                .apiKey(store.getApiKey())
                .apiSecret(store.getApiSecret())
                .accessToken(store.getAccessToken())
                .build();

        boolean isConnected = testConnection(storeDto);

        // Update connection status if it has changed
        if (store.isConnected() != isConnected) {
            store.setConnected(isConnected);
            storeRepository.save(store);
        }

        return isConnected;
    }

    /**
     * Helper method to find a store by ID and verify ownership
     */
    private ConnectedStore findStoreById(String storeId) {
        String currentUserId = "";

        Optional<ConnectedStore> store = storeRepository.findById(storeId);


        // Verify the store belongs to the current user
        if (!store.get().getUsername().equals(currentUserId)) {
            System.out.println(("Store not found with ID: " + storeId));
        }

        return store.orElse(null);
    }

    /**
     * Test connection to the store using the provided credentials
     */
    private boolean testConnection(ConnectedStoreDto storeDto) {
        try {
//            return apiConnectionTester.testConnection(
//                    storeDto.getPlatformType(),
//                    storeDto.getStoreUrl(),
//                    storeDto.getApiKey(),
//                    storeDto.getApiSecret(),
//                    storeDto.getAccessToken()
//            );
        } catch (Exception e) {
            log.error("Error testing connection to store: {}", e.getMessage());
            return false;
        }
        return true;
    }

    /**
     * Map DTO to MongoDB document
     */
    private ConnectedStore mapToDocument(ConnectedStoreDto dto) {
        return ConnectedStore.builder()
                .storeName(dto.getStoreName())
                .storeUrl(dto.getStoreUrl())
                .platformType(dto.getPlatformType())
                .apiKey(dto.getApiKey())
                .apiSecret(dto.getApiSecret())
                .accessToken(dto.getAccessToken())
                .build();
    }

    /**
     * Map MongoDB document to response DTO
     */
    private ConnectedStoreResponseDto mapToResponseDto(ConnectedStore store) {
        return ConnectedStoreResponseDto.builder()
                .id(store.getId())
                .storeName(store.getStoreName())
                .storeUrl(store.getStoreUrl())
                .platformType(store.getPlatformType())
                .maskedApiKey(maskApiKey(store.getApiKey()))
                .connected(store.isConnected())
                .connectedAt(store.getConnectedAt())
                .lastSyncAt(store.getLastSyncAt())
                .productCount(store.getProductCount())
                .orderCount(store.getOrderCount())
                .build();
    }

    /**
     * Mask API key for security (show only first 4 and last 4 characters)
     */
    private String maskApiKey(String apiKey) {
        if (apiKey == null || apiKey.length() <= 8) {
            return "****";
        }

        return apiKey.substring(0, 4) + "****" + apiKey.substring(apiKey.length() - 4);
    }
    public   int getConnetedStoreCount(String username){
        return connectedStoreRepository.countByUsername(username);
    }
}