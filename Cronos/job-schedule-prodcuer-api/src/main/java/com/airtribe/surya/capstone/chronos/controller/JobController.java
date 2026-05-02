package com.airtribe.surya.capstone.chronos.controller;

import com.airtribe.surya.capstone.chronos.dto.JobRequest;
import com.airtribe.surya.capstone.chronos.entity.JobInstance;
import com.airtribe.surya.capstone.chronos.service.JobService;
import jakarta.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/jobs")
public class JobController {

    private static final Logger log = LoggerFactory.getLogger(JobController.class);

    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public JobInstance createJob(@Valid @RequestBody JobRequest request) {
        log.debug("Received job request: {}", request);
        return jobService.scheduleJob(request);
    }

    @GetMapping
    public List<JobInstance> getAllJobs() {
        log.debug("Fetching all jobs");
        return jobService.getAllJobs();
    }

    @GetMapping("/{jobId}")
    public JobInstance getJobById(@PathVariable UUID jobId) {
        log.debug("Fetching job with ID: {}", jobId);
        return jobService.getJobById(jobId);
    }
}