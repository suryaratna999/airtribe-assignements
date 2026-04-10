package com.example.recipeapi.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "recipe_images")
public class RecipeImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long recipeId;

    @Column(nullable = false)
    private String filename; // original filename stored

    @Column
    private String resizedFilename;

    @Column
    private String contentType;

    @Column
    private Long size;

    @Column
    private Instant createdAt = Instant.now();

    // getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getRecipeId() { return recipeId; }
    public void setRecipeId(Long recipeId) { this.recipeId = recipeId; }

    public String getFilename() { return filename; }
    public void setFilename(String filename) { this.filename = filename; }

    public String getResizedFilename() { return resizedFilename; }
    public void setResizedFilename(String resizedFilename) { this.resizedFilename = resizedFilename; }

    public String getContentType() { return contentType; }
    public void setContentType(String contentType) { this.contentType = contentType; }

    public Long getSize() { return size; }
    public void setSize(Long size) { this.size = size; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
