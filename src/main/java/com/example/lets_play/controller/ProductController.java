package com.example.lets_play.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.lets_play.dto.ProductCreateRequest;
import com.example.lets_play.dto.ProductResponse;
import com.example.lets_play.dto.ProductUpdateRequest;
import com.example.lets_play.dto.ProductsResponse;
import com.example.lets_play.model.User;
import com.example.lets_play.service.ProductService;
import com.example.lets_play.service.SecurityService;

import jakarta.validation.Valid;

/**
 * Product catalog and management.
 * <ul>
 *   <li>GET /products, GET /products/{id}: public.</li>
 *   <li>POST /products: USER role only; product is owned by current user.</li>
 *   <li>PUT /products/{id}, DELETE /products/{id}: authenticated; owner or admin only.</li>
 * </ul>
 * Setup: none.
 */
@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;
    
    @Autowired
    private SecurityService securityService;

    /** List products with optional userId filter and pagination (page, size). Public. */
    @GetMapping
    public ProductsResponse listProducts(
            @RequestParam(required = false) String userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return productService.listProducts(userId, page, size);
    }

    /** Create a product owned by the current user. USER role only. Returns 201. */
    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody ProductCreateRequest request) {
        User currentUser = securityService.getCurrentUserOrThrow();
        ProductResponse response = productService.createProduct(request, currentUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /** Get product by id. Public. */
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable String id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    /** Update product. Owner or admin only; at least one field required. */
    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable String id,
            @Valid @RequestBody ProductUpdateRequest request) {
        User currentUser = securityService.getCurrentUserOrThrow();
        return ResponseEntity.ok(productService.updateProduct(id, request, currentUser));
    }

    /** Delete product. Owner or admin only. Returns 204. */
    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteProduct(@PathVariable String id) {
        User currentUser = securityService.getCurrentUserOrThrow();
        productService.deleteProduct(id, currentUser);
        return ResponseEntity.noContent().build();
    }
}
