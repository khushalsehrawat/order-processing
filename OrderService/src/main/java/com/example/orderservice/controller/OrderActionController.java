package com.example.orderservice.controller;
import com.example.common.dto.*;
import com.example.orderservice.service.OrderActionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderActionController {
    private final OrderActionService service;

    @PostMapping("/create")
    public StepResponse create(@RequestBody StepRequest request) {
        return service.process(request, "CREATE", false);
    }
    @PostMapping("/cancel")
    public StepResponse cancel(@RequestBody StepRequest request) {
        return service.process(request, "CANCEL", true);
    }
}
