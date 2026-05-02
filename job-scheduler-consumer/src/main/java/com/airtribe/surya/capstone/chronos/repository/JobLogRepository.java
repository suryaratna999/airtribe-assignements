package com.airtribe.surya.capstone.chronos.repository;

import com.airtribe.surya.capstone.chronos.entity.JobLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JobLogRepository extends JpaRepository<JobLog, Long> {}