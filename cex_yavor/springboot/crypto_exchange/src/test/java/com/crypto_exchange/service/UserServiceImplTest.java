package com.crypto_exchange.service;

import com.crypto_exchange.entity.User;
import com.crypto_exchange.exception.ResourceNotFoundException;
import com.crypto_exchange.exception.UnauthorizedException;
import com.crypto_exchange.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("UserServiceImpl business logic tests")
class UserServiceImplTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Nested
    @DisplayName("Delete account logic")
    class DeleteAccount {
        @Test
        @DisplayName("Should delete account if password is correct")
        void deleteAccountSuccess() {
            User user = User.builder().email("alice").password("hashed").build();
            when(userRepository.findByEmail("alice")).thenReturn(Optional.of(user));
            when(passwordEncoder.matches("secret", "hashed")).thenReturn(true);
            doNothing().when(userRepository).delete(user);
            assertDoesNotThrow(() -> userService.deleteAccount("alice", "secret"));
        }
        @Test
        @DisplayName("Should fail if password is wrong")
        void deleteAccountWrongPassword() {
            User user = User.builder().email("alice").password("hashed").build();
            when(userRepository.findByEmail("alice")).thenReturn(Optional.of(user));
            when(passwordEncoder.matches("wrong", "hashed")).thenReturn(false);
            assertThrows(UnauthorizedException.class, () -> userService.deleteAccount("alice", "wrong"));
        }
    }

    @Nested
    @DisplayName("Edit username logic")
    class EditUsername {
        @Test
        @DisplayName("Should edit username if user exists")
        void editUsernameSuccess() {
            User user = User.builder().email("alice").firstName("Alice").lastName("Old").build();
            when(userRepository.findByEmail("alice")).thenReturn(Optional.of(user));
            when(userRepository.save(any(User.class))).thenReturn(user);
            assertTrue(userService.editUsername("alice", "Bob", "Smith"));
        }
        @Test
        @DisplayName("Should fail if user does not exist")
        void editUsernameNotFound() {
            when(userRepository.findByEmail("dave")).thenReturn(Optional.empty());
            assertThrows(ResourceNotFoundException.class, () -> userService.editUsername("dave", "Davo", "Smith"));
        }
    }
} 