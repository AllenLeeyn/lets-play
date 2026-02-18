package com.example.lets_play.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.lets_play.dto.ProductCreateRequest;
import com.example.lets_play.dto.ProductResponse;
import com.example.lets_play.dto.ProductUpdateRequest;
import com.example.lets_play.dto.ProductsResponse;
import com.example.lets_play.model.Product;
import com.example.lets_play.model.User;
import com.example.lets_play.repository.ProductRepository;

@Service
public class ProductService {

    private static final String OBJECT_ID_PATTERN = "^[a-fA-F0-9]{24}$";
    private static final int MAX_PAGE_SIZE = 100;

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    /**
     * List products with optional owner filter and pagination.
     * Public access. page is 0-based; size is clamped to 1–100.
     */
    public ProductsResponse listProducts(String userId, int page, int size) {
        if (userId != null && !userId.isBlank()) {
            validateObjectId(userId);
        }
        int safeSize = Math.min(MAX_PAGE_SIZE, Math.max(1, size));
        Pageable pageable = PageRequest.of(Math.max(0, page), safeSize);

        Page<Product> productPage = userId != null && !userId.isBlank()
                ? productRepository.findByUserId(userId.trim(), pageable)
                : productRepository.findAll(pageable);

        List<ProductResponse> content = productPage.getContent().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return new ProductsResponse(
                content,
                productPage.getTotalElements(),
                productPage.getTotalPages(),
                productPage.getSize(),
                productPage.getNumber());
    }

    /**
     * Create a product owned by the current user. Authenticated users only.
     */
    public ProductResponse createProduct(ProductCreateRequest request, User currentUser) {
        Product product = new Product();
        product.setName(request.getName().trim());
        product.setDescription(request.getDescription() != null ? request.getDescription().trim() : null);
        product.setPrice(request.getPrice());
        product.setQuantity(request.getQuantity());
        product.setUserId(currentUser.getId());

        product = productRepository.save(product);
        return toResponse(product);
    }

    /**
     * Get product by id. Public access. Returns 404 if not found or invalid id.
     */
    public ProductResponse getProductById(String id) {
        validateProductId(id);
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));
        return toResponse(product);
    }

    /**
     * Update product. Product owner or admin only. At least one field required.
     */
    public ProductResponse updateProduct(String id, ProductUpdateRequest request, User currentUser) {
        validateProductId(id);
        if (!request.hasAnyField()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "At least one field must be provided");
        }
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));
        ensureOwnerOrAdmin(product, currentUser);

        if (request.getName() != null) {
            product.setName(request.getName().trim());
        }
        if (request.getDescription() != null) {
            product.setDescription(request.getDescription().trim());
        }
        if (request.getPrice() != null) {
            product.setPrice(request.getPrice());
        }
        if (request.getQuantity() != null) {
            product.setQuantity(request.getQuantity());
        }

        product = productRepository.save(product);
        return toResponse(product);
    }

    /**
     * Delete product. Product owner or admin only.
     */
    public void deleteProduct(String id, User currentUser) {
        validateProductId(id);
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));
        ensureOwnerOrAdmin(product, currentUser);
        productRepository.deleteById(id);
    }

    /**
     * Delete all products owned by the given user. Used when cascading from user deletion.
     */
    public void deleteByUserId(String userId) {
        productRepository.deleteByUserId(userId);
    }

    private void validateProductId(String id) {
        if (id == null || !id.matches(OBJECT_ID_PATTERN)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Invalid product ID format");
        }
    }

    private void validateObjectId(String id) {
        if (id == null || !id.matches(OBJECT_ID_PATTERN)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid user ID format");
        }
    }

    private void ensureOwnerOrAdmin(Product product, User currentUser) {
        boolean isOwner = product.getUserId() != null && product.getUserId().equals(currentUser.getId());
        boolean isAdmin = currentUser.getRole() == User.Role.ADMIN;
        if (!isOwner && !isAdmin) {
            throw new AccessDeniedException("Forbidden");
        }
    }

    private ProductResponse toResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getQuantity(),
                product.getUserId());
    }
}
