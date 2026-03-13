package com.example.tasktracker.service;

import com.example.tasktracker.model.User;
import com.example.tasktracker.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    private final UserRepository repo;

    public UserService(UserRepository repo){ this.repo = repo; }

    public User register(String email, String rawPassword, String name){
        String hashed = BCrypt.hashpw(rawPassword, BCrypt.gensalt(10));
        User u = new User();
        u.setEmail(email);
        u.setPasswordHash(hashed);
        u.setName(name);
        return repo.save(u);
    }

    public Optional<User> findByEmail(String email){ return repo.findByEmail(email); }

    public Optional<User> findById(Long id){ return repo.findById(id); }
}
