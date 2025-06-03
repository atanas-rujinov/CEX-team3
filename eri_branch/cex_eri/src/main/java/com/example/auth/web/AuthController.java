package com.example.auth.web;

import com.example.auth.service.AuthService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
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
            @RequestParam String username,
            @Valid @RequestBody EditPassRequest rq
    ) {
        authService.editPassword(username, rq.getCurrentPassword(), rq.getNewPassword());
        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "/users/{id}/uploadDocument", consumes = "multipart/form-data")
    public ResponseEntity<Void> uploadDocument(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file
    ) throws Exception {
        authService.uploadDocument(id, file);
        return ResponseEntity.ok().build();
    }

    @Data static class LoginRequest {
        @NotBlank private String username;
        @NotBlank private String password;
    }

    @Data @AllArgsConstructor
    static class JwtResponse { private String token; }

    @Data static class EditPassRequest {
        @NotBlank private String currentPassword;
        @NotBlank private String newPassword;
    }
}