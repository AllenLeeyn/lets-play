package com.example.lets_play.repository;

import com.example.lets_play.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * MongoDB repository for {@link Product}. Id type is String (ObjectId).
 * Setup: none; Spring Data provides the implementation when MongoDB is configured.
 */
public interface ProductRepository extends MongoRepository<Product, String> {

    /** Returns a page of products owned by the given user. */
    Page<Product> findByUserId(String userId, Pageable pageable);

    /** Deletes all products owned by the given user (e.g. when cascading from user deletion). */
    void deleteByUserId(String userId);
}
