package com.example.lets_play.controller;

import java.util.List;

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
import org.springframework.web.bind.annotation.RestController;

import com.example.lets_play.dto.UserCreateRequest;
import com.example.lets_play.dto.UserResponse;
import com.example.lets_play.dto.UserUpdateRequest;
import com.example.lets_play.service.SecurityService;
import com.example.lets_play.service.UserService;

import jakarta.validation.Valid;

/**
 * User management endpoints. All require authentication.
 * - GET /users, POST /users: admin only.
 * - GET /users/{id}, PUT /users/{id}, DELETE /users/{id}: admin or account owner only.
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final SecurityService securityService;

    public UserController(UserService userService, SecurityService securityService) {
        this.userService = userService;
        this.securityService = securityService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<UserResponse> listUsers() {
        return userService.listUsers();
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserCreateRequest request) {
        UserResponse response = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @securityService.isCurrentUser(#id)")
    public ResponseEntity<UserResponse> getUserById(@PathVariable String id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @securityService.isCurrentUser(#id)")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable String id,
            @Valid @RequestBody UserUpdateRequest request) {
        return ResponseEntity.ok(userService.updateUser(id, request, securityService.isCurrentUser(id)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @securityService.isCurrentUser(#id)")
    public ResponseEntity<Void> deleteUser(@PathVariable String id) {
        userService.deleteUser(id, securityService.isCurrentUser(id));
        return ResponseEntity.noContent().build();
    }
}
