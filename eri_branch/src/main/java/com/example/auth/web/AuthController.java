package com.example.auth.web;

import com.example.auth.domain.User;
import com.example.auth.repository.UserRepository;
import com.example.auth.service.AuthService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Auth Service is running!");
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(@Valid @RequestBody RegisterRequest rq) {
        if (userRepo.findByUsername(rq.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username already exists");
        }
        
        User user = User.builder()
                .username(rq.getUsername())
                .passwordHash(passwordEncoder.encode(rq.getPassword()))
                .firstName(rq.getFirstName())
                .lastName(rq.getLastName())
                .email(rq.getEmail())
                .build();
        
        userRepo.save(user);
        return ResponseEntity.ok().build();
    }

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

    @GetMapping("/userId")
    public ResponseEntity<Long> getUserId(
            @AuthenticationPrincipal(expression = "username") String username
    ) {
        User u = userRepo.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return ResponseEntity.ok(u.getId());
    }

    @Data
    static class RegisterRequest {
        @NotBlank private String username;
        @NotBlank private String password;
        @NotBlank private String firstName;
        @NotBlank private String lastName;
        private String email;
    }

    @Data
    static class LoginRequest {
        @NotBlank private String username;
        @NotBlank private String password;
    }

    @Data @AllArgsConstructor
    static class JwtResponse {
        private String token;
    }

    @Data
    static class EditPassRequest {
        @NotBlank private String currentPassword;
        @NotBlank private String newPassword;
    }
} 