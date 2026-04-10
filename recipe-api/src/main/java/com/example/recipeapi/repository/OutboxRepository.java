package com.example.recipeapi.repository;

import com.example.recipeapi.entity.OutboxMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OutboxRepository extends JpaRepository<OutboxMessage, String> {
    List<OutboxMessage> findByStatus(String status);
}
