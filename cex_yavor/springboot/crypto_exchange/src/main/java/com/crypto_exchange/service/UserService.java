package com.crypto_exchange.service;

public interface UserService {
    boolean deleteAccount(String username, String password);
    boolean editUsername(String currentUsername, String newUsername);
    boolean banAccount(String username);
} 