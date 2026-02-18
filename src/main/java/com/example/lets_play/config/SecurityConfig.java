package com.example.lets_play.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.example.lets_play.filter.JwtAuthenticationFilter;

/**
 * Configures HTTP security and request authorization.
 * <ul>
 *   <li>Uses JWT for authentication (no sessions).</li>
 *   <li>Public: signin, signup, GET products (list and by id), OPTIONS.</li>
 *   <li>All other /api/** require a valid JWT; method-level rules (e.g. admin-only) use {@code @PreAuthorize}.</li>
 * </ul>
 * Setup: ensure {@link JwtAuthenticationFilter} is registered so the JWT is validated and the security context is set.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * Defines the security filter chain: CORS (from CorsConfig), no CSRF (stateless API),
     * stateless sessions, public vs authenticated paths, and JWT filter before Spring Security's username/password filter.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> {})
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/signin", "/api/auth/signup").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/products", "/api/products/*").permitAll()
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .requestMatchers("/api/**").authenticated()
                .anyRequest().denyAll())
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
