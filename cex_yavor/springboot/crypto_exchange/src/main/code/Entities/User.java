import jakarta.persistence.*;
import java.time.Instant;
import java.time.LocalDate;

@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String password;
    private boolean banned;

    // Лични данни
    private String firstName;
    private String lastName;
    private String middleName;
    private LocalDate dateOfBirth;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private String nationality;

    // Контактни данни
    private String email;
    private String phoneNumber;
    private String address;

    // Данни за документ за самоличност
    @Enumerated(EnumType.STRING)
    private DocumentType documentType;

    private String documentNumber;
    private LocalDate documentIssueDate;
    private LocalDate documentExpiryDate;
    private String documentPhotoPath;

    // Статус на верификация
    private boolean isDocumentVerified;
    private Instant verificationRequestedAt;
    private Instant verifiedAt;

    // Мета-данни
    private Instant createdAt;
    private Instant updatedAt;
    private Instant lastLoginAt;

    @Enumerated(EnumType.STRING)
    private UserStatus status;

    public User() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public boolean isBanned() { return banned; }
    public void setBanned(boolean banned) { this.banned = banned; }

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

    public UserStatus getStatus() { return status; }
    public void setStatus(UserStatus status) { this.status = status; }
}
