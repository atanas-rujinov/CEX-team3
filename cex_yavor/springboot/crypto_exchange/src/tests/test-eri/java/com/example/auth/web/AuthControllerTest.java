package com.example.auth.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Nested
    @DisplayName("POST /account/login")
    class LoginTests {
        @Test
        @DisplayName("LI-01: Happy path - valid credentials")
        void loginSuccess() throws Exception {
            String body = "{\"username\":\"alice\",\"password\":\"secret\"}";
            mockMvc.perform(post("/account/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(body))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("Login successful"))
                    .andExpect(jsonPath("$.token").exists());
        }

        @Test
        @DisplayName("LI-02: Negative - wrong password")
        void loginWrongPassword() throws Exception {
            String body = "{\"username\":\"alice\",\"password\":\"wrong\"}";
            mockMvc.perform(post("/account/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(body))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.error").value("Invalid credentials"));
        }

        @Test
        @DisplayName("LI-04: Negative - missing fields")
        void loginMissingFields() throws Exception {
            String body = "{\"username\":\"alice\"}";
            mockMvc.perform(post("/account/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(body))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error").value("password required"));
        }

        @Test
        @DisplayName("Negative - missing username field")
        void loginMissingUsername() throws Exception {
            mockMvc.perform(post("/account/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"password\":\"secret\"}"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Negative - missing password field")
        void loginMissingPassword() throws Exception {
            mockMvc.perform(post("/account/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"username\":\"alice\"}"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Negative - user not found")
        void loginUserNotFound() throws Exception {
            mockMvc.perform(post("/account/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"username\":\"nonexistent\",\"password\":\"secret\"}"))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Negative - blank username")
        void loginBlankUsername() throws Exception {
            mockMvc.perform(post("/account/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"username\":\"\",\"password\":\"secret\"}"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Negative - blank password")
        void loginBlankPassword() throws Exception {
            mockMvc.perform(post("/account/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"username\":\"alice\",\"password\":\"\"}"))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("POST /account/signup")
    class SignupTests {
        @Test
        @DisplayName("SU-01: Happy path - register new user")
        void signupSuccess() throws Exception {
            String body = "{\"username\":\"frank\",\"password\":\"GoodPass123!\"}";
            mockMvc.perform(post("/account/signup")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(body))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.username").value("frank"));
        }

        @Test
        @DisplayName("SU-02: Negative - username already taken")
        void signupUsernameTaken() throws Exception {
            String body = "{\"username\":\"alice\",\"password\":\"Whatever\"}";
            mockMvc.perform(post("/account/signup")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(body))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.error").value("Username already taken"));
        }

        @Test
        @DisplayName("SU-03: Negative - password too weak")
        void signupWeakPassword() throws Exception {
            String body = "{\"username\":\"george\",\"password\":\"123\"}";
            mockMvc.perform(post("/account/signup")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(body))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error").value("Password does not meet strength requirements"));
        }

        @Test
        @DisplayName("Negative - missing username field")
        void signupMissingUsername() throws Exception {
            mockMvc.perform(post("/account/signup")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"password\":\"GoodPass123!\"}"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Negative - missing password field")
        void signupMissingPassword() throws Exception {
            mockMvc.perform(post("/account/signup")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"username\":\"frank\"}"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Negative - blank username")
        void signupBlankUsername() throws Exception {
            mockMvc.perform(post("/account/signup")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"username\":\"\",\"password\":\"GoodPass123!\"}"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Negative - blank password")
        void signupBlankPassword() throws Exception {
            mockMvc.perform(post("/account/signup")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"username\":\"frank\",\"password\":\"\"}"))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("POST /account/edit-password")
    class EditPasswordTests {
        @Test
        @DisplayName("Happy path - correct current password, valid new password")
        void editPasswordSuccess() throws Exception {
            mockMvc.perform(post("/account/edit-password")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"username\":\"alice\",\"currentPassword\":\"old\",\"newPassword\":\"newSecret123\"}"))
                    .andExpect(status().isOk());
        }
        @Test
        @DisplayName("Negative - wrong current password")
        void editPasswordWrongCurrent() throws Exception {
            mockMvc.perform(post("/account/edit-password")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"username\":\"alice\",\"currentPassword\":\"bad\",\"newPassword\":\"newSecret123\"}"))
                    .andExpect(status().isUnauthorized());
        }
        @Test
        @DisplayName("Negative - new password too weak")
        void editPasswordWeakNew() throws Exception {
            mockMvc.perform(post("/account/edit-password")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"username\":\"alice\",\"currentPassword\":\"old\",\"newPassword\":\"123\"}"))
                    .andExpect(status().isBadRequest());
        }
        @Test
        @DisplayName("Negative - missing fields")
        void editPasswordMissingFields() throws Exception {
            mockMvc.perform(post("/account/edit-password")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"username\":\"alice\",\"currentPassword\":\"old\"}"))
                    .andExpect(status().isBadRequest());
        }
        @Test
        @DisplayName("Negative - new password same as current")
        void editPasswordSameAsCurrent() throws Exception {
            mockMvc.perform(post("/account/edit-password")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"username\":\"alice\",\"currentPassword\":\"old\",\"newPassword\":\"old\"}"))
                    .andExpect(status().isBadRequest());
        }
        @Test
        @DisplayName("Negative - user not found")
        void editPasswordUserNotFound() throws Exception {
            mockMvc.perform(post("/account/edit-password")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"username\":\"nonexistent\",\"currentPassword\":\"old\",\"newPassword\":\"newSecret123\"}"))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("POST /account/edit-username")
    class EditUsernameTests {
        @Test
        @DisplayName("Happy path - change to unused username")
        void editUsernameSuccess() throws Exception {
            mockMvc.perform(post("/account/edit-username")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"currentUsername\":\"alice\",\"newUsername\":\"bob\"}"))
                    .andExpect(status().isOk());
        }
        @Test
        @DisplayName("Negative - new username already exists")
        void editUsernameExists() throws Exception {
            mockMvc.perform(post("/account/edit-username")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"currentUsername\":\"alice\",\"newUsername\":\"charlie\"}"))
                    .andExpect(status().isConflict());
        }
        @Test
        @DisplayName("Negative - current user not found")
        void editUsernameNotFound() throws Exception {
            mockMvc.perform(post("/account/edit-username")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"currentUsername\":\"dave\",\"newUsername\":\"davo\"}"))
                    .andExpect(status().isNotFound());
        }
        @Test
        @DisplayName("Negative - invalid new username (empty)")
        void editUsernameInvalid() throws Exception {
            mockMvc.perform(post("/account/edit-username")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"currentUsername\":\"alice\",\"newUsername\":\"\"}"))
                    .andExpect(status().isBadRequest());
        }
        @Test
        @DisplayName("Negative - new username same as current")
        void editUsernameSameAsCurrent() throws Exception {
            mockMvc.perform(post("/account/edit-username")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"currentUsername\":\"alice\",\"newUsername\":\"alice\"}"))
                    .andExpect(status().isBadRequest());
        }
        @Test
        @DisplayName("Negative - blank new username")
        void editUsernameBlankNew() throws Exception {
            mockMvc.perform(post("/account/edit-username")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"currentUsername\":\"alice\",\"newUsername\":\" \"}"))
                    .andExpect(status().isBadRequest());
        }
    }
} 