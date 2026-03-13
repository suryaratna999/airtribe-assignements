package com.example.tasktracker.controller;

import com.example.tasktracker.dto.TeamRequest;
import com.example.tasktracker.model.Team;
import com.example.tasktracker.model.User;
import com.example.tasktracker.repository.TeamRepository;
import com.example.tasktracker.repository.UserRepository;
import com.example.tasktracker.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.Optional;

@RestController
@RequestMapping("/api/teams")
public class TeamController {
    private final TeamRepository teamRepository;
    private final UserService userService;
    private final UserRepository userRepository;

    public TeamController(TeamRepository teamRepository, UserService userService, UserRepository userRepository) {
        this.teamRepository = teamRepository;
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody TeamRequest req, Authentication auth){
        if(auth == null) return ResponseEntity.status(401).body("unauthorized");
        String sub = (String) auth.getPrincipal();
        Long uid = Long.parseLong(sub);
        User owner = userService.findById(uid).orElse(null);
        if(owner == null) return ResponseEntity.status(404).body("user not found");
        Team t = new Team();
        t.setName(req.getName());
        t.setDescription(req.getDescription());
        t.setOwner(owner);
        t.getMembers().add(owner);
        Team saved = teamRepository.save(t);
        return ResponseEntity.status(201).body(saved);
    }

    @GetMapping
    public ResponseEntity<?> list(){
        return ResponseEntity.ok(teamRepository.findAll());
    }

    @PostMapping("/{id}/members")
    public ResponseEntity<?> addMember(@PathVariable Long id, @RequestParam Long userId){
        Optional<Team> t = teamRepository.findById(id);
        if(t.isEmpty()) return ResponseEntity.status(404).body("team not found");
        Optional<User> u = userRepository.findById(userId);
        if(u.isEmpty()) return ResponseEntity.status(404).body("user not found");
        Team team = t.get();
        team.getMembers().add(u.get());
        teamRepository.save(team);
        return ResponseEntity.ok(team);
    }
}
