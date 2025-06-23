package com.crypto_exchange.controller;

import com.crypto_exchange.dto.AuthenticationRequest;
import com.crypto_exchange.dto.AuthenticationResponse;
import com.crypto_exchange.dto.RegisterRequest;
import com.crypto_exchange.service.AuthenticationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthenticationService authenticationService;

    @Nested
    @DisplayName("POST /api/v1/auth/authenticate")
    class LoginTests {
        @Test
        @DisplayName("LI-01: Happy path - valid credentials")
        void loginSuccess() throws Exception {
            AuthenticationRequest req = new AuthenticationRequest("alice", "secret");
            when(authenticationService.authenticate(any(AuthenticationRequest.class)))
                    .thenReturn(AuthenticationResponse.builder().token("valid.jwt.token").build());
            mockMvc.perform(post("/api/v1/auth/authenticate")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(req)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.token").value("valid.jwt.token"));
        }

        @Test
        @DisplayName("LI-02: Negative - wrong password")
        void loginWrongPassword() throws Exception {
            AuthenticationRequest req = new AuthenticationRequest("alice", "wrong");
            when(authenticationService.authenticate(any(AuthenticationRequest.class)))
                    .thenThrow(new RuntimeException("Invalid email or password"));
            mockMvc.perform(post("/api/v1/auth/authenticate")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(req)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error").value("Invalid email or password"));
        }

        @Test
        @DisplayName("Negative - missing email field")
        void loginMissingEmail() throws Exception {
            mockMvc.perform(post("/api/v1/auth/authenticate")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"password\":\"secret\"}"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Negative - missing password field")
        void loginMissingPassword() throws Exception {
            mockMvc.perform(post("/api/v1/auth/authenticate")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"email\":\"alice@example.com\"}"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Negative - user not found")
        void loginUserNotFound() throws Exception {
            when(authenticationService.authenticate(any())).thenThrow(new RuntimeException("User not found"));
            mockMvc.perform(post("/api/v1/auth/authenticate")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"email\":\"nonexistent@example.com\",\"password\":\"secret\"}"))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Negative - blank email")
        void loginBlankEmail() throws Exception {
            mockMvc.perform(post("/api/v1/auth/authenticate")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"email\":\"\",\"password\":\"secret\"}"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Negative - blank password")
        void loginBlankPassword() throws Exception {
            mockMvc.perform(post("/api/v1/auth/authenticate")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"email\":\"alice@example.com\",\"password\":\"\"}"))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("POST /api/v1/auth/register")
    class SignupTests {
        @Test
        @DisplayName("SU-01: Happy path - register new user")
        void signupSuccess() throws Exception {
            RegisterRequest req = new RegisterRequest("frank", "GoodPass123!", "Frank", "Test");
            when(authenticationService.register(any(RegisterRequest.class)))
                    .thenReturn(AuthenticationResponse.builder().token("signup.jwt.token").build());
            mockMvc.perform(post("/api/v1/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(req)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.token").value("signup.jwt.token"));
        }

        @Test
        @DisplayName("SU-02: Negative - username already taken")
        void signupUsernameTaken() throws Exception {
            RegisterRequest req = new RegisterRequest("alice", "Whatever", "Alice", "Test");
            when(authenticationService.register(any(RegisterRequest.class)))
                    .thenThrow(new RuntimeException("Email already registered"));
            mockMvc.perform(post("/api/v1/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(req)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error").value("Email already registered"));
        }

        @Test
        @DisplayName("Negative - missing email field")
        void signupMissingEmail() throws Exception {
            mockMvc.perform(post("/api/v1/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"password\":\"GoodPass123!\",\"firstName\":\"Frank\",\"lastName\":\"Test\"}"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Negative - missing password field")
        void signupMissingPassword() throws Exception {
            mockMvc.perform(post("/api/v1/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"email\":\"frank@example.com\",\"firstName\":\"Frank\",\"lastName\":\"Test\"}"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Negative - blank email")
        void signupBlankEmail() throws Exception {
            mockMvc.perform(post("/api/v1/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"email\":\"\",\"password\":\"GoodPass123!\",\"firstName\":\"Frank\",\"lastName\":\"Test\"}"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Negative - blank password")
        void signupBlankPassword() throws Exception {
            mockMvc.perform(post("/api/v1/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"email\":\"frank@example.com\",\"password\":\"\",\"firstName\":\"Frank\",\"lastName\":\"Test\"}"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Negative - invalid email format")
        void signupInvalidEmailFormat() throws Exception {
            mockMvc.perform(post("/api/v1/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"email\":\"not-an-email\",\"password\":\"GoodPass123!\",\"firstName\":\"Frank\",\"lastName\":\"Test\"}"))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("POST /api/v1/auth/register/admin")
    class RegisterAdminTests {
        @Test
        @DisplayName("Happy path - admin creates a new admin")
        void registerAdminSuccess() throws Exception {
            RegisterRequest req = new RegisterRequest("newadmin@example.com", "StrongP@ssw0rd", "New", "Admin");
            when(authenticationService.registerAdmin(any(RegisterRequest.class)))
                    .thenReturn(AuthenticationResponse.builder().token("admin.jwt.token").build());
            mockMvc.perform(post("/api/v1/auth/register/admin")
                    .header("Authorization", "Bearer valid_admin_JWT")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(req)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.token").value("admin.jwt.token"));
        }
        @Test
        @DisplayName("Negative - missing JWT in request header")
        void registerAdminMissingJwt() throws Exception {
            RegisterRequest req = new RegisterRequest("newadmin@example.com", "StrongP@ssw0rd", "New", "Admin");
            mockMvc.perform(post("/api/v1/auth/register/admin")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(req)))
                    .andExpect(status().isUnauthorized());
        }
        @Test
        @DisplayName("Negative - invalid or expired JWT")
        void registerAdminInvalidJwt() throws Exception {
            RegisterRequest req = new RegisterRequest("newadmin@example.com", "StrongP@ssw0rd", "New", "Admin");
            mockMvc.perform(post("/api/v1/auth/register/admin")
                    .header("Authorization", "Bearer invalid_or_expired_JWT")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(req)))
                    .andExpect(status().isUnauthorized());
        }
        @Test
        @DisplayName("Negative - JWT belongs to a non-admin user")
        void registerAdminNonAdminJwt() throws Exception {
            RegisterRequest req = new RegisterRequest("newadmin@example.com", "StrongP@ssw0rd", "New", "Admin");
            mockMvc.perform(post("/api/v1/auth/register/admin")
                    .header("Authorization", "Bearer valid_nonadmin_JWT")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(req)))
                    .andExpect(status().isForbidden());
        }
        @Test
        @DisplayName("Negative - missing required fields (password)")
        void registerAdminMissingFields() throws Exception {
            String body = "{\"email\":\"onlyUser@example.com\",\"firstName\":\"Only\",\"lastName\":\"User\"}";
            mockMvc.perform(post("/api/v1/auth/register/admin")
                    .header("Authorization", "Bearer valid_admin_JWT")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(body))
                    .andExpect(status().isBadRequest());
        }
        @Test
        @DisplayName("Negative - weak or invalid password format")
        void registerAdminWeakPassword() throws Exception {
            RegisterRequest req = new RegisterRequest("newadmin@example.com", "abc", "New", "Admin");
            mockMvc.perform(post("/api/v1/auth/register/admin")
                    .header("Authorization", "Bearer valid_admin_JWT")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(req)))
                    .andExpect(status().isBadRequest());
        }
        @Test
        @DisplayName("Negative - email already exists (duplicate)")
        void registerAdminEmailExists() throws Exception {
            RegisterRequest req = new RegisterRequest("existingAdmin@example.com", "AnotherP@ss1", "Existing", "Admin");
            when(authenticationService.registerAdmin(any(RegisterRequest.class)))
                    .thenThrow(new RuntimeException("Email already registered"));
            mockMvc.perform(post("/api/v1/auth/register/admin")
                    .header("Authorization", "Bearer valid_admin_JWT")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(req)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error").value("Email already registered"));
        }
    }
} 