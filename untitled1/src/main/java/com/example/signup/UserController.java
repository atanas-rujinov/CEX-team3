package com.example.signup;

import jakarta.validation.Valid;
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

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class UserController {

    private final BCryptPasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final Path fileStorageLocation;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

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
        
        try {
            // Validate if user already exists
            if (userRepository.existsByEmail(email)) {
                return ResponseEntity.badRequest().body("Email already registered");
            }

            if (userRepository.existsByDocumentNumber(documentNumber)) {
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

            return ResponseEntity.ok(response);
        } catch (IOException ex) {
            return ResponseEntity.internalServerError().body("Could not process document photo: " + ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.internalServerError().body("Error during registration: " + ex.getMessage());
        }
    }
}
