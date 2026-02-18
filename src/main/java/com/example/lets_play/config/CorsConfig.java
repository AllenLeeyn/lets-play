package com.example.lets_play.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Configures Cross-Origin Resource Sharing (CORS) for the API.
 * Allows browser clients on other origins (e.g. Swagger UI, frontend on another port) to call
 * /api/** with credentials. For production, replace {@code allowedOriginPatterns("*")} with a list
 * of specific origins (e.g. your frontend URL).
 */
@Configuration
public class CorsConfig {

    /**
     * Registers CORS for /api/**: any origin, common HTTP methods, all headers, credentials allowed.
     * Used by SecurityConfig via {@code .cors(cors -> {})}.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(List.of("*"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", config);
        return source;
    }
}
