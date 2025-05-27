package com.example.auth.service;

import com.example.auth.domain.User;
import com.example.auth.repository.UserRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
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
        Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );
        User u = userRepo.findByUsername(username).orElseThrow();
        u.setLastLoginAt(Instant.now());
        userRepo.save(u);

        Date now = new Date();
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + jwtExpirationMs))
                .signWith(Keys.hmacShaKeyFor(jwtSecret.getBytes()))
                .compact();
    }

    public void editPassword(String username, String currentPassword, String newPassword) {
        User u = userRepo.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (!pwEncoder.matches(currentPassword, u.getPasswordHash())) {
            throw new BadCredentialsException("Current password is incorrect");
        }
        u.setPasswordHash(pwEncoder.encode(newPassword));
        userRepo.save(u);
    }

    public void uploadDocument(Long userId, MultipartFile file) throws IOException {
        if (!file.getContentType().startsWith("image/")) {
            throw new IllegalArgumentException("File must be an image");
        }
        Path dir = Paths.get(uploadDir, String.valueOf(userId));
        Files.createDirectories(dir);
        String filename = UUID.randomUUID() + "-" + file.getOriginalFilename();
        Path target = dir.resolve(filename);
        Files.write(target, file.getBytes());

        User u = userRepo.findById(userId).orElseThrow();
        u.setDocumentPhotoPath(target.toString());
        u.setVerificationRequestedAt(Instant.now());
        userRepo.save(u);
    }
}