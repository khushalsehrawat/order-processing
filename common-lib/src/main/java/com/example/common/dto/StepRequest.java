package com.example.common.dto;

import java.math.BigDecimal;

public record StepRequest(
        String orderId,
        String customerId,
        String productId,
        Integer quantity,
        BigDecimal amount,
        String idempotencyKey,
        String failAt,
        String compFailAt,
        boolean manualRetry
) {
}
