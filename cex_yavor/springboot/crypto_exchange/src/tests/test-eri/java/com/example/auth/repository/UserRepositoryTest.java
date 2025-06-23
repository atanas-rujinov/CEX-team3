package com.example.auth.repository;

import com.example.auth.domain.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@DisplayName("UserRepository JPA tests")
class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("Should find user by username if exists")
    void findByUsernameExists() {
        User user = User.builder()
                .username("alice")
                .passwordHash("hashed")
                .firstName("Alice")
                .lastName("Test")
                .build();
        userRepository.save(user);
        Optional<User> found = userRepository.findByUsername("alice");
        assertTrue(found.isPresent());
        assertEquals("alice", found.get().getUsername());
    }

    @Test
    @DisplayName("Should return empty if username does not exist")
    void findByUsernameNotExists() {
        Optional<User> user = userRepository.findByUsername("nonexistent");
        assertTrue(user.isEmpty());
    }
} 