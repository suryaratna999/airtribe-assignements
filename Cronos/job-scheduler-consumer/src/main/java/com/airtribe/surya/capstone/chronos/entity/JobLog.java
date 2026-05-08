package com.airtribe.surya.capstone.chronos.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "job_logs")
public class JobLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "log_id")
    private Long logId;

    @Column(name = "job_id")
    private UUID jobId;

    @Column(name = "execution_start")
    private OffsetDateTime executionStart;

    @Column(name = "execution_end")
    private OffsetDateTime executionEnd;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "status", columnDefinition = "job_status")
    private JobStatus status;

    @Column(name = "message")
    private String message;

    @Column(name = "worker_id")
    private String workerId;

    public JobLog() {}

    public JobLog(UUID jobId, OffsetDateTime start, JobStatus status, String message) {
        this.jobId = jobId;
        this.executionStart = start;
        this.status = status;
        this.message = message;
        this.workerId = "Dasaris-MacBook-Air";
    }

    public Long getLogId() { return logId; }
    public void setLogId(Long logId) { this.logId = logId; }
    public UUID getJobId() { return jobId; }
    public void setJobId(UUID jobId) { this.jobId = jobId; }
    public OffsetDateTime getExecutionStart() { return executionStart; }
    public void setExecutionStart(OffsetDateTime executionStart) { this.executionStart = executionStart; }
    public OffsetDateTime getExecutionEnd() { return executionEnd; }
    public void setExecutionEnd(OffsetDateTime executionEnd) { this.executionEnd = executionEnd; }
    public JobStatus getStatus() { return status; }
    public void setStatus(JobStatus status) { this.status = status; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public String getWorkerId() { return workerId; }
    public void setWorkerId(String workerId) { this.workerId = workerId; }
}