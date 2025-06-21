package com.example.auth.domain;

import com.example.auth.domain.data.*;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "users")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 50)
    private String username;

    @Column(nullable = false)
    private String passwordHash;

    @Column(nullable = false, length = 100)
    private String firstName;

    @Column(nullable = false, length = 100)
    private String lastName;

    @Column(length = 100)
    private String middleName;
    
    private LocalDate dateOfBirth;

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private Gender gender;

    @Column(unique = true, length = 255)
    private String email;

    @Column(length = 20)
    private String phoneNumber;
    
    @Column(length = 500)
    private String address;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private DocumentType documentType;

    @Column(length = 50)
    private String documentNumber;
    
    private LocalDate documentIssueDate;
    private LocalDate documentExpiryDate;
    
    @Column(length = 500)
    private String documentPhotoPath;

    @Column(nullable = false)
    private boolean isDocumentVerified = false;
    
    private Instant verificationRequestedAt;
    private Instant verifiedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private AccountStatus status = AccountStatus.ACTIVE;

    @Column(nullable = false)
    private Instant createdAt = Instant.now();
    
    @Column(nullable = false)
    private Instant updatedAt = Instant.now();
    
    private Instant lastLoginAt;

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = Instant.now();
    }
}