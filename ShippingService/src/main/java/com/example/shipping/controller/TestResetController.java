package com.example.shipping.controller;

import com.example.shipping.repository.BusinessRecordRepository;
import com.example.shipping.repository.ProcessedOperationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
public class TestResetController {
    private final BusinessRecordRepository records;
    private final ProcessedOperationRepository operations;
    @Value("${test.reset.enabled:true}") private boolean enabled;

    @PostMapping("/reset")
    public Map<String, Object> reset() {
        if (!enabled) throw new IllegalStateException("Test reset endpoint is disabled");
        records.deleteAllInBatch();
        operations.deleteAllInBatch();
        return Map.of("service", "ShippingService", "reset", true);
    }
}
