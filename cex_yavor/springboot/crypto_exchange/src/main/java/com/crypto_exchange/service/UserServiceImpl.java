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

import java.math.BigDecimal;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public boolean deleteAccount(String email, String password) {
        logger.info("Attempting to delete account for email: {}", email);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.error("Delete account failed: User not found with email: {}", email);
                    return new ResourceNotFoundException("User not found with email: " + email);
                });
        if (!passwordEncoder.matches(password, user.getPassword())) {
            logger.warn("Delete account failed for email {}: Invalid password", email);
            throw new UnauthorizedException("Invalid password");
        }
        userRepository.delete(user);
        logger.info("Successfully deleted account for email: {}", email);
        return true;
    }

    @Override
    public boolean editUsername(String email, String newFirstName, String newLastName) {
        logger.info("Attempting to edit name for email: {}", email);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.error("Edit name failed: User not found with email: {}", email);
                    return new ResourceNotFoundException("User not found with email: " + email);
                });
        user.setFirstName(newFirstName);
        user.setLastName(newLastName);
        userRepository.save(user);
        logger.info("Successfully updated name for email: {}", email);
        return true;
    }

    @Override
    public boolean editPassword(String email, String currentPassword, String newPassword) {
        logger.info("Attempting to edit password for email: {}", email);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.error("Edit password failed: User not found with email: {}", email);
                    return new ResourceNotFoundException("User not found with email: " + email);
                });
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            logger.warn("Edit password failed for email {}: Invalid current password", email);
            throw new UnauthorizedException("Invalid current password");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        logger.info("Successfully updated password for email: {}", email);
        return true;
    }

    @Override
    public boolean banAccount(String email) {
        logger.info("Attempting to ban account for email: {}", email);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.error("Ban account failed: User not found with email: {}", email);
                    return new ResourceNotFoundException("User not found with email: " + email);
                });
        if (user.isBanned()) {
            logger.warn("Ban account failed: User {} is already banned", email);
            throw new BadRequestException("User is already banned");
        }
        user.setBanned(true);
        userRepository.save(user);
        logger.info("Successfully banned account for email: {}", email);
        return true;
    }

    @Override
    public boolean updateBalance(String email, BigDecimal newBalance) {
        logger.info("Attempting to update balance for email: {}", email);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.error("Update balance failed: User not found with email: {}", email);
                    return new ResourceNotFoundException("User not found with email: " + email);
                });
        user.setBalance(newBalance);
        userRepository.save(user);
        logger.info("Successfully updated balance for email: {}", email);
        return true;
    }
} 