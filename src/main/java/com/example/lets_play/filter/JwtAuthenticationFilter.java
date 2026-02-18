package com.example.lets_play.filter;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.lets_play.service.SecurityService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Optional;

/**
 * Runs once per request and authenticates the caller using a JWT from the {@code Authorization: Bearer <token>} header.
 * If a valid token is present, the corresponding {@link org.springframework.security.core.Authentication} is set on
 * {@link SecurityContextHolder} so downstream filters and {@code @PreAuthorize} see the current user.
 * If there is no token or it is invalid, the context is left unauthenticated and Spring Security applies normal
 * authorization rules (e.g. permitAll vs authenticated).
 * <p>
 * Setup: this filter must run before Spring Security's username/password filter. Registration is done in
 * {@link com.example.lets_play.config.SecurityConfig#securityFilterChain} via {@code addFilterBefore}.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final SecurityService securityService;

    public JwtAuthenticationFilter(SecurityService securityService) {
        this.securityService = securityService;
    }

    /**
     * Extracts a Bearer token from the request; if present and valid, sets the authentication on the security context, then continues the chain.
     */
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        String token = extractBearerToken(request);

        if (token != null) {
            Optional<Authentication> authOpt = securityService.getAuthentication(token);

            if (authOpt.isPresent()) {
                SecurityContextHolder.getContext().setAuthentication(authOpt.get());
            }
        }

        filterChain.doFilter(request, response);
    }

    /** Returns the value after {@code "Bearer "} in the Authorization header, or null if missing or not Bearer. */
    private String extractBearerToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }
        return authHeader.substring("Bearer ".length()).trim();
    }
}
