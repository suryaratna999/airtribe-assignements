package com.example.tasktracker.controller;

import com.example.tasktracker.dto.TaskRequest;
import com.example.tasktracker.model.Task;
import com.example.tasktracker.model.User;
import com.example.tasktracker.repository.TaskRepository;
import com.example.tasktracker.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {
    private final TaskRepository taskRepository;
    private final UserService userService;

    public TaskController(TaskRepository taskRepository, UserService userService) {
        this.taskRepository = taskRepository;
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<Task>> list(@RequestParam(required = false) String q, @RequestParam(required = false) String status) {
        // Basic filtering: if q provided search title/description; if status provided filter
        if(q != null && !q.isBlank()){
            return ResponseEntity.ok(taskRepository.findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(q,q));
        }
        if(status != null && !status.isBlank()){
            return ResponseEntity.ok(taskRepository.findByStatus(status));
        }
        return ResponseEntity.ok(taskRepository.findAll());
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody TaskRequest req, Authentication auth){
        if(auth == null) return ResponseEntity.status(401).body("unauthorized");
        String sub = (String) auth.getPrincipal();
        Long id = Long.parseLong(sub);
        Optional<User> reporter = userService.findById(id);
        if(reporter.isEmpty()) return ResponseEntity.status(404).body("reporter not found");
        Task t = new Task();
        t.setTitle(req.getTitle());
        t.setDescription(req.getDescription());
        t.setPriority(req.getPriority());
        t.setDueDate(req.getDueDate());
        t.setReporter(reporter.get());
        if(req.getAssigneeId() != null){
            userService.findById(req.getAssigneeId()).ifPresent(t::setAssignee);
        }
        if(req.getTeamId() != null){
            // lightweight: set team by id only if exists
            com.example.tasktracker.repository.TeamRepository tr = null;
        }
        Task saved = taskRepository.save(t);
        return ResponseEntity.status(201).body(saved);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable Long id){
        Optional<Task> t = taskRepository.findById(id);
        if(t.isEmpty()) return ResponseEntity.status(404).body("not found");
        return ResponseEntity.ok(t.get());
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> patch(@PathVariable Long id, @RequestBody TaskRequest req){
        Optional<Task> t = taskRepository.findById(id);
        if(t.isEmpty()) return ResponseEntity.status(404).body("not found");
        Task existing = t.get();
        if(req.getTitle() != null) existing.setTitle(req.getTitle());
        if(req.getDescription() != null) existing.setDescription(req.getDescription());
        if(req.getPriority() != null) existing.setPriority(req.getPriority());
        if(req.getDueDate() != null) existing.setDueDate(req.getDueDate());
        taskRepository.save(existing);
        return ResponseEntity.ok(existing);
    }
}
