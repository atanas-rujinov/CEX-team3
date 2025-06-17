package com.crypto_exchange.service;

import com.crypto_exchange.entity.User;
import com.crypto_exchange.exception.BadRequestException;
import com.crypto_exchange.exception.ResourceNotFoundException;
import com.crypto_exchange.exception.UnauthorizedException;
import com.crypto_exchange.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public boolean deleteAccount(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new UnauthorizedException("Invalid password");
        }

        userRepository.delete(user);
        return true;
    }

    @Override
    public boolean editUsername(String currentUsername, String newUsername) {
        User user = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + currentUsername));

        if (userRepository.findByUsername(newUsername).isPresent()) {
            throw new BadRequestException("Username already taken");
        }

        user.setUsername(newUsername);
        userRepository.save(user);
        return true;
    }

    @Override
    public boolean banAccount(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));

        if (user.isBanned()) {
            throw new BadRequestException("User is already banned");
        }

        user.setBanned(true);
        userRepository.save(user);
        return true;
    }
} 