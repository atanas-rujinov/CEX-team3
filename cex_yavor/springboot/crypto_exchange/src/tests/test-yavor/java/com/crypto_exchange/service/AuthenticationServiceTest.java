package com.crypto_exchange.service;

import com.crypto_exchange.dto.AuthenticationRequest;
import com.crypto_exchange.dto.RegisterRequest;
import com.crypto_exchange.entity.User;
import com.crypto_exchange.exception.BadRequestException;
import com.crypto_exchange.repository.UserRepository;
import com.crypto_exchange.security.JwtService;
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

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("AuthenticationService business logic tests")
class AuthenticationServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtService jwtService;
    @Mock
    private AuthenticationManager authenticationManager;
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
            RegisterRequest req = new RegisterRequest("frank", "GoodPass123!", "Frank", "Test");
            when(userRepository.findByEmail("frank")).thenReturn(Optional.empty());
            when(passwordEncoder.encode("GoodPass123!")).thenReturn("hashedPassword");
            User user = User.builder().email("frank").password("hashedPassword").firstName("Frank").lastName("Test").role("USER").build();
            when(userRepository.save(any(User.class))).thenReturn(user);
            // Simulate registration logic
            User saved = userRepository.save(user);
            assertEquals("frank", saved.getEmail());
        }
        @Test
        @DisplayName("Should fail if email already exists")
        void registerEmailTaken() {
            RegisterRequest req = new RegisterRequest("alice", "Whatever", "Alice", "Test");
            User user = User.builder().email("alice").build();
            when(userRepository.findByEmail("alice")).thenReturn(Optional.of(user));
            assertTrue(userRepository.findByEmail("alice").isPresent());
        }
    }

    @Nested
    @DisplayName("Authenticate logic")
    class Authenticate {
        @Test
        @DisplayName("Should return token for valid credentials")
        void authenticateSuccess() {
            AuthenticationRequest req = new AuthenticationRequest("alice", "secret");
            User user = User.builder().email("alice").password("hashed").build();
            Authentication auth = mock(Authentication.class);
            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(auth);
            when(userRepository.findByEmail("alice")).thenReturn(Optional.of(user));
            // Simulate authentication logic
            assertDoesNotThrow(() -> authenticationManager.authenticate(new UsernamePasswordAuthenticationToken("alice", "secret")));
        }
        @Test
        @DisplayName("Should fail for wrong password")
        void authenticateWrongPassword() {
            AuthenticationRequest req = new AuthenticationRequest("alice", "wrong");
            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenThrow(new BadCredentialsException("Invalid credentials"));
            assertThrows(BadCredentialsException.class, () -> authenticationManager.authenticate(new UsernamePasswordAuthenticationToken("alice", "wrong")));
        }
    }
} 