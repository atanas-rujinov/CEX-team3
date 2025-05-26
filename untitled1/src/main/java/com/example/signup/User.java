package com.example.signup;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Personal Data
    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    private String middleName;

    @NotNull
    private LocalDate dateOfBirth;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private String nationality;

    // Contact Information
    @Column(unique = true)
    @NotBlank
    @Email
    private String email;

    private String phoneNumber;
    private String address;

    // Identity Document Data
    @Enumerated(EnumType.STRING)
    private DocumentType documentType;

    @NotBlank
    private String documentNumber;

    @NotNull
    private LocalDate documentIssueDate;

    @NotNull
    private LocalDate documentExpiryDate;

    private String documentPhotoPath;

    // Verification Status
    private boolean isDocumentVerified;
    private Instant verificationRequestedAt;
    private Instant verifiedAt;

    // Metadata
    @NotNull
    private Instant createdAt;
    private Instant updatedAt;
    private Instant lastLoginAt;

    @Enumerated(EnumType.STRING)
    @NotNull
    private AccountStatus status;

    // Security
    @NotBlank
    private String password;

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
        status = AccountStatus.ACTIVE;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }

    // Enums
    public enum Gender {
        MALE, FEMALE
    }

    public enum DocumentType {
        PASSPORT, ID_CARD, DRIVERS_LICENSE
    }

    public enum AccountStatus {
        ACTIVE, BLOCKED
    }
}
