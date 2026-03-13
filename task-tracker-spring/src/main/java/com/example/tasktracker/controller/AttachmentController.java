package com.example.tasktracker.controller;

import com.example.tasktracker.model.Attachment;
import com.example.tasktracker.model.Task;
import com.example.tasktracker.model.User;
import com.example.tasktracker.repository.AttachmentRepository;
import com.example.tasktracker.repository.TaskRepository;
import com.example.tasktracker.service.AttachmentService;
import com.example.tasktracker.service.UserService;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

@RestController
@RequestMapping("/api/tasks/{taskId}/attachments")
public class AttachmentController {
    private final AttachmentService attachmentService;
    private final TaskRepository taskRepository;
    private final UserService userService;
    private final AttachmentRepository attachmentRepository;

    public AttachmentController(AttachmentService attachmentService, TaskRepository taskRepository, UserService userService, AttachmentRepository attachmentRepository) {
        this.attachmentService = attachmentService;
        this.taskRepository = taskRepository;
        this.userService = userService;
        this.attachmentRepository = attachmentRepository;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> upload(@PathVariable Long taskId, @RequestPart("file") MultipartFile file, Authentication auth) throws IOException{
        if(auth == null) return ResponseEntity.status(401).body("unauthorized");
        Optional<Task> t = taskRepository.findById(taskId);
        if(t.isEmpty()) return ResponseEntity.status(404).body("task not found");
        Long uid = Long.parseLong((String)auth.getPrincipal());
        User u = userService.findById(uid).orElse(null);
        if(u == null) return ResponseEntity.status(404).body("user not found");
        Attachment a = attachmentService.store(file, t.get(), u);
        return ResponseEntity.status(201).body(a);
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<?> download(@PathVariable Long taskId, @PathVariable Long id){
        Optional<Attachment> a = attachmentRepository.findById(id);
        if(a.isEmpty()) return ResponseEntity.status(404).body("attachment not found");
        File f = new File(a.get().getPath());
        if(!f.exists()) return ResponseEntity.status(404).body("file not found");
        FileSystemResource resource = new FileSystemResource(f);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + a.get().getFilename() + "\"")
                .contentLength(a.get().getSize())
                .contentType(MediaType.parseMediaType(a.get().getMimeType() != null ? a.get().getMimeType() : "application/octet-stream"))
                .body(resource);
    }
}
