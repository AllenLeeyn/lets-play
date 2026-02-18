package com.example.lets_play;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import com.example.lets_play.model.User;
import com.example.lets_play.repository.UserRepository;

@Component
public class AdminSeedRunner implements  ApplicationRunner{

    private static final Logger log = LoggerFactory.getLogger(AdminSeedRunner.class);

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Value("${admin.seed.email}")
    private String adminEmail;

    @Value("${admin.seed.name}")
    private String adminName;

    @Value("${admin.seed.password}")
    private String adminPassword;
    
    public AdminSeedRunner(UserRepository userRepository,
                           BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

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
