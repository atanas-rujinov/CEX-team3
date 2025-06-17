package com.example.auth.service;

import com.example.auth.domain.User;
import com.example.auth.repository.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final AuthenticationManager authManager;
    private final UserRepository userRepo;
    private final PasswordEncoder pwEncoder;

    @Value("${security.jwt.secret}")
    private String jwtSecret;

    @Value("${security.jwt.expirationMs}")
    private long jwtExpirationMs;

    @Value("${file.upload-dir}")
    private String uploadDir;

    public String login(String username, String password) {
        log.info("Attempting login for user {}", username);
        try {
            Authentication auth = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );
        } catch (AuthenticationException e) {
            log.warn("Login failed for user {}: {}", username, e.getMessage());
            throw e;
        }

        User u = userRepo.findByUsername(username)
                .orElseThrow(() -> {
                    log.error("User {} not found after successful authentication", username);
                    return new IllegalStateException("User not found");
                });
        u.setLastLoginAt(Instant.now());
        userRepo.save(u);
        log.info("User {} logged in successfully", username);

        Date now = new Date();
        String token = Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + jwtExpirationMs))
                .signWith(Keys.hmacShaKeyFor(jwtSecret.getBytes()))
                .compact();

        log.debug("Generated JWT for user {}: {}", username, token);
        return token;
    }

    public void editPassword(String username, String currentPassword, String newPassword) {
        log.info("User {} requested password change", username);
        User u = userRepo.findByUsername(username)
                .orElseThrow(() -> {
                    log.warn("Password change failed: user {} not found", username);
                    return new IllegalArgumentException("User not found");
                });

        if (!pwEncoder.matches(currentPassword, u.getPasswordHash())) {
            log.warn("User {} provided incorrect current password", username);
            throw new BadCredentialsException("Current password is incorrect");
        }

        u.setPasswordHash(pwEncoder.encode(newPassword));
        userRepo.save(u);
        log.info("Password changed successfully for user {}", username);
    }

    public void uploadDocument(Long userId, MultipartFile file) throws IOException {
        log.info("User {} uploading document {} ({} bytes)",
                userId, file.getOriginalFilename(), file.getSize());

        if (!file.getContentType().startsWith("image/")) {
            log.warn("Upload failed for user {}: invalid content type {}", userId, file.getContentType());
            throw new IllegalArgumentException("File must be an image");
        }

        Path dir = Paths.get(uploadDir, String.valueOf(userId));
        Files.createDirectories(dir);

        String filename = UUID.randomUUID() + "-" + file.getOriginalFilename();
        Path target = dir.resolve(filename);
        Files.write(target, file.getBytes());

        User u = userRepo.findById(userId)
                .orElseThrow(() -> {
                    log.error("Upload failed: user {} not found", userId);
                    return new IllegalArgumentException("User not found");
                });
        u.setDocumentPhotoPath(target.toString());
        u.setVerificationRequestedAt(Instant.now());
        userRepo.save(u);

        log.info("Document stored for user {} at {}", userId, target);
    }
}
