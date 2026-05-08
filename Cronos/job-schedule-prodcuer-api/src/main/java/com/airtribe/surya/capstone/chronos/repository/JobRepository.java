package com.airtribe.surya.capstone.chronos.repository;

import com.airtribe.surya.capstone.chronos.entity.JobInstance;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface JobRepository extends JpaRepository<JobInstance, UUID> {
    // We will add specialized polling queries here in Phase 3
}