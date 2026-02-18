package com.example.lets_play.repository;

import com.example.lets_play.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

/**
 * MongoDB repository for {@link User}. Id type is String (ObjectId).
 * Setup: none; Spring Data provides the implementation when MongoDB is configured.
 */
public interface UserRepository extends MongoRepository<User, String> {

    /** Finds a user by email (unique). */
    Optional<User> findByEmail(String email);

    /** Returns true if a user exists with the given email. */
    boolean existsByEmail(String email);
}
