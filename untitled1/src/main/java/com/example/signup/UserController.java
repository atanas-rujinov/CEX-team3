package com.example.signup;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import com.example.signup.exception.UserException;
import com.example.signup.exception.FileUploadException;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private static final int MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB
    private static final String[] ALLOWED_FILE_TYPES = {"image/jpeg", "image/png", "image/jpg"};

    private final BCryptPasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final Path fileStorageLocation;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final ConcurrentHashMap<String, User> userStore = new ConcurrentHashMap<>();

    @Autowired
    public UserController(UserRepository userRepository, 
                        AuthenticationManager authenticationManager,
                        JwtTokenProvider jwtTokenProvider) {
        this.passwordEncoder = new BCryptPasswordEncoder();
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.fileStorageLocation = Paths.get("uploads/documents").toAbsolutePath().normalize();
        
        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (IOException ex) {
            throw new RuntimeException("Could not create the directory where the uploaded files will be stored.", ex);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestParam("email") String email,
                                 @RequestParam("password") String password) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
            );

            String token = jwtTokenProvider.generateToken(email);
            
            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("email", email);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Invalid email or password");
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            @RequestParam("firstName") String firstName,
            @RequestParam("lastName") String lastName,
            @RequestParam(value = "middleName", required = false) String middleName,
            @RequestParam("dateOfBirth") LocalDate dateOfBirth,
            @RequestParam(value = "gender", required = false) User.Gender gender,
            @RequestParam(value = "nationality", required = false) String nationality,
            @RequestParam(value = "phoneNumber", required = false) String phoneNumber,
            @RequestParam(value = "address", required = false) String address,
            @RequestParam("documentType") User.DocumentType documentType,
            @RequestParam("documentNumber") String documentNumber,
            @RequestParam("documentIssueDate") LocalDate documentIssueDate,
            @RequestParam("documentExpiryDate") LocalDate documentExpiryDate,
            @RequestParam("documentPhoto") MultipartFile documentPhoto) {
        
        logger.info("Received signup request for email: {}", email);
        
        try {
            validateInput(email, password, firstName, lastName, documentPhoto);
            
            // Validate if user already exists
            if (userRepository.existsByEmail(email)) {
                logger.error("Email already registered: {}", email);
                return ResponseEntity.badRequest().body("Email already registered");
            }

            if (userRepository.existsByDocumentNumber(documentNumber)) {
                logger.error("Document number already registered: {}", documentNumber);
                return ResponseEntity.badRequest().body("Document number already registered");
            }

            // Handle document photo upload
            String fileName = UUID.randomUUID().toString() + "_" + documentPhoto.getOriginalFilename();
            Path targetLocation = fileStorageLocation.resolve(fileName);
            Files.copy(documentPhoto.getInputStream(), targetLocation);

            // Create new user
            User user = new User();
            user.setEmail(email);
            user.setPassword(passwordEncoder.encode(password));
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setMiddleName(middleName);
            user.setDateOfBirth(dateOfBirth);
            user.setGender(gender);
            user.setNationality(nationality);
            user.setPhoneNumber(phoneNumber);
            user.setAddress(address);
            user.setDocumentType(documentType);
            user.setDocumentNumber(documentNumber);
            user.setDocumentIssueDate(documentIssueDate);
            user.setDocumentExpiryDate(documentExpiryDate);
            user.setDocumentPhotoPath(fileName);
            user.setVerificationRequestedAt(Instant.now());
            user.setDocumentVerified(false);

            userRepository.save(user);

            // Generate JWT token for the new user
            String token = jwtTokenProvider.generateToken(email);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "User registered successfully");
            response.put("token", token);
            response.put("email", email);

            logger.info("Successfully registered user: {}", email);
            return ResponseEntity.ok(response);
        } catch (IOException ex) {
            logger.error("Could not process document photo: {}", ex.getMessage());
            return ResponseEntity.internalServerError().body("Could not process document photo: " + ex.getMessage());
        } catch (Exception ex) {
            logger.error("Error during registration: {}", ex.getMessage());
            return ResponseEntity.internalServerError().body("Error during registration: " + ex.getMessage());
        }
    }

    private void validateInput(String email, String password, String firstName, 
                             String lastName, MultipartFile documentPhoto) {
        // Email validation
        if (email == null || email.trim().isEmpty()) {
            logger.error("Email validation failed: empty email");
            throw new UserException("Email cannot be empty");
        }
        if (email.length() < 3 || email.length() > 50) {
            logger.error("Email validation failed: length not between 3 and 50 characters");
            throw new UserException("Email must be between 3 and 50 characters");
        }
        
        // Password validation
        if (password == null || password.trim().isEmpty()) {
            logger.error("Password validation failed: empty password");
            throw new UserException("Password cannot be empty");
        }
        if (password.length() < 8) {
            logger.error("Password validation failed: length less than 8 characters");
            throw new UserException("Password must be at least 8 characters long");
        }
        
        // Name validation
        if (firstName == null || firstName.trim().isEmpty()) {
            logger.error("First name validation failed: empty first name");
            throw new UserException("First name cannot be empty");
        }
        if (lastName == null || lastName.trim().isEmpty()) {
            logger.error("Last name validation failed: empty last name");
            throw new UserException("Last name cannot be empty");
        }

        // Email existence check
        if (userStore.containsKey(email)) {
            logger.error("Email already exists: {}", email);
            throw new UserException("Email already exists");
        }

        // Document photo validation
        if (documentPhoto.isEmpty()) {
            logger.error("Document photo validation failed: empty document photo");
            throw new FileUploadException("Document photo is required");
        }

        if (documentPhoto.getSize() > MAX_FILE_SIZE) {
            logger.error("Document photo validation failed: size exceeds limit - {} bytes", documentPhoto.getSize());
            throw new FileUploadException("Document photo size exceeds maximum limit of 5MB");
        }

        String contentType = documentPhoto.getContentType();
        if (contentType == null || !isAllowedFileType(contentType)) {
            logger.error("Document photo validation failed: invalid file type - {}", contentType);
            throw new FileUploadException("Only JPG, JPEG, and PNG files are allowed");
        }
    }

    private boolean isAllowedFileType(String contentType) {
        for (String type : ALLOWED_FILE_TYPES) {
            if (type.equals(contentType)) {
                return true;
            }
        }
        return false;
    }
}
