package com.example.signup;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
//import lombok.Data;
//import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDate;

//@Data
//@NoArgsConstructor
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

    public User() {}

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
        status = AccountStatus.ACTIVE;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getMiddleName() { return middleName; }
    public void setMiddleName(String middleName) { this.middleName = middleName; }

    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public Gender getGender() { return gender; }
    public void setGender(Gender gender) { this.gender = gender; }

    public String getNationality() { return nationality; }
    public void setNationality(String nationality) { this.nationality = nationality; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public DocumentType getDocumentType() { return documentType; }
    public void setDocumentType(DocumentType documentType) { this.documentType = documentType; }

    public String getDocumentNumber() { return documentNumber; }
    public void setDocumentNumber(String documentNumber) { this.documentNumber = documentNumber; }

    public LocalDate getDocumentIssueDate() { return documentIssueDate; }
    public void setDocumentIssueDate(LocalDate documentIssueDate) { this.documentIssueDate = documentIssueDate; }

    public LocalDate getDocumentExpiryDate() { return documentExpiryDate; }
    public void setDocumentExpiryDate(LocalDate documentExpiryDate) { this.documentExpiryDate = documentExpiryDate; }

    public String getDocumentPhotoPath() { return documentPhotoPath; }
    public void setDocumentPhotoPath(String documentPhotoPath) { this.documentPhotoPath = documentPhotoPath; }

    public boolean isDocumentVerified() { return isDocumentVerified; }
    public void setDocumentVerified(boolean documentVerified) { isDocumentVerified = documentVerified; }

    public Instant getVerificationRequestedAt() { return verificationRequestedAt; }
    public void setVerificationRequestedAt(Instant verificationRequestedAt) { this.verificationRequestedAt = verificationRequestedAt; }

    public Instant getVerifiedAt() { return verifiedAt; }
    public void setVerifiedAt(Instant verifiedAt) { this.verifiedAt = verifiedAt; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }

    public Instant getLastLoginAt() { return lastLoginAt; }
    public void setLastLoginAt(Instant lastLoginAt) { this.lastLoginAt = lastLoginAt; }

    public AccountStatus getStatus() { return status; }
    public void setStatus(AccountStatus status) { this.status = status; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

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
