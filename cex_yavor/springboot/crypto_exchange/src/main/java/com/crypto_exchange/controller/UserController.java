package com.crypto_exchange.controller;

import com.crypto_exchange.dto.BanAccountRequest;
import com.crypto_exchange.dto.DeleteAccountRequest;
import com.crypto_exchange.dto.EditUsernameRequest;
import com.crypto_exchange.service.UserService;
import lombok.RequiredArgsConstructor;
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
        userService.deleteAccount(request.getUsername(), request.getPassword());
        return ResponseEntity.ok("Account deleted successfully");
    }

    @PostMapping("/editUsername")
    public ResponseEntity<String> editUsername(@RequestBody EditUsernameRequest request) {
        userService.editUsername(request.getCurrentUsername(), request.getNewUsername());
        return ResponseEntity.ok("Username updated successfully");
    }

    @PostMapping("/banAccount")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> banAccount(@RequestBody BanAccountRequest request) {
        userService.banAccount(request.getUsername());
        return ResponseEntity.ok("Account banned successfully");
    }
} 