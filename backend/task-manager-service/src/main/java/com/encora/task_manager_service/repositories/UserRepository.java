package com.encora.task_manager_service.repositories;

import com.encora.task_manager_service.models.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {

    Optional<User> findByUsername(String username);
    // Add other custom query methods if needed
}