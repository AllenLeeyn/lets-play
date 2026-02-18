package com.example.lets_play.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.lets_play.dto.AuthResponse;
import com.example.lets_play.dto.SigninRequest;
import com.example.lets_play.dto.SignupRequest;
import com.example.lets_play.model.User;
import com.example.lets_play.repository.UserRepository;

/**
 * Signup and signin. Signup creates a user with role USER and returns a JWT; signin validates credentials and returns a JWT.
 * <p>
 * Setup: none; uses {@link JwtService} and {@link org.springframework.security.crypto.password.PasswordEncoder}.
 */
@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

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
        user.setRole(User.Role.USER);

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
