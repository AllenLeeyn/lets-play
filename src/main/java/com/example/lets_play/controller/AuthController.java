package com.example.lets_play.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.lets_play.dto.AuthResponse;
import com.example.lets_play.dto.SigninRequest;
import com.example.lets_play.dto.SignupRequest;
import com.example.lets_play.service.AuthService;

import jakarta.validation.Valid;

/**
 * Auth endpoints: signup and signin. All are public (permitAll in SecurityConfig). Return JWT in body; client sends as Bearer token.
 * <p>
 * Setup: none.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /** Register a new user (role USER). Returns 201 with JWT; 409 if email already registered. */
    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> signup(@Valid @RequestBody SignupRequest request) {
        AuthResponse response = authService.signup(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /** Authenticate by email/password. Returns 200 with JWT; 401 if invalid. */
    @PostMapping("/signin")
    public ResponseEntity<AuthResponse> signin(@Valid @RequestBody SigninRequest request) {
        AuthResponse response = authService.signin(request);
        return ResponseEntity.ok(response);
    }
}
