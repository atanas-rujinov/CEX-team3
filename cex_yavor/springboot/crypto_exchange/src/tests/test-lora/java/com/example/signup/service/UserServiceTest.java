package com.example.signup.service;

import com.example.signup.User;
import com.example.signup.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("UserService business logic tests")
class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private BCryptPasswordEncoder passwordEncoder;
    @InjectMocks
    private UserService userService;

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
            User user = new User();
            user.setEmail("alice@example.com");
            user.setPassword("hashed");
            when(userRepository.findByEmail("alice@example.com")).thenReturn(Optional.of(user));
            when(passwordEncoder.matches("secret", "hashed")).thenReturn(true);
            doNothing().when(userRepository).delete(user);
            assertDoesNotThrow(() -> userService.deleteAccount("alice@example.com", "secret"));
        }
        @Test
        @DisplayName("Should fail if password is wrong")
        void deleteAccountWrongPassword() {
            User user = new User();
            user.setEmail("alice@example.com");
            user.setPassword("hashed");
            when(userRepository.findByEmail("alice@example.com")).thenReturn(Optional.of(user));
            when(passwordEncoder.matches("wrong", "hashed")).thenReturn(false);
            assertThrows(RuntimeException.class, () -> userService.deleteAccount("alice@example.com", "wrong"));
        }
        @Test
        @DisplayName("Should fail if email is missing")
        void deleteAccountMissingEmail() {
            assertThrows(RuntimeException.class, () -> userService.deleteAccount(null, "secret"));
        }
        @Test
        @DisplayName("Should fail if password is missing")
        void deleteAccountMissingPassword() {
            assertThrows(RuntimeException.class, () -> userService.deleteAccount("alice@example.com", null));
        }
    }

    @Nested
    @DisplayName("Edit username logic")
    class EditUsername {
        @Test
        @DisplayName("Should edit username if user exists")
        void editUsernameSuccess() {
            User user = new User();
            user.setEmail("alice@example.com");
            user.setFirstName("Alice");
            user.setLastName("Old");
            when(userRepository.findByEmail("alice@example.com")).thenReturn(Optional.of(user));
            when(userRepository.save(any(User.class))).thenReturn(user);
            assertTrue(userService.editUsername("alice@example.com", "Bob", "Smith"));
        }
        @Test
        @DisplayName("Should fail if user does not exist")
        void editUsernameNotFound() {
            when(userRepository.findByEmail("dave@example.com")).thenReturn(Optional.empty());
            assertThrows(RuntimeException.class, () -> userService.editUsername("dave@example.com", "Davo", "Smith"));
        }
        @Test
        @DisplayName("Should fail if new first name is missing")
        void editUsernameMissingFirstName() {
            assertThrows(RuntimeException.class, () -> userService.editUsername("alice@example.com", null, "Smith"));
        }
        @Test
        @DisplayName("Should fail if new last name is missing")
        void editUsernameMissingLastName() {
            assertThrows(RuntimeException.class, () -> userService.editUsername("alice@example.com", "Bob", null));
        }
    }
} 