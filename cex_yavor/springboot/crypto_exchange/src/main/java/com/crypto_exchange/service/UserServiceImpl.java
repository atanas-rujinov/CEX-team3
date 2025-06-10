package com.crypto_exchange.service;

import com.crypto_exchange.entity.User;
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
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (passwordEncoder.matches(password, user.getPassword())) {
                userRepository.delete(user);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean editUsername(String currentUsername, String newUsername) {
        Optional<User> existingUserOpt = userRepository.findByUsername(currentUsername);
        if (existingUserOpt.isPresent() && userRepository.findByUsername(newUsername).isEmpty()) {
            User user = existingUserOpt.get();
            user.setUsername(newUsername);
            userRepository.save(user);
            return true;
        }
        return false;
    }

    @Override
    public boolean banAccount(String username) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setBanned(true);
            userRepository.save(user);
            return true;
        }
        return false;
    }
} 