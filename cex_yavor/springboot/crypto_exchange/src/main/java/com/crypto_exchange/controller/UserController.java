package com.crypto_exchange.controller;

import com.crypto_exchange.dto.*;
import com.crypto_exchange.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteAccount(@Valid @RequestBody DeleteAccountRequest request) {
        userService.deleteAccount(request.getEmail(), request.getPassword());
        return ResponseEntity.ok("Account deleted successfully");
    }

    @PutMapping("/name")
    public ResponseEntity<String> editName(@Valid @RequestBody EditUsernameRequest request) {
        userService.editUsername(request.getEmail(), request.getNewFirstName(), request.getNewLastName());
        return ResponseEntity.ok("Name updated successfully");
    }

    @PutMapping("/password")
    public ResponseEntity<String> editPassword(@Valid @RequestBody EditPasswordRequest request) {
        userService.editPassword(request.getEmail(), request.getCurrentPassword(), request.getNewPassword());
        return ResponseEntity.ok("Password updated successfully");
    }

    @PutMapping("/ban")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> banAccount(@Valid @RequestBody BanAccountRequest request) {
        userService.banAccount(request.getEmail());
        return ResponseEntity.ok("Account banned successfully");
    }

    @PutMapping("/balance")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> updateBalance(@Valid @RequestBody UpdateBalanceRequest request) {
        userService.updateBalance(request.getEmail(), request.getNewBalance());
        return ResponseEntity.ok("Balance updated successfully");
    }
} 