package com.example.signup;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class UserController {

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final ConcurrentHashMap<String, User> userStore = new ConcurrentHashMap<>();

    @PostMapping("/signup")
    public String signup(
            @RequestParam("username") String username,
            @RequestParam("password") String password,
            @RequestParam("firstName") String firstName,
            @RequestParam("lastName") String lastName,
            @RequestParam("photoId") MultipartFile photoId) throws IOException {
        
        if (userStore.containsKey(username)) {
            return "Username already exists.";
        }

        if (photoId.isEmpty()) {
            return "Photo ID is required.";
        }

        String encryptedPassword = passwordEncoder.encode(password);
        User newUser = new User(username, encryptedPassword, firstName, lastName, photoId.getBytes());
        userStore.put(username, newUser);

        return "User registered successfully.";
    }
}
