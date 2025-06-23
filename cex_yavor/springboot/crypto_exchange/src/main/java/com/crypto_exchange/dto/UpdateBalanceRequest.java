package com.crypto_exchange.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class UpdateBalanceRequest {
    private String email;
    private BigDecimal newBalance;
} 