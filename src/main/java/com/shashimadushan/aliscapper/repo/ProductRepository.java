package com.shashimadushan.aliscapper.repo;

import com.shashimadushan.aliscapper.model.Product;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ProductRepository extends MongoRepository<Product, String> {

    List<Product> findByUserName(String userName);

}