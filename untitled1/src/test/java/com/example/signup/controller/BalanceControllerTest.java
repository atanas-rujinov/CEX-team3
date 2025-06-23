package com.example.signup.controller;

import com.example.signup.BalanceRepository;
import com.example.signup.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BalanceController.class)
class BalanceControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BalanceRepository balanceRepository;

    @Nested
    @DisplayName("POST /withdraw")
    class WithdrawTests {
        @Test
        @DisplayName("Happy path - withdraw with sufficient funds")
        void withdrawSuccess() throws Exception {
            // Simulate a user with enough balance
            User user = new User();
            user.setId(1L);
            Authentication auth = Mockito.mock(Authentication.class);
            when(auth.getPrincipal()).thenReturn(user);
            SecurityContextHolder.getContext().setAuthentication(auth);
            when(balanceRepository.findByUserIdAndCurrencyType(eq(1L), eq(com.example.signup.Balance.CurrencyType.USD)))
                    .thenReturn(Optional.of(new com.example.signup.Balance(user, com.example.signup.Balance.CurrencyType.USD)));
            mockMvc.perform(post("/withdraw")
                    .param("currency", "USD")
                    .param("amount", "100"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("Withdrawal successful"));
        }

        @Test
        @DisplayName("Negative - insufficient funds")
        void withdrawInsufficientFunds() throws Exception {
            User user = new User();
            user.setId(1L);
            Authentication auth = Mockito.mock(Authentication.class);
            when(auth.getPrincipal()).thenReturn(user);
            SecurityContextHolder.getContext().setAuthentication(auth);
            com.example.signup.Balance balance = new com.example.signup.Balance(user, com.example.signup.Balance.CurrencyType.USD);
            balance.setAmount(BigDecimal.ZERO);
            when(balanceRepository.findByUserIdAndCurrencyType(eq(1L), eq(com.example.signup.Balance.CurrencyType.USD)))
                    .thenReturn(Optional.of(balance));
            mockMvc.perform(post("/withdraw")
                    .param("currency", "USD")
                    .param("amount", "100"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$").value("Insufficient funds"));
        }

        @Test
        @DisplayName("Negative - missing currency field")
        void withdrawMissingCurrency() throws Exception {
            mockMvc.perform(post("/withdraw")
                    .param("amount", "100"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Negative - missing amount field")
        void withdrawMissingAmount() throws Exception {
            mockMvc.perform(post("/withdraw")
                    .param("currency", "USD"))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("POST /deposit")
    class DepositTests {
        @Test
        @DisplayName("Happy path - deposit positive amount")
        void depositSuccess() throws Exception {
            User user = new User();
            user.setId(1L);
            Authentication auth = Mockito.mock(Authentication.class);
            when(auth.getPrincipal()).thenReturn(user);
            SecurityContextHolder.getContext().setAuthentication(auth);
            when(balanceRepository.findByUserIdAndCurrencyType(eq(1L), eq(com.example.signup.Balance.CurrencyType.USD)))
                    .thenReturn(Optional.of(new com.example.signup.Balance(user, com.example.signup.Balance.CurrencyType.USD)));
            mockMvc.perform(post("/deposit")
                    .param("currency", "USD")
                    .param("amount", "100"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("Deposit successful"));
        }
        @Test
        @DisplayName("Negative - negative amount")
        void depositNegativeAmount() throws Exception {
            mockMvc.perform(post("/deposit")
                    .param("currency", "USD")
                    .param("amount", "-50"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$").value("Amount must be positive"));
        }
        @Test
        @DisplayName("Negative - zero amount")
        void depositZeroAmount() throws Exception {
            mockMvc.perform(post("/deposit")
                    .param("currency", "USD")
                    .param("amount", "0"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$").value("Amount must be positive"));
        }
        @Test
        @DisplayName("Negative - unsupported currency")
        void depositUnsupportedCurrency() throws Exception {
            mockMvc.perform(post("/deposit")
                    .param("currency", "DOGE")
                    .param("amount", "100"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$").value("Invalid currency type"));
        }
        @Test
        @DisplayName("Negative - missing currency field")
        void depositMissingCurrency() throws Exception {
            mockMvc.perform(post("/deposit")
                    .param("amount", "100"))
                    .andExpect(status().isBadRequest());
        }
        @Test
        @DisplayName("Negative - missing amount field")
        void depositMissingAmount() throws Exception {
            mockMvc.perform(post("/deposit")
                    .param("currency", "USD"))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("GET /api/balances")
    class ViewBalancesTests {
        @Test
        @DisplayName("Happy path - view all balances")
        void viewAllBalancesSuccess() throws Exception {
            User user = new User();
            user.setId(1L);
            Authentication auth = Mockito.mock(Authentication.class);
            when(auth.getPrincipal()).thenReturn(user);
            SecurityContextHolder.getContext().setAuthentication(auth);
            when(balanceRepository.findByUserId(1L)).thenReturn(List.of(
                    new com.example.signup.Balance(user, com.example.signup.Balance.CurrencyType.USD),
                    new com.example.signup.Balance(user, com.example.signup.Balance.CurrencyType.BTC)
            ));
            mockMvc.perform(get("/api/balances"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].currency").exists())
                    .andExpect(jsonPath("$[0].amount").exists());
        }
    }

    @Nested
    @DisplayName("GET /api/balances/{currency}")
    class ViewBalanceByCurrencyTests {
        @Test
        @DisplayName("Happy path - view balance by currency")
        void viewBalanceByCurrencySuccess() throws Exception {
            User user = new User();
            user.setId(1L);
            Authentication auth = Mockito.mock(Authentication.class);
            when(auth.getPrincipal()).thenReturn(user);
            SecurityContextHolder.getContext().setAuthentication(auth);
            when(balanceRepository.findByUserIdAndCurrencyType(eq(1L), eq(com.example.signup.Balance.CurrencyType.USD)))
                    .thenReturn(Optional.of(new com.example.signup.Balance(user, com.example.signup.Balance.CurrencyType.USD)));
            mockMvc.perform(get("/api/balances/USD"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.currency").value("USD"));
        }
        @Test
        @DisplayName("Negative - invalid currency type")
        void viewBalanceByCurrencyInvalid() throws Exception {
            mockMvc.perform(get("/api/balances/DOGE"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$").value("Invalid currency type"));
        }
    }
} 