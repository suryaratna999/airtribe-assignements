package com.airtribe.surya.capstone.chronos.worker;

import com.airtribe.surya.capstone.chronos.entity.JobInstance;
import com.airtribe.surya.capstone.chronos.repository.WorkerRepository;
import com.airtribe.surya.capstone.chronos.service.JobExecutionService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.time.OffsetDateTime;
import java.util.List;

@Component
public class JobPoller {
    private final WorkerRepository repository;
    private final JobExecutionService executionService;

    public JobPoller(WorkerRepository repository, JobExecutionService executionService) {
        this.repository = repository;
        this.executionService = executionService;
    }

    @Scheduled(fixedDelay = 5000)
    public void poll() {
        List<JobInstance> jobs = repository.findJobsToExecute(OffsetDateTime.now(), 5);
        if (!jobs.isEmpty()) {
            System.out.println(">>> Found " + jobs.size() + " jobs.");
            jobs.forEach(executionService::execute);
        }
    }
}