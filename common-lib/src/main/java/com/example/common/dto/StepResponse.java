package com.example.common.dto;

import java.time.Instant;

public record StepResponse(
        String orderId,
        String operation,
        boolean success,
        boolean idempotent,
        String message,
        Instant processedAt
) {
}
