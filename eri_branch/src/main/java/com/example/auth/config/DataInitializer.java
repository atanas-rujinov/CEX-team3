package com.example.auth.config;

import com.example.auth.domain.User;
import com.example.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Create a test user if no users exist
        if (userRepository.count() == 0) {
            User testUser = User.builder()
                    .username("testuser")
                    .passwordHash(passwordEncoder.encode("password123"))
                    .firstName("Test")
                    .lastName("User")
                    .email("test@example.com")
                    .build();
            
            userRepository.save(testUser);
            log.info("Created test user: testuser with password: password123");
        }
    }
} 