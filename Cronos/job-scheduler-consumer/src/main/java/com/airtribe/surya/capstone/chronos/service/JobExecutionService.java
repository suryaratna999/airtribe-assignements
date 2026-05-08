package com.airtribe.surya.capstone.chronos.service;

import com.airtribe.surya.capstone.chronos.entity.JobInstance;
import com.airtribe.surya.capstone.chronos.entity.JobLog;
import com.airtribe.surya.capstone.chronos.entity.JobStatus;
import com.airtribe.surya.capstone.chronos.repository.JobLogRepository;
import com.airtribe.surya.capstone.chronos.repository.WorkerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.OffsetDateTime;

@Service
public class JobExecutionService {
    private final WorkerRepository repository;
    private final JobLogRepository logRepository; // NEW

    public JobExecutionService(WorkerRepository repository, JobLogRepository logRepository) {
        this.repository = repository;
        this.logRepository = logRepository;
    }

    @Transactional
    public void execute(JobInstance job) {
        OffsetDateTime startTime = OffsetDateTime.now();
        // Create initial log entry
        JobLog log = new JobLog(job.getJobId(), startTime, JobStatus.RUNNING, "Starting execution");

        try {
            job.setStatus(JobStatus.RUNNING);
            repository.saveAndFlush(job);

            System.out.println(">>> [WORKER] Executing: " + job.getName());

            if (job.getPayload().contains("fail")) {
                throw new RuntimeException("Simulated business logic failure");
            }
            Thread.sleep(2000);

            if (job.getRecurrenceInterval() != null && !job.getRecurrenceInterval().isEmpty()) {
                handleRecurrence(job);
                log.setStatus(JobStatus.PENDING);
                log.setMessage("Success - Recurring job rescheduled.");
            } else {
                job.setStatus(JobStatus.COMPLETED);
                log.setStatus(JobStatus.COMPLETED);
                log.setMessage("Execution finished successfully.");
            }

        } catch (Exception e) {
            handleFailure(job, e);
            log.setStatus(job.getStatus());
            log.setMessage("Attempt " + job.getRetryCount() + " failed: " + e.getMessage());
        } finally {
            log.setExecutionEnd(OffsetDateTime.now());
            logRepository.save(log); // Save log history
            repository.save(job);
        }
    }

    private void handleFailure(JobInstance job, Exception e) {
        if (job.getRetryCount() < job.getMaxRetries()) {
            job.setRetryCount(job.getRetryCount() + 1);
            job.setStatus(JobStatus.PENDING);
            int delayMinutes = 5 * job.getRetryCount();
            job.setScheduledAt(OffsetDateTime.now().plusMinutes(delayMinutes));
        } else {
            job.setStatus(JobStatus.FAILED);
        }
    }

    private void handleRecurrence(JobInstance job) {
        job.setStatus(JobStatus.PENDING);
        job.setRetryCount(0);
        String interval = job.getRecurrenceInterval().toUpperCase();
        OffsetDateTime nextRun = switch (interval) {
            case "HOURLY" -> job.getScheduledAt().plusHours(1);
            case "DAILY" -> job.getScheduledAt().plusDays(1);
            case "WEEKLY" -> job.getScheduledAt().plusWeeks(1);
            case "MONTHLY" -> job.getScheduledAt().plusMonths(1);
            default -> job.getScheduledAt().plusMinutes(30);
        };
        job.setScheduledAt(nextRun);
    }
}