package com.example.coordinator.controller;

import com.example.coordinator.repository.OrderEventRepository;
import com.example.coordinator.repository.OrderStepRepository;
import com.example.coordinator.repository.OrderWorkflowRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
public class TestResetController {
    private final OrderEventRepository events;
    private final OrderStepRepository steps;
    private final OrderWorkflowRepository orders;
    private final RestClient client;

    @Value("${test.reset.enabled:true}") private boolean enabled;
    @Value("${services.order.url}") private String orderUrl;
    @Value("${services.inventory.url}") private String inventoryUrl;
    @Value("${services.payment.url}") private String paymentUrl;
    @Value("${services.shipping.url}") private String shippingUrl;
    @Value("${services.notification.url:http://localhost:8085}") private String notificationUrl;

    @PostMapping("/reset")
    public Map<String, String> reset(@RequestParam(defaultValue = "true") boolean includeServices) {
        if (!enabled) throw new IllegalStateException("Test reset endpoint is disabled");
        Map<String, String> result = new LinkedHashMap<>();
        events.deleteAllInBatch();
        steps.deleteAllInBatch();
        orders.deleteAllInBatch();
        result.put("CoordinatorService", "reset");

        if (includeServices) {
            resetRemote("OrderService", orderUrl, result);
            resetRemote("InventoryService", inventoryUrl, result);
            resetRemote("PaymentService", paymentUrl, result);
            resetRemote("ShippingService", shippingUrl, result);
            resetRemote("NotificationService", notificationUrl, result);
        }
        return result;
    }

    private void resetRemote(String service, String baseUrl, Map<String, String> result) {
        try {
            client.post().uri(baseUrl + "/api/test/reset").retrieve().toBodilessEntity();
            result.put(service, "reset");
        } catch (RuntimeException e) {
            result.put(service, "not reset: " + rootMessage(e));
        }
    }

    private static String rootMessage(Throwable e) {
        Throwable t = e;
        while (t.getCause() != null) t = t.getCause();
        String message = String.valueOf(t.getMessage());
        return message.length() > 160 ? message.substring(0, 160) : message;
    }
}
