package com.example.lets_play.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Product entity stored in MongoDB collection {@code products}.
 * Id is MongoDB ObjectId. {@code userId} is the owner's user id (products are cascade-deleted when that user is deleted).
 */
@Document(collection = "products")
public class Product {

    @Id
    private String id;

    private String name;
    private String description;
    private double price;
    private int quantity;
    /** Owner's user id (MongoDB ObjectId). */
    private String userId;
    
    public Product() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
