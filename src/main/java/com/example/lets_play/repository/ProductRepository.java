package com.example.lets_play.repository;

import com.example.lets_play.model.Product;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductRepository extends MongoRepository<Product, String> {

    Page<Product> findByUserId(String userId, Pageable pageable);

    void deleteByUserId(String userId);
}
