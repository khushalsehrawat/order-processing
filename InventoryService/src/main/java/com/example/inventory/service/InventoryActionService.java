package com.example.inventory.service;
import com.example.common.dto.*;
import com.example.inventory.entity.*;
import com.example.inventory.repository.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;

@Service
@RequiredArgsConstructor
public class InventoryActionService {
    private final ProcessedOperationRepository operations;
    private final BusinessRecordRepository records;
    private final ObjectMapper mapper;

    @Transactional
    public StepResponse process(StepRequest request, String operation, boolean compensation) {
        var existing = operations.findByIdempotencyKey(request.idempotencyKey());
        if (existing.isPresent() && "SUCCEEDED".equals(existing.get().getStatus())) {
            try {
                StepResponse saved = mapper.readValue(existing.get().getResponseJson(), StepResponse.class);
                return new StepResponse(saved.orderId(), saved.operation(), true, true, saved.message(), saved.processedAt());
            } catch (Exception e) { throw new IllegalStateException(e); }
        }

        ProcessedOperation op;
        if (existing.isPresent()) {
            // Reuse a previously failed operation. This is required for retries and
            // for the manual compensation retry from NEEDS_ATTENTION.
            op = existing.get();
            if ("STARTED".equals(op.getStatus()) && op.getUpdatedAt() != null
                    && op.getUpdatedAt().isAfter(Instant.now().minusSeconds(30))) {
                throw new IllegalStateException("Operation is already being processed; retry with the same key");
            }
            op.setStatus("STARTED");
            op.setUpdatedAt(Instant.now());
            operations.saveAndFlush(op);
        } else {
            op = new ProcessedOperation();
            op.setIdempotencyKey(request.idempotencyKey());
            op.setOrderId(request.orderId());
            op.setOperationName(operation);
            op.setStatus("STARTED");
            op.setCreatedAt(Instant.now());
            op.setUpdatedAt(Instant.now());
            try {
                operations.saveAndFlush(op);
            } catch (DataIntegrityViolationException race) {
                ProcessedOperation winner = operations.findByIdempotencyKey(request.idempotencyKey()).orElseThrow();
                if ("SUCCEEDED".equals(winner.getStatus()) && winner.getResponseJson() != null) {
                    try {
                        StepResponse saved = mapper.readValue(winner.getResponseJson(), StepResponse.class);
                        return new StepResponse(saved.orderId(), saved.operation(), true, true, saved.message(), saved.processedAt());
                    } catch (Exception e) { throw new IllegalStateException(e); }
                }
                throw new IllegalStateException("Operation is already being processed; retry with the same key");
            }
        }

        if (!compensation && "INVENTORY".equalsIgnoreCase(request.failAt())) {
            op.setStatus("FAILED"); op.setUpdatedAt(Instant.now()); operations.save(op);
            throw new IllegalStateException("Simulated INVENTORY execution failure");
        }
        if (compensation && !request.manualRetry() && "INVENTORY".equalsIgnoreCase(request.compFailAt())) {
            op.setStatus("FAILED"); op.setUpdatedAt(Instant.now()); operations.save(op);
            throw new IllegalStateException("Simulated INVENTORY compensation failure");
        }

        if (!records.existsByOrderIdAndOperationName(request.orderId(), operation)) {
            BusinessRecord record = new BusinessRecord();
            record.setOrderId(request.orderId());
            record.setOperationName(operation);
            record.setState("DONE");
            record.setCreatedAt(Instant.now());
            records.save(record);
        }

        StepResponse response = new StepResponse(request.orderId(), operation, true, false,
                operation + " completed", Instant.now());
        try {
            op.setStatus("SUCCEEDED");
            op.setResponseJson(mapper.writeValueAsString(response));
            op.setUpdatedAt(Instant.now());
            operations.save(op);
            return response;
        } catch (Exception e) { throw new IllegalStateException(e); }
    }
}
