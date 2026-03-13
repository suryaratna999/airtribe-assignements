package com.example.tasktracker.repository;

import com.example.tasktracker.model.Team;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamRepository extends JpaRepository<Team, Long> {
}
