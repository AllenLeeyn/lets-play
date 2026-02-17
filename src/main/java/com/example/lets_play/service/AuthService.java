package com.example.lets_play.service;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.lets_play.dto.AuthResponse;
import com.example.lets_play.dto.SigninRequest;
import com.example.lets_play.dto.SignupRequest;
import com.example.lets_play.model.User;
import com.example.lets_play.repository.UserRepository;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, JwtService jwtService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Register a new user. Encodes password and issues a JWT.
     *
     * @throws IllegalStateException if email is already registered
     */
    public AuthResponse signup(SignupRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalStateException("Email already registered");
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail().trim().toLowerCase());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(User.Role.USER.name());

        user = userRepository.save(user);
        String token = jwtService.generateToken(user);
        return new AuthResponse(token);
    }

    /**
     * Authenticate by email and password. Returns JWT on success.
     *
     * @throws BadCredentialsException if email or password is invalid
     */
    public AuthResponse signin(SigninRequest request) {
        User user = userRepository.findByEmail(request.getEmail().trim().toLowerCase())
                .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid email or password");
        }

        String token = jwtService.generateToken(user);
        return new AuthResponse(token);
    }
}
