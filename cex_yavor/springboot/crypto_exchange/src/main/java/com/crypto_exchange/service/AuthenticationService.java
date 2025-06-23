package com.crypto_exchange.service;

import com.crypto_exchange.dto.AuthenticationRequest;
import com.crypto_exchange.dto.AuthenticationResponse;
import com.crypto_exchange.dto.RegisterRequest;
import com.crypto_exchange.entity.User;
import com.crypto_exchange.entity.enums.Role;
import com.crypto_exchange.exception.BadRequestException;
import com.crypto_exchange.exception.ResourceNotFoundException;
import com.crypto_exchange.repository.UserRepository;
import com.crypto_exchange.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationResponse register(RegisterRequest request) {
        logger.info("Attempting to register new user with email: {}", request.getEmail());
        
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            logger.warn("Registration failed: Email {} already registered", request.getEmail());
            throw new BadRequestException("Email already registered");
        }

        var user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role("USER")
                .build();
        userRepository.save(user);
        logger.info("Successfully registered new user with email: {}", request.getEmail());
        
        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    public AuthenticationResponse registerAdmin(RegisterRequest request) {
        logger.info("Attempting to register new admin with email: {}", request.getEmail());
        
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            logger.warn("Admin registration failed: Email {} already registered", request.getEmail());
            throw new BadRequestException("Email already registered");
        }

        var user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role("ADMIN")
                .build();
        userRepository.save(user);
        logger.info("Successfully registered new admin with email: {}", request.getEmail());
        
        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        logger.info("Attempting to authenticate user with email: {}", request.getEmail());
        
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );
        } catch (BadCredentialsException e) {
            logger.warn("Authentication failed for email {}: Invalid credentials", request.getEmail());
            throw new BadRequestException("Invalid email or password");
        }

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> {
                    logger.error("User not found with email: {}", request.getEmail());
                    return new ResourceNotFoundException("User not found with email: " + request.getEmail());
                });
        
        String jwtToken = jwtService.generateToken(user);
        logger.info("Successfully authenticated user with email: {}", request.getEmail());
        
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }
} 