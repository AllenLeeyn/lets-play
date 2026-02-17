package com.example.lets_play.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public class ProductCreateRequest {

    @NotBlank(message = "Name is required")
    @Size(min = 1, max = 255)
    private String name;

    @Size(max = 2000)
    private String description;
    
    @PositiveOrZero(message = "Price must be greater than or equal to 0")
    private double price;

    @PositiveOrZero(message = "Quantity must be greater than or equal to 0")
    private int quantity;

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
}
