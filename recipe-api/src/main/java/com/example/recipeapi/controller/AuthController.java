package com.example.recipeapi.controller;

import com.example.recipeapi.entity.User;
import com.example.recipeapi.repository.UserRepository;
import com.example.recipeapi.security.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public AuthController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String email = body.get("email");
        String password = body.get("password");
        if (username == null || password == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "username and password required"));
        }
        User u = new User();
        u.setUsername(username);
        u.setEmail(email);
        u.setPasswordHash(passwordEncoder.encode(password));
        u.setRoles("USER");
        userRepository.save(u);
        return ResponseEntity.status(201).body(Map.of("id", u.getId(), "username", u.getUsername()));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");
        if (username == null || password == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "username and password required"));
        }
        var opt = userRepository.findByUsername(username);
        if (opt.isEmpty()) {
            return ResponseEntity.status(401).body(Map.of("error", "invalid"));
        }
        User u = opt.get();
        if (!passwordEncoder.matches(password, u.getPasswordHash())) {
            return ResponseEntity.status(401).body(Map.of("error", "invalid"));
        }
        String token = JwtUtil.generateToken(u.getId().toString(), u.getUsername());
        return ResponseEntity.ok(Map.of("token", token));
    }
}
