package com.example.tasktracker.service;

import com.example.tasktracker.model.Attachment;
import com.example.tasktracker.model.Task;
import com.example.tasktracker.model.User;
import com.example.tasktracker.repository.AttachmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class AttachmentService {
    private final AttachmentRepository repo;
    private final String uploadDir = System.getProperty("user.dir") + File.separator + "uploads";

    public AttachmentService(AttachmentRepository repo) {
        this.repo = repo;
        File d = new File(uploadDir);
        if(!d.exists()) d.mkdirs();
    }

    public Attachment store(MultipartFile file, Task task, User uploader) throws IOException {
        String id = UUID.randomUUID().toString();
        String filename = id + "_" + file.getOriginalFilename();
        File out = new File(uploadDir, filename);
        Files.copy(file.getInputStream(), out.toPath(), StandardCopyOption.REPLACE_EXISTING);
        Attachment a = new Attachment();
        a.setTask(task);
        a.setUploader(uploader);
        a.setFilename(file.getOriginalFilename());
        a.setMimeType(file.getContentType());
        a.setPath(out.getAbsolutePath());
        a.setSize(file.getSize());
        a.setCreatedAt(LocalDateTime.now());
        return repo.save(a);
    }
}
