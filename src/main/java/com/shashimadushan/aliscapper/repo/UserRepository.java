package com.shashimadushan.aliscapper.repo;
import org.springframework.data.mongodb.repository.MongoRepository;
import com.shashimadushan.aliscapper.model.User;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
