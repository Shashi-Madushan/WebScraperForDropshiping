package com.shashimadushan.aliscapper.repo;

import com.shashimadushan.aliscapper.model.ConnectedStore;
import com.shashimadushan.aliscapper.dto.ConnectedStoreDto.StorePlatformType;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConnectedStoreRepository extends MongoRepository<ConnectedStore, String> {

    /**
     * Find all stores associated with a specific username
     * @param username the username to search for
     * @return list of connected stores for the username
     */
    List<ConnectedStore> findByUsername(String username);

    /**
     * Find stores by username and platform type
     * @param username the username to search for
     * @param platformType the platform type to filter by
     * @return list of connected stores matching criteria
     */
    List<ConnectedStore> findByUsernameAndPlatformType(String username, StorePlatformType platformType);

    /**
     * Find a specific store by name and username
     * @param storeName the name of the store
     * @param username the username associated with the store
     * @return the connected store if found
     */
    Optional<ConnectedStore> findByStoreNameAndUsername(String storeName, String username);

    /**
     * Check if a store with the given name exists for a username
     * @param storeName the name of the store
     * @param username the username to check
     * @return true if the store exists, false otherwise
     */
    boolean existsByStoreNameAndUsername(String storeName, String username);

    /**
     * Count the number of stores for a specific username
     * @param username the username to count stores for
     * @return the count of stores
     */
    int countByUsername(String username);

    /**
     * Find connected stores for a username
     * @param username the username to search for
     * @param connected the connection status to filter by
     * @return list of stores with the specified connection status
     */
    List<ConnectedStore> findByUsernameAndConnected(String username, boolean connected);
}