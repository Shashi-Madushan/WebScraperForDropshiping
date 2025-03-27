package com.shashimadushan.aliscapper.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;



/**
 * Data Transfer Object for connected store information
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConnectedStoreDto {

    private String storeName;

    private String storeUrl;

    private StorePlatformType platformType;

    private String apiKey;

    private String apiSecret;

    // For OAuth-based platforms like Shopify
    private String accessToken;

    // Additional fields that might be needed for specific platforms
    private String storeIdentifier;

    /**
     * Enum representing supported e-commerce platforms
     */
    public enum StorePlatformType {
        WOOCOMMERCE,
        SHOPIFY,
        MAGENTO,
        PRESTASHOP,
        OTHER
    }
}