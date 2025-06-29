package com.crypto_exchange.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EditUsernameRequest {
    private String email;
    private String newFirstName;
    private String newLastName;
} 