package com.example.tasktracker.controller;

import com.example.tasktracker.model.User;
import com.example.tasktracker.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService){ this.userService = userService; }

    @GetMapping("/me")
    public ResponseEntity<?> me(Authentication auth){
        if(auth == null) return ResponseEntity.status(401).body("unauthorized");
        String sub = (String) auth.getPrincipal();
        Long id = Long.parseLong(sub);
        Optional<User> u = userService.findById(id);
        if(u.isEmpty()) return ResponseEntity.status(404).body("not found");
        User safe = u.get();
        safe.setPasswordHash(null);
        return ResponseEntity.ok(safe);
    }
}
