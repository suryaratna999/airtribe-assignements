package com.airtribe.surya.capstone.chronos.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.OffsetDateTime;

public record JobRequest(
        @NotBlank(message = "Name is required") String name,
        @NotNull(message = "Payload is required") Object payload,
        @Future(message = "must be a future date") OffsetDateTime scheduledAt,
        int maxRetries,
        String recurrenceInterval
) {}