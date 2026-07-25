package com.example.notification.controller;

import com.example.notification.repository.ShippingNotificationRepository;
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
    private final ShippingNotificationRepository notifications;
    @Value("${test.reset.enabled:true}") private boolean enabled;

    @PostMapping("/reset")
    public Map<String, Object> reset() {
        if (!enabled) throw new IllegalStateException("Test reset endpoint is disabled");
        notifications.deleteAllInBatch();
        return Map.of("service", "NotificationService", "reset", true);
    }
}
