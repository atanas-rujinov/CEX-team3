package com.crypto_exchange.dto;

import lombok.Data;

@Data
public class EditPasswordRequest {
    private String email;
    private String currentPassword;
    private String newPassword;
} 