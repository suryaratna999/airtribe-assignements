package com.airtribe.surya.capstone.chronos.service;

import com.airtribe.surya.capstone.chronos.dto.JobRequest;
import com.airtribe.surya.capstone.chronos.entity.JobInstance;
import com.airtribe.surya.capstone.chronos.entity.JobStatus;
import com.airtribe.surya.capstone.chronos.repository.JobRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

@Service
public class JobService {

    private final JobRepository jobRepository;
    private final ObjectMapper objectMapper;

    public JobService(JobRepository jobRepository, ObjectMapper objectMapper) {
        this.jobRepository = jobRepository;
        this.objectMapper = objectMapper;
    }

    public JobInstance scheduleJob(JobRequest request) {
        final String jsonPayload;
        try {
            jsonPayload = objectMapper.writeValueAsString(request.payload());
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Failed to serialize job payload", e);
        }

        JobInstance job = new JobInstance();
        job.setJobId(UUID.randomUUID());
        job.setName(request.name());
        job.setPayload(jsonPayload);
        job.setScheduledAt(request.scheduledAt());
        job.setStatus(JobStatus.PENDING);

        // Phase 4: Mapping Retry and Recurrence data
        job.setMaxRetries(request.maxRetries());
        job.setRecurrenceInterval(request.recurrenceInterval());
        job.setRetryCount(0);

        return jobRepository.save(job);
    }

    public List<JobInstance> getAllJobs() {
        return jobRepository.findAll();
    }

    // --- FIX: The missing method ---
    public JobInstance getJobById(UUID jobId) {
        return jobRepository.findById(jobId)
                .orElseThrow(() -> new IllegalArgumentException("Job not found with ID: " + jobId));
    }

    public void deleteJob(UUID jobId) {
        if (!jobRepository.existsById(jobId)) {
            throw new IllegalArgumentException("Cannot delete: Job not found with ID: " + jobId);
        }
        jobRepository.deleteById(jobId);
    }
}