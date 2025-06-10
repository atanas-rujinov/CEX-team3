package com.crypto_exchange.controller;

import com.crypto_exchange.dto.BanAccountRequest;
import com.crypto_exchange.dto.DeleteAccountRequest;
import com.crypto_exchange.dto.EditUsernameRequest;
import com.crypto_exchange.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/deleteAccount")
    public ResponseEntity<String> deleteAccount(@RequestBody DeleteAccountRequest request) {
        boolean success = userService.deleteAccount(request.getUsername(), request.getPassword());
        if (success) {
            return ResponseEntity.ok("Account deleted.");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials.");
        }
    }

    @PostMapping("/editUsername")
    public ResponseEntity<String> editUsername(@RequestBody EditUsernameRequest request) {
        boolean success = userService.editUsername(request.getCurrentUsername(), request.getNewUsername());
        if (success) {
            return ResponseEntity.ok("Username updated.");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Username change failed.");
        }
    }

    @PostMapping("/banAccount")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> banAccount(@RequestBody BanAccountRequest request) {
        boolean success = userService.banAccount(request.getUsername());
        if (success) {
            return ResponseEntity.ok("Account banned.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        }
    }
} 