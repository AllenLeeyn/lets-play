package com.example.lets_play.service;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.lets_play.model.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

/**
 * JWT creation and verification. Tokens carry subject (user id), email, role, and expiration.
 * <p>
 * Setup: set {@code jwt.secret} (min 256 bits / 32 bytes for HS256) and {@code jwt.expiration-ms} (e.g. 86400000 for 24h).
 */
@Service
public class JwtService {

    private final SecretKey key;
    private final long expirationMs;

    public JwtService(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration-ms}") long expirationMs) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMs = expirationMs;
    }

    /** Builds a signed JWT with subject=user.id, claims email and role, and expiration. */
    public String generateToken(User user) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expirationMs);

        return Jwts.builder()
        .setSubject(user.getId())
        .claim("email", user.getEmail())
        .claim("role", user.getRole().name())
        .setIssuedAt(now)
        .setExpiration(expiry)
        .signWith(key, SignatureAlgorithm.HS256)
        .compact();
    }

    /** Returns the subject (user id) from the token; throws if token invalid or expired. */
    public String extractUserId(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /** Parses and verifies the token; returns claims body. Throws on invalid or expired token. */
    public Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)   // verifies signature, throws if invalid/expired
                .getBody();
    }

    /** Extracts a single claim using the given resolver (e.g. Claims::getSubject). */
    public <T> T extractClaim(String token, Function<Claims, T> resolver) {
        Claims claims = getClaims(token);
        return resolver.apply(claims);
    }
}
