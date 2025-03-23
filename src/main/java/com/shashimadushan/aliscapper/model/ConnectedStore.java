package com.shashimadushan.aliscapper.model;

import com.shashimadushan.aliscapper.dto.ConnectedStoreDto.StorePlatformType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

/**
 * MongoDB document for storing connected e-commerce store information
 */
@Document(collection = "connected_stores")
@CompoundIndexes({
        @CompoundIndex(name = "user_store_idx", def = "{'userId': 1, 'storeName': 1}", unique = true)
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConnectedStore {

    @Id
    private String id;

    private String storeName;

    private String storeUrl;

    private StorePlatformType platformType;

    private String apiKey;

    private String apiSecret;

    private String accessToken;

    private String storeIdentifier;

    private boolean connected;

    @CreatedDate
    private LocalDateTime connectedAt;

    private LocalDateTime lastSyncAt;

    private int productCount;

    private int orderCount;

    private String userId;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}