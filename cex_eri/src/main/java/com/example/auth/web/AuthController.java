package com.example.auth.web;

import com.example.auth.service.AuthService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Validated
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(
            @Valid @RequestBody LoginRequest rq
    ) {
        String token = authService.login(rq.getUsername(), rq.getPassword());
        return ResponseEntity.ok(new JwtResponse(token));
    }

    @PostMapping("/editPassword")
    public ResponseEntity<Void> editPass(
            @AuthenticationPrincipal(expression="username") String username,
            @Valid @RequestBody EditPassRequest rq
    ) {
        authService.editPassword(username, rq.getCurrentPassword(), rq.getNewPassword());
        return ResponseEntity.ok().build();
    }

    @Data
    public static class LoginRequest {
        @NotBlank
        private String username;

        @NotBlank
        private String password;
    }

    @Data @AllArgsConstructor
    public static class JwtResponse {
        private String token;
    }

    @Data
    public static class EditPassRequest {
        @NotBlank
        private String currentPassword;

        @NotBlank
        private String newPassword;
    }
}
