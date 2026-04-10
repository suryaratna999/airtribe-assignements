package com.example.recipeapi.repository;

import com.example.recipeapi.entity.RecipeImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecipeImageRepository extends JpaRepository<RecipeImage, Long> {
    List<RecipeImage> findByRecipeId(Long recipeId);
}
