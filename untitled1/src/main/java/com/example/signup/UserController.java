package com.example.signup;

import com.example.signup.model.User;
import com.example.signup.payload.SignupRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api")
public class UserController {

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final ConcurrentHashMap<String, User> userStore = new ConcurrentHashMap<>();

    @PostMapping("/signup")
    public String signup(@RequestBody SignupRequest request) {
        if (userStore.containsKey(request.getUsername())) {
            return "Username already exists.";
        }

        String encryptedPassword = passwordEncoder.encode(request.getPassword());
        User newUser = new User(request.getUsername(), encryptedPassword);
        userStore.put(request.getUsername(), newUser);

        return "User registered successfully.";
    }
}
