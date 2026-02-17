package com.example.lets_play.dto;

import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public class ProductUpdateRequest {

    @Size(min = 1, max = 255)
    private String name;

    @Size(max = 2000)
    private String description;

    @PositiveOrZero(message = "Price must be greater than or equal to 0")
    private Double price;

    @PositiveOrZero(message = "Quantity must be greater than or equal to 0")
    private Integer quantity;

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

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    /** True if at least one field was provided (for PUT "at least one required"). */
    public boolean hasAnyField() {
        return name != null || description != null || price != null || quantity != null;
    }
}
