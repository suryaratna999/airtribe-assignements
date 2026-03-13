package com.example.tasktracker.controller;

import com.example.tasktracker.dto.LoginRequest;
import com.example.tasktracker.dto.RegisterRequest;
import com.example.tasktracker.model.User;
import com.example.tasktracker.repository.UserRepository;
import com.example.tasktracker.security.JwtUtil;
import com.example.tasktracker.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final UserRepository userRepository;

    public AuthController(UserService userService, UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest payload){
        String email = payload.getEmail();
        String password = payload.getPassword();
        String name = payload.getName();
        if(userRepository.findByEmail(email).isPresent()) return ResponseEntity.status(409).body("Email taken");
        User u = userService.register(email, password, name);
        u.setPasswordHash(null);
        String token = JwtUtil.generateToken(u.getId().toString(), u.getRole());
        return ResponseEntity.ok(Map.of("user", u, "token", token));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest payload){
        String email = payload.getEmail();
        String password = payload.getPassword();
        Optional<User> u = userService.findByEmail(email);
        if(u.isEmpty()) return ResponseEntity.status(401).body("Invalid credentials");
        if(!org.springframework.security.crypto.bcrypt.BCrypt.checkpw(password, u.get().getPasswordHash())) return ResponseEntity.status(401).body("Invalid credentials");
        User safe = u.get();
        safe.setPasswordHash(null);
        String token = JwtUtil.generateToken(safe.getId().toString(), safe.getRole());
        return ResponseEntity.ok(Map.of("user", safe, "token", token));
    }
}
