package com.example.payment.controller;
import com.example.common.dto.*;
import com.example.payment.service.PaymentActionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentActionService service;

    @PostMapping("/charge")
    public StepResponse charge(@RequestBody StepRequest request) {
        return service.process(request, "CHARGE", false);
    }
    @PostMapping("/refund")
    public StepResponse refund(@RequestBody StepRequest request) {
        return service.process(request, "REFUND", true);
    }
}
