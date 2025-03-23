package com.shashimadushan.aliscapper.repo;


import com.shashimadushan.aliscapper.model.ConnectedStore;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConnectedStoreRepository extends MongoRepository<ConnectedStore, String> {


    List<ConnectedStore> findByUserId(String userId);


    Optional<ConnectedStore> findByStoreNameAndUserId(String storeName, String userId);



    boolean existsByStoreNameAndUserId(String storeName, String userId);
}