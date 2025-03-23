package com.shashimadushan.aliscapper.repo;

import com.shashimadushan.aliscapper.model.Product;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface ProductRepository extends MongoRepository<Product, String> {

    List<Product> findByUserName(String userName);

    long countByUserName(String userName);

    @Query("{'userName': ?0, 'creationDate': {'$gte': ?1, '$lte': ?2}}")
    List<Product> findByUserNameAndCreationDateBetween(String userName, LocalDate startDate, LocalDate endDate);

    @Query("{'creationDate': {'$gte': ?0, '$lte': ?1}}")
    List<Product> findByCreationDateBetween(LocalDate startDate, LocalDate endDate);

    @Query(value = "{'userName': ?0, 'creationDate': ?1}", count = true)
    long countByUserNameAndCreationDate(String userName, LocalDate date);

    @Query(value = "{'creationDate': ?0}", count = true)
    long countByCreationDate(LocalDate date);
}