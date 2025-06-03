package com.example.signup;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@Entity
@Table(name = "balances")
public class Balance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CurrencyType currencyType;

    @Column(nullable = false, precision = 20, scale = 8)
    private BigDecimal amount;

    public enum CurrencyType {
        USD,    // Fiat currency
        BTC,    // Bitcoin
        ETH,    // Ethereum
        USDT,   // Tether
        BNB     // Binance Coin
    }

    public Balance(User user, CurrencyType currencyType) {
        this.user = user;
        this.currencyType = currencyType;
        this.amount = BigDecimal.ZERO;
    }
} 