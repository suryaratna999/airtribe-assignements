package com.airtribe.surya.capstone.chronos.repository;

import com.airtribe.surya.capstone.chronos.entity.JobInstance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public interface WorkerRepository extends JpaRepository<JobInstance, UUID> {

    @Query(value = "SELECT * FROM jobs j " +
            "WHERE j.status = 'PENDING' AND j.scheduled_at <= :now " +
            "ORDER BY j.scheduled_at ASC " +
            "LIMIT :limit FOR UPDATE SKIP LOCKED",
            nativeQuery = true)
    List<JobInstance> findJobsToExecute(@Param("now") OffsetDateTime now, @Param("limit") int limit);
}