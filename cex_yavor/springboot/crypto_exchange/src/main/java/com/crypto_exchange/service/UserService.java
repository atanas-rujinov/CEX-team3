package com.crypto_exchange.service;

import java.math.BigDecimal;

public interface UserService {
    boolean deleteAccount(String email, String password);
    boolean editUsername(String email, String newFirstName, String newLastName);
    boolean editPassword(String email, String currentPassword, String newPassword);
    boolean banAccount(String email);
    boolean updateBalance(String email, BigDecimal newBalance);
} 