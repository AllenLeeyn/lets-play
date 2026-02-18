package com.example.lets_play.service;

import java.util.List;
import java.util.Optional;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import com.example.lets_play.model.User;
import com.example.lets_play.repository.UserRepository;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;

@Service
public class SecurityService {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    public SecurityService(JwtService jwtService, UserRepository userRepository) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    public Optional<User> validateTokenAndLoadUser(String token) {
        try {
            Claims claims = jwtService.getClaims(token);

            String userId = claims.getSubject();
            if (userId == null) { return Optional.empty(); }

            Optional<User> userOpt = userRepository.findById(userId);
            if (userOpt.isEmpty()) { return Optional.empty(); }

            User user = userOpt.get();

            String tokenEmail = claims.get("email", String.class);
            String tokenRole = claims.get("role", String.class);

            String userRole = user.getRole().name();
            if (!user.getEmail().equals(tokenEmail) || !userRole.equals(tokenRole)) {
                return Optional.empty();
            }

            return Optional.of(user);
        } catch (JwtException| IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    public Optional<Authentication> getAuthentication(String token) {
        return validateTokenAndLoadUser(token)
            .map(user -> {
                String role = user.getRole().name();
                var authorities = List.of(new SimpleGrantedAuthority("ROLE_" + role));

                return new UsernamePasswordAuthenticationToken(
                    user, null, authorities);
            });
    }

    /**
     * Returns true if the current authenticated user's id equals the given userId.
     * Used for @PreAuthorize to allow admin or account owner only.
     */
    public boolean isCurrentUser(String userId) {
        if (userId == null) return false;
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!(principal instanceof User)) return false;
        return userId.equals(((User) principal).getId());
    }
}
