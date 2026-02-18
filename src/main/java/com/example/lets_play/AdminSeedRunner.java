package com.example.lets_play;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import com.example.lets_play.model.User;
import com.example.lets_play.repository.UserRepository;

/**
 * Seeds a default ADMIN user on application startup if one does not already exist.
 * The created user is identified by {@code admin.seed.email}; this same email is used elsewhere
 * (e.g. UserService) to protect the "default admin" from role change and deletion.
 * <p>
 * Setup: set these properties (e.g. in application.yml or env):
 * <ul>
 *   <li>{@code admin.seed.email} – email for the default admin (required)</li>
 *   <li>{@code admin.seed.name} – display name</li>
 *   <li>{@code admin.seed.password} – plain password; stored hashed. Change after first login in non-dev.</li>
 * </ul>
 */
@Component
public class AdminSeedRunner implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(AdminSeedRunner.class);

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Value("${admin.seed.email}")
    private String adminEmail;

    @Value("${admin.seed.name}")
    private String adminName;

    @Value("${admin.seed.password}")
    private String adminPassword;

    /**
     * Runs after the application context is ready. If no user exists with {@code admin.seed.email},
     * creates an ADMIN user with the configured name and hashed password; otherwise logs and exits.
     * Idempotent; on duplicate key (e.g. race with another instance) logs an error.
     */
    @Override
    public void run(ApplicationArguments args) {
        if (userRepository.existsByEmail(adminEmail)) {
            log.info("Default admin already exists (email: {});", adminEmail);
            return;
        }

        User admin = new User();
        admin.setEmail(adminEmail);
        admin.setName(adminName);
        admin.setRole(User.Role.ADMIN);
        admin.setPassword(passwordEncoder.encode(adminPassword));

        try {
            userRepository.save(admin);
            log.info("Default admin created (email: {});", adminEmail);
        } catch (DuplicateKeyException e) {
            log.error("Failed to create default admin (email: {}): {}", adminEmail, e.getMessage());
        }

        log.warn("Seeded DEFAULT ADMIN user (email={}). CHANGE THE PASSWORD immediately. " +
                 "Set env ADMIN_PASSWORD (and rotate the stored password) for non-dev environments.",
                 adminEmail);
    }
}
