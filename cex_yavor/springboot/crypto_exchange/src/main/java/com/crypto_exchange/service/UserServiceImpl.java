package com.crypto_exchange.service;

import com.crypto_exchange.entity.User;
import com.crypto_exchange.exception.BadRequestException;
import com.crypto_exchange.exception.ResourceNotFoundException;
import com.crypto_exchange.exception.UnauthorizedException;
import com.crypto_exchange.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public boolean deleteAccount(String username, String password) {
        logger.info("Attempting to delete account for username: {}", username);
        
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    logger.error("Delete account failed: User not found with username: {}", username);
                    return new ResourceNotFoundException("User not found with username: " + username);
                });

        if (!passwordEncoder.matches(password, user.getPassword())) {
            logger.warn("Delete account failed for username {}: Invalid password", username);
            throw new UnauthorizedException("Invalid password");
        }

        userRepository.delete(user);
        logger.info("Successfully deleted account for username: {}", username);
        return true;
    }

    @Override
    public boolean editUsername(String currentUsername, String newUsername) {
        logger.info("Attempting to edit username from {} to {}", currentUsername, newUsername);
        
        User user = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> {
                    logger.error("Edit username failed: User not found with username: {}", currentUsername);
                    return new ResourceNotFoundException("User not found with username: " + currentUsername);
                });

        if (userRepository.findByUsername(newUsername).isPresent()) {
            logger.warn("Edit username failed: Username {} already taken", newUsername);
            throw new BadRequestException("Username already taken");
        }

        user.setUsername(newUsername);
        userRepository.save(user);
        logger.info("Successfully updated username from {} to {}", currentUsername, newUsername);
        return true;
    }

    @Override
    public boolean banAccount(String username) {
        logger.info("Attempting to ban account for username: {}", username);
        
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    logger.error("Ban account failed: User not found with username: {}", username);
                    return new ResourceNotFoundException("User not found with username: " + username);
                });

        if (user.isBanned()) {
            logger.warn("Ban account failed: User {} is already banned", username);
            throw new BadRequestException("User is already banned");
        }

        user.setBanned(true);
        userRepository.save(user);
        logger.info("Successfully banned account for username: {}", username);
        return true;
    }
} 