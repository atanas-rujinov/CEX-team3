package com.crypto_exchange.controller;

import com.crypto_exchange.dto.DeleteAccountRequest;
import com.crypto_exchange.dto.EditUsernameRequest;
import com.crypto_exchange.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @Nested
    @DisplayName("DELETE /api/users/delete")
    class DeleteAccountTests {
        @Test
        @DisplayName("DA-01: Happy - correct password deletes")
        void deleteAccountSuccess() throws Exception {
            DeleteAccountRequest req = new DeleteAccountRequest("alice", "secret");
            when(userService.deleteAccount(any(String.class), any(String.class))).thenReturn(true);
            mockMvc.perform(delete("/api/users/delete")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(req)))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("DA-02: Negative - wrong password")
        void deleteAccountWrongPassword() throws Exception {
            DeleteAccountRequest req = new DeleteAccountRequest("alice", "wrong");
            when(userService.deleteAccount(any(String.class), any(String.class)))
                    .thenThrow(new RuntimeException("Invalid password"));
            mockMvc.perform(delete("/api/users/delete")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(req)))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.error").value("Invalid password"));
        }
    }

    @Nested
    @DisplayName("PUT /api/users/password")
    class EditPasswordTests {
        @Test
        @DisplayName("Happy path - correct current password, valid new password")
        void editPasswordSuccess() throws Exception {
            mockMvc.perform(put("/api/users/password")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"email\":\"alice@example.com\",\"currentPassword\":\"old\",\"newPassword\":\"newSecret123\"}"))
                    .andExpect(status().isOk());
        }
        @Test
        @DisplayName("Negative - wrong current password")
        void editPasswordWrongCurrent() throws Exception {
            mockMvc.perform(put("/api/users/password")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"email\":\"alice@example.com\",\"currentPassword\":\"bad\",\"newPassword\":\"newSecret123\"}"))
                    .andExpect(status().isUnauthorized());
        }
        @Test
        @DisplayName("Negative - new password too weak")
        void editPasswordWeakNew() throws Exception {
            mockMvc.perform(put("/api/users/password")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"email\":\"alice@example.com\",\"currentPassword\":\"old\",\"newPassword\":\"123\"}"))
                    .andExpect(status().isBadRequest());
        }
        @Test
        @DisplayName("Negative - missing fields")
        void editPasswordMissingFields() throws Exception {
            mockMvc.perform(put("/api/users/password")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"email\":\"alice@example.com\",\"currentPassword\":\"old\"}"))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("PUT /api/users/name")
    class EditUsernameTests {
        @Test
        @DisplayName("Happy path - change to unused username")
        void editUsernameSuccess() throws Exception {
            mockMvc.perform(put("/api/users/name")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"email\":\"alice@example.com\",\"newFirstName\":\"Bob\",\"newLastName\":\"Smith\"}"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("Name updated successfully"));
        }
        @Test
        @DisplayName("Negative - user not found")
        void editUsernameNotFound() throws Exception {
            mockMvc.perform(put("/api/users/name")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"email\":\"dave@example.com\",\"newFirstName\":\"Davo\",\"newLastName\":\"Smith\"}"))
                    .andExpect(status().isNotFound());
        }
        @Test
        @DisplayName("Negative - missing fields")
        void editUsernameMissingFields() throws Exception {
            mockMvc.perform(put("/api/users/name")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"email\":\"alice@example.com\",\"newFirstName\":\"Bob\"}"))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("PUT /api/users/ban")
    class BanAccountTests {
        @Test
        @DisplayName("Happy path - ban a normal user")
        void banAccountSuccess() throws Exception {
            BanAccountRequest req = new BanAccountRequest("bob@example.com");
            when(userService.banAccount(any(String.class))).thenReturn(true);
            mockMvc.perform(put("/api/users/ban")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(req)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("Account banned successfully"));
        }
        @Test
        @DisplayName("Negative - user not found")
        void banAccountUserNotFound() throws Exception {
            BanAccountRequest req = new BanAccountRequest("nonexistent@example.com");
            when(userService.banAccount(any(String.class))).thenThrow(new RuntimeException("User not found"));
            mockMvc.perform(put("/api/users/ban")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(req)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.error").value("User not found"));
        }
        @Test
        @DisplayName("Negative - user already banned")
        void banAccountAlreadyBanned() throws Exception {
            BanAccountRequest req = new BanAccountRequest("banned@example.com");
            when(userService.banAccount(any(String.class))).thenThrow(new RuntimeException("User is already banned"));
            mockMvc.perform(put("/api/users/ban")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(req)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error").value("User is already banned"));
        }
        @Test
        @DisplayName("Negative - missing email field")
        void banAccountMissingEmail() throws Exception {
            mockMvc.perform(put("/api/users/ban")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{}"))
                    .andExpect(status().isBadRequest());
        }
        @Test
        @DisplayName("Negative - blank email field")
        void banAccountBlankEmail() throws Exception {
            BanAccountRequest req = new BanAccountRequest("");
            mockMvc.perform(put("/api/users/ban")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(req)))
                    .andExpect(status().isBadRequest());
        }
    }
} 