package com.shashimadushan.aliscapper.dto;

import com.shashimadushan.aliscapper.dto.ConnectedStoreDto.StorePlatformType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Response DTO for connected store information
 * Contains masked API credentials for security
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConnectedStoreResponseDto {

    private String id;
    private String storeName;
    private String storeUrl;
    private StorePlatformType platformType;
    private String maskedApiKey; // Only shows first and last few characters
    private boolean connected;
    private LocalDateTime connectedAt;
    private LocalDateTime lastSyncAt;

    // Additional metadata about the store
    private int productCount;
    private int orderCount;
}