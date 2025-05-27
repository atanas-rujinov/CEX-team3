package com.example.auth.domain;

import com.example.auth.domain.data.*;
        import jakarta.persistence.*;
        import lombok.*;

        import java.time.Instant;
import java.time.LocalDate;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String passwordHash;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    private String middleName;
    private LocalDate dateOfBirth;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(unique = true)
    private String email;

    private String phoneNumber;
    private String address;

    @Enumerated(EnumType.STRING)
    private DocumentType documentType;

    private String documentNumber;
    private LocalDate documentIssueDate;
    private LocalDate documentExpiryDate;
    private String documentPhotoPath;

    private boolean isDocumentVerified = false;
    private Instant verificationRequestedAt;
    private Instant verifiedAt;

    @Enumerated(EnumType.STRING)
    private AccountStatus status = AccountStatus.ACTIVE;

    private Instant createdAt = Instant.now();
    private Instant updatedAt = Instant.now();
    private Instant lastLoginAt;

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = Instant.now();
    }
}