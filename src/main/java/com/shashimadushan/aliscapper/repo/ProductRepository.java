package com.shashimadushan.aliscapper.repo;

import com.shashimadushan.aliscapper.model.Product;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProductRepository extends MongoRepository<Product, String> {
}
