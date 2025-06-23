package com.example.signup.controller;

import com.example.signup.User;
import com.example.signup.UserRepository;
import com.example.signup.JwtTokenProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserRepository userRepository;
    @MockBean
    private AuthenticationManager authenticationManager;
    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @Nested
    @DisplayName("POST /api/login")
    class LoginTests {
        @Test
        @DisplayName("Happy path - valid credentials")
        void loginSuccess() throws Exception {
            String email = "alice@example.com";
            String password = "secret";
            when(authenticationManager.authenticate(any())).thenReturn(Mockito.mock(org.springframework.security.core.Authentication.class));
            when(jwtTokenProvider.generateToken(eq(email))).thenReturn("valid.jwt.token");
            mockMvc.perform(post("/api/login")
                    .param("email", email)
                    .param("password", password))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.token").value("valid.jwt.token"))
                    .andExpect(jsonPath("$.email").value(email));
        }

        @Test
        @DisplayName("Negative - wrong password")
        void loginWrongPassword() throws Exception {
            String email = "alice@example.com";
            String password = "wrong";
            when(authenticationManager.authenticate(any())).thenThrow(new RuntimeException("Invalid email or password"));
            mockMvc.perform(post("/api/login")
                    .param("email", email)
                    .param("password", password))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$").value("Invalid email or password"));
        }

        @Test
        @DisplayName("Negative - missing email field")
        void loginMissingEmail() throws Exception {
            mockMvc.perform(post("/api/login")
                    .param("password", "secret"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Negative - missing password field")
        void loginMissingPassword() throws Exception {
            mockMvc.perform(post("/api/login")
                    .param("email", "alice@example.com"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Negative - user not found")
        void loginUserNotFound() throws Exception {
            when(authenticationManager.authenticate(any())).thenThrow(new RuntimeException("User not found"));
            mockMvc.perform(post("/api/login")
                    .param("email", "nonexistent@example.com")
                    .param("password", "secret"))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Negative - blank email")
        void loginBlankEmail() throws Exception {
            mockMvc.perform(post("/api/login")
                    .param("email", "")
                    .param("password", "secret"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Negative - blank password")
        void loginBlankPassword() throws Exception {
            mockMvc.perform(post("/api/login")
                    .param("email", "alice@example.com")
                    .param("password", ""))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("POST /api/signup")
    class SignupTests {
        @Test
        @DisplayName("Happy path - register new user")
        void signupSuccess() throws Exception {
            String email = "frank@example.com";
            String password = "GoodPass123!";
            when(userRepository.existsByEmail(email)).thenReturn(false);
            when(userRepository.existsByDocumentNumber(any())).thenReturn(false);
            when(jwtTokenProvider.generateToken(eq(email))).thenReturn("signup.jwt.token");
            mockMvc.perform(post("/api/signup")
                    .param("email", email)
                    .param("password", password)
                    .param("firstName", "Frank")
                    .param("lastName", "Test")
                    .param("dateOfBirth", "2000-01-01")
                    .param("documentType", "PASSPORT")
                    .param("documentNumber", "123456789")
                    .param("documentIssueDate", "2015-01-01")
                    .param("documentExpiryDate", "2025-01-01")
                    .param("documentPhoto", "mockFile"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.token").value("signup.jwt.token"))
                    .andExpect(jsonPath("$.email").value(email));
        }

        @Test
        @DisplayName("Negative - email already registered")
        void signupEmailTaken() throws Exception {
            String email = "alice@example.com";
            when(userRepository.existsByEmail(email)).thenReturn(true);
            mockMvc.perform(post("/api/signup")
                    .param("email", email)
                    .param("password", "Whatever")
                    .param("firstName", "Alice")
                    .param("lastName", "Test")
                    .param("dateOfBirth", "2000-01-01")
                    .param("documentType", "PASSPORT")
                    .param("documentNumber", "123456789")
                    .param("documentIssueDate", "2015-01-01")
                    .param("documentExpiryDate", "2025-01-01")
                    .param("documentPhoto", "mockFile"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$").value("Email already registered"));
        }

        @Test
        @DisplayName("Negative - missing email field")
        void signupMissingEmail() throws Exception {
            mockMvc.perform(post("/api/signup")
                    .param("password", "GoodPass123!")
                    .param("firstName", "Frank")
                    .param("lastName", "Test")
                    .param("dateOfBirth", "2000-01-01")
                    .param("documentType", "PASSPORT")
                    .param("documentNumber", "123456789")
                    .param("documentIssueDate", "2015-01-01")
                    .param("documentExpiryDate", "2025-01-01")
                    .param("documentPhoto", "mockFile"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Negative - missing password field")
        void signupMissingPassword() throws Exception {
            mockMvc.perform(post("/api/signup")
                    .param("email", "frank@example.com")
                    .param("firstName", "Frank")
                    .param("lastName", "Test")
                    .param("dateOfBirth", "2000-01-01")
                    .param("documentType", "PASSPORT")
                    .param("documentNumber", "123456789")
                    .param("documentIssueDate", "2015-01-01")
                    .param("documentExpiryDate", "2025-01-01")
                    .param("documentPhoto", "mockFile"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Negative - blank email")
        void signupBlankEmail() throws Exception {
            mockMvc.perform(post("/api/signup")
                    .param("email", "")
                    .param("password", "GoodPass123!")
                    .param("firstName", "Frank")
                    .param("lastName", "Test")
                    .param("dateOfBirth", "2000-01-01")
                    .param("documentType", "PASSPORT")
                    .param("documentNumber", "123456789")
                    .param("documentIssueDate", "2015-01-01")
                    .param("documentExpiryDate", "2025-01-01")
                    .param("documentPhoto", "mockFile"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Negative - blank password")
        void signupBlankPassword() throws Exception {
            mockMvc.perform(post("/api/signup")
                    .param("email", "frank@example.com")
                    .param("password", "")
                    .param("firstName", "Frank")
                    .param("lastName", "Test")
                    .param("dateOfBirth", "2000-01-01")
                    .param("documentType", "PASSPORT")
                    .param("documentNumber", "123456789")
                    .param("documentIssueDate", "2015-01-01")
                    .param("documentExpiryDate", "2025-01-01")
                    .param("documentPhoto", "mockFile"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Negative - invalid email format")
        void signupInvalidEmailFormat() throws Exception {
            mockMvc.perform(post("/api/signup")
                    .param("email", "not-an-email")
                    .param("password", "GoodPass123!")
                    .param("firstName", "Frank")
                    .param("lastName", "Test")
                    .param("dateOfBirth", "2000-01-01")
                    .param("documentType", "PASSPORT")
                    .param("documentNumber", "123456789")
                    .param("documentIssueDate", "2015-01-01")
                    .param("documentExpiryDate", "2025-01-01")
                    .param("documentPhoto", "mockFile"))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("POST /api/edit-password")
    class EditPasswordTests {
        @Test
        @DisplayName("Happy path - correct current password, valid new password")
        void editPasswordSuccess() throws Exception {
            // Simulate successful password change
            mockMvc.perform(post("/api/edit-password")
                    .param("email", "alice@example.com")
                    .param("currentPassword", "old")
                    .param("newPassword", "newSecret123"))
                    .andExpect(status().isOk());
        }
        @Test
        @DisplayName("Negative - wrong current password")
        void editPasswordWrongCurrent() throws Exception {
            mockMvc.perform(post("/api/edit-password")
                    .param("email", "alice@example.com")
                    .param("currentPassword", "bad")
                    .param("newPassword", "newSecret123"))
                    .andExpect(status().isUnauthorized());
        }
        @Test
        @DisplayName("Negative - new password too weak")
        void editPasswordWeakNew() throws Exception {
            mockMvc.perform(post("/api/edit-password")
                    .param("email", "alice@example.com")
                    .param("currentPassword", "old")
                    .param("newPassword", "123"))
                    .andExpect(status().isBadRequest());
        }
        @Test
        @DisplayName("Negative - missing fields")
        void editPasswordMissingFields() throws Exception {
            mockMvc.perform(post("/api/edit-password")
                    .param("email", "alice@example.com")
                    .param("currentPassword", "old"))
                    .andExpect(status().isBadRequest());
        }
        @Test
        @DisplayName("Negative - new password same as old")
        void editPasswordSameAsOld() throws Exception {
            mockMvc.perform(post("/api/edit-password")
                    .param("email", "alice@example.com")
                    .param("currentPassword", "old")
                    .param("newPassword", "old"))
                    .andExpect(status().isBadRequest());
        }
        @Test
        @DisplayName("Negative - user not found")
        void editPasswordUserNotFound() throws Exception {
            mockMvc.perform(post("/api/edit-password")
                    .param("email", "nonexistent@example.com")
                    .param("currentPassword", "old")
                    .param("newPassword", "newSecret123"))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("POST /api/edit-username")
    class EditUsernameTests {
        @Test
        @DisplayName("Happy path - change to unused username")
        void editUsernameSuccess() throws Exception {
            mockMvc.perform(post("/api/edit-username")
                    .param("currentUsername", "alice")
                    .param("newUsername", "bob"))
                    .andExpect(status().isOk());
        }
        @Test
        @DisplayName("Negative - new username already exists")
        void editUsernameExists() throws Exception {
            mockMvc.perform(post("/api/edit-username")
                    .param("currentUsername", "alice")
                    .param("newUsername", "charlie"))
                    .andExpect(status().isConflict());
        }
        @Test
        @DisplayName("Negative - current user not found")
        void editUsernameNotFound() throws Exception {
            mockMvc.perform(post("/api/edit-username")
                    .param("currentUsername", "dave")
                    .param("newUsername", "davo"))
                    .andExpect(status().isNotFound());
        }
        @Test
        @DisplayName("Negative - invalid new username (empty)")
        void editUsernameInvalid() throws Exception {
            mockMvc.perform(post("/api/edit-username")
                    .param("currentUsername", "alice")
                    .param("newUsername", ""))
                    .andExpect(status().isBadRequest());
        }
        @Test
        @DisplayName("Negative - new username same as current")
        void editUsernameSameAsCurrent() throws Exception {
            mockMvc.perform(post("/api/edit-username")
                    .param("currentUsername", "alice")
                    .param("newUsername", "alice"))
                    .andExpect(status().isBadRequest());
        }
        @Test
        @DisplayName("Negative - blank new username")
        void editUsernameBlankNew() throws Exception {
            mockMvc.perform(post("/api/edit-username")
                    .param("currentUsername", "alice")
                    .param("newUsername", " "))
                    .andExpect(status().isBadRequest());
        }
    }
} 