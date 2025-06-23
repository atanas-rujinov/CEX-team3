package com.example.signup;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/balances")
@CrossOrigin(origins = "*")
public class BalanceController {

    private final BalanceRepository balanceRepository;
    private final UserRepository userRepository;

    @Autowired
    public BalanceController(BalanceRepository balanceRepository, UserRepository userRepository) {
        this.balanceRepository = balanceRepository;
        this.userRepository = userRepository;
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @GetMapping
    public ResponseEntity<?> getAllBalances() {
        User user = getCurrentUser();
        List<Balance> balances = balanceRepository.findByUserId(user.getId());
        
        // If user has no balances, initialize them
        if (balances.isEmpty()) {
            balances = initializeBalances(user);
        }

        List<Map<String, Object>> response = balances.stream()
                .map(balance -> {
                    Map<String, Object> map = new java.util.HashMap<>();
                    map.put("currency", balance.getCurrencyType());
                    map.put("amount", balance.getAmount());
                    return map;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{currency}")
    public ResponseEntity<?> getBalance(@PathVariable String currency) {
        try {
            User user = getCurrentUser();
            Balance.CurrencyType currencyType = Balance.CurrencyType.valueOf(currency.toUpperCase());
            
            Balance balance = balanceRepository.findByUserIdAndCurrencyType(user.getId(), currencyType)
                    .orElseGet(() -> new Balance(user, currencyType));

            Map<String, Object> map = new java.util.HashMap<>();
            map.put("currency", balance.getCurrencyType());
            map.put("amount", balance.getAmount());
            return ResponseEntity.ok(map);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid currency type");
        }
    }

    @PostMapping("/deposit")
    public ResponseEntity<?> deposit(
            @RequestParam String currency,
            @RequestParam BigDecimal amount) {
        try {
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                return ResponseEntity.badRequest().body("Amount must be positive");
            }

            User user = getCurrentUser();
            Balance.CurrencyType currencyType = Balance.CurrencyType.valueOf(currency.toUpperCase());
            
            Balance balance = balanceRepository.findByUserIdAndCurrencyType(user.getId(), currencyType)
                    .orElseGet(() -> new Balance(user, currencyType));

            balance.setAmount(balance.getAmount().add(amount));
            balanceRepository.save(balance);

            Map<String, Object> map = new java.util.HashMap<>();
            map.put("currency", balance.getCurrencyType());
            map.put("amount", balance.getAmount());
            map.put("message", "Deposit successful");
            return ResponseEntity.ok(map);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid currency type");
        }
    }

    @PostMapping("/withdraw")
    public ResponseEntity<?> withdraw(
            @RequestParam String currency,
            @RequestParam BigDecimal amount) {
        try {
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                return ResponseEntity.badRequest().body("Amount must be positive");
            }

            User user = getCurrentUser();
            Balance.CurrencyType currencyType = Balance.CurrencyType.valueOf(currency.toUpperCase());
            
            Balance balance = balanceRepository.findByUserIdAndCurrencyType(user.getId(), currencyType)
                    .orElseGet(() -> new Balance(user, currencyType));

            if (balance.getAmount().compareTo(amount) < 0) {
                return ResponseEntity.badRequest().body("Insufficient funds");
            }

            balance.setAmount(balance.getAmount().subtract(amount));
            balanceRepository.save(balance);

            Map<String, Object> map = new java.util.HashMap<>();
            map.put("currency", balance.getCurrencyType());
            map.put("amount", balance.getAmount());
            map.put("message", "Withdrawal successful");
            return ResponseEntity.ok(map);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid currency type");
        }
    }

    private List<Balance> initializeBalances(User user) {
        List<Balance> balances = List.of(
            new Balance(user, Balance.CurrencyType.USD),
            new Balance(user, Balance.CurrencyType.BTC),
            new Balance(user, Balance.CurrencyType.ETH),
            new Balance(user, Balance.CurrencyType.USDT),
            new Balance(user, Balance.CurrencyType.BNB)
        );
        return balanceRepository.saveAll(balances);
    }
} 