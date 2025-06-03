package com.example.signup;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface BalanceRepository extends JpaRepository<Balance, Long> {
    List<Balance> findByUserId(Long userId);
    Optional<Balance> findByUserIdAndCurrencyType(Long userId, Balance.CurrencyType currencyType);
} 