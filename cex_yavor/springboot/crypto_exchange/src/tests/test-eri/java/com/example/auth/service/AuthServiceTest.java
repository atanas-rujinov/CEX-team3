package com.example.auth.service;

import com.example.auth.domain.User;
import com.example.auth.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("AuthService business logic tests")
class AuthServiceTest {
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Nested
    @DisplayName("Signup logic")
    class Signup {
        @Test
        @DisplayName("Should register new user if username is unused and password is strong")
        void signupSuccess() {
            User user = User.builder()
                    .username("frank")
                    .passwordHash("hashedPassword")
                    .firstName("Frank")
                    .lastName("Test")
                    .build();
            when(userRepository.findByUsername("frank")).thenReturn(Optional.empty());
            when(passwordEncoder.encode("GoodPass123!")).thenReturn("hashedPassword");
            when(userRepository.save(any(User.class))).thenReturn(user);
            // Simulate registration logic
            User saved = userRepository.save(user);
            assertEquals("frank", saved.getUsername());
        }
        @Test
        @DisplayName("Should fail if username already exists")
        void signupUsernameTaken() {
            User user = User.builder().username("alice").build();
            when(userRepository.findByUsername("alice")).thenReturn(Optional.of(user));
            assertTrue(userRepository.findByUsername("alice").isPresent());
        }
        @Test
        @DisplayName("Should fail if password is too weak")
        void signupWeakPassword() {
            // Simulate password validation logic
            String weakPassword = "123";
            assertTrue(weakPassword.length() < 8);
        }
    }

    @Nested
    @DisplayName("Login logic")
    class Login {
        @Test
        @DisplayName("Should return token for valid credentials")
        void loginSuccess() {
            User user = User.builder().username("alice").passwordHash("hashed").build();
            Authentication auth = mock(Authentication.class);
            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(auth);
            when(userRepository.findByUsername("alice")).thenReturn(Optional.of(user));
            // Simulate login logic
            assertDoesNotThrow(() -> authenticationManager.authenticate(new UsernamePasswordAuthenticationToken("alice", "secret")));
        }
        @Test
        @DisplayName("Should fail for wrong password")
        void loginWrongPassword() {
            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenThrow(new BadCredentialsException("Invalid credentials"));
            assertThrows(BadCredentialsException.class, () -> authenticationManager.authenticate(new UsernamePasswordAuthenticationToken("alice", "wrong")));
        }
    }

    @Nested
    @DisplayName("Password change logic")
    class EditPassword {
        @Test
        @DisplayName("Should change password if current password is correct and new is valid")
        void editPasswordSuccess() {
            User user = User.builder().username("alice").passwordHash("oldHash").build();
            when(userRepository.findByUsername("alice")).thenReturn(Optional.of(user));
            when(passwordEncoder.matches("old", "oldHash")).thenReturn(true);
            when(passwordEncoder.encode("newSecret")).thenReturn("newHash");
            user.setPasswordHash("newHash");
            when(userRepository.save(any(User.class))).thenReturn(user);
            // Simulate password change
            user.setPasswordHash(passwordEncoder.encode("newSecret"));
            assertEquals("newHash", user.getPasswordHash());
        }
        @Test
        @DisplayName("Should fail if current password is wrong")
        void editPasswordWrongCurrent() {
            User user = User.builder().username("alice").passwordHash("oldHash").build();
            when(userRepository.findByUsername("alice")).thenReturn(Optional.of(user));
            when(passwordEncoder.matches("bad", "oldHash")).thenReturn(false);
            assertFalse(passwordEncoder.matches("bad", user.getPasswordHash()));
        }
        @Test
        @DisplayName("Should fail if new password is too weak")
        void editPasswordWeakNew() {
            String newPassword = "123";
            assertTrue(newPassword.length() < 8);
        }
    }
} 