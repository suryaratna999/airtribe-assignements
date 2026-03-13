package com.example.tasktracker.controller;

import com.example.tasktracker.dto.CommentRequest;
import com.example.tasktracker.model.Comment;
import com.example.tasktracker.model.Task;
import com.example.tasktracker.model.User;
import com.example.tasktracker.repository.CommentRepository;
import com.example.tasktracker.repository.TaskRepository;
import com.example.tasktracker.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/tasks/{taskId}/comments")
public class CommentController {
    private final CommentRepository commentRepository;
    private final TaskRepository taskRepository;
    private final UserService userService;

    public CommentController(CommentRepository commentRepository, TaskRepository taskRepository, UserService userService) {
        this.commentRepository = commentRepository;
        this.taskRepository = taskRepository;
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<?> create(@PathVariable Long taskId, @Valid @RequestBody CommentRequest req, Authentication auth){
        if(auth == null) return ResponseEntity.status(401).body("unauthorized");
        Optional<Task> t = taskRepository.findById(taskId);
        if(t.isEmpty()) return ResponseEntity.status(404).body("task not found");
        String sub = (String) auth.getPrincipal();
        Long uid = Long.parseLong(sub);
        User u = userService.findById(uid).orElse(null);
        if(u == null) return ResponseEntity.status(404).body("user not found");
        Comment c = new Comment();
        c.setTask(t.get());
        c.setAuthor(u);
        c.setContent(req.getContent());
        Comment saved = commentRepository.save(c);
        return ResponseEntity.status(201).body(saved);
    }

    @GetMapping
    public ResponseEntity<List<Comment>> list(@PathVariable Long taskId){
        return ResponseEntity.ok(commentRepository.findByTaskId(taskId));
    }
}
