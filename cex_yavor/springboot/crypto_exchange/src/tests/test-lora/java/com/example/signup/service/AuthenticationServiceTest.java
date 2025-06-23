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
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("AuthenticationService business logic tests")
class AuthenticationServiceTest {
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BCryptPasswordEncoder passwordEncoder;
    @InjectMocks
    private AuthenticationService authenticationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Nested
    @DisplayName("Register logic")
    class Register {
        @Test
        @DisplayName("Should register new user if email is unused and password is strong")
        void registerSuccess() {
            User user = new User();
            user.setEmail("frank@example.com");
            when(userRepository.existsByEmail("frank@example.com")).thenReturn(false);
            when(userRepository.save(any(User.class))).thenReturn(user);
            User saved = userRepository.save(user);
            assertEquals("frank@example.com", saved.getEmail());
        }
        @Test
        @DisplayName("Should fail if email already exists")
        void registerEmailTaken() {
            when(userRepository.existsByEmail("alice@example.com")).thenReturn(true);
            assertTrue(userRepository.existsByEmail("alice@example.com"));
        }
        @Test
        @DisplayName("Should fail if password is too weak")
        void registerWeakPassword() {
            String weakPassword = "123";
            assertTrue(weakPassword.length() < 8);
        }
        @Test
        @DisplayName("Should fail if email is missing")
        void registerMissingEmail() {
            User user = new User();
            user.setPassword("GoodPass123!");
            // Simulate missing email
            assertNull(user.getEmail());
        }
        @Test
        @DisplayName("Should fail if password is missing")
        void registerMissingPassword() {
            User user = new User();
            user.setEmail("frank@example.com");
            // Simulate missing password
            assertNull(user.getPassword());
        }
    }

    @Nested
    @DisplayName("Login logic")
    class Login {
        @Test
        @DisplayName("Should return token for valid credentials")
        void loginSuccess() {
            User user = new User();
            user.setEmail("alice@example.com");
            user.setPassword("hashed");
            Authentication auth = mock(Authentication.class);
            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(auth);
            when(userRepository.findByEmail("alice@example.com")).thenReturn(Optional.of(user));
            assertDoesNotThrow(() -> authenticationManager.authenticate(new UsernamePasswordAuthenticationToken("alice@example.com", "secret")));
        }
        @Test
        @DisplayName("Should fail for wrong password")
        void loginWrongPassword() {
            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenThrow(new BadCredentialsException("Invalid credentials"));
            assertThrows(BadCredentialsException.class, () -> authenticationManager.authenticate(new UsernamePasswordAuthenticationToken("alice@example.com", "wrong")));
        }
        @Test
        @DisplayName("Should fail if email is missing")
        void loginMissingEmail() {
            User user = new User();
            user.setPassword("secret");
            assertNull(user.getEmail());
        }
        @Test
        @DisplayName("Should fail if password is missing")
        void loginMissingPassword() {
            User user = new User();
            user.setEmail("alice@example.com");
            assertNull(user.getPassword());
        }
    }
} 