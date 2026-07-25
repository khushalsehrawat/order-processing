package com.example.shipping.controller;
import com.example.common.dto.*;
import com.example.shipping.service.ShippingActionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/shipments")
@RequiredArgsConstructor
public class ShippingController {
    private final ShippingActionService service;

    @PostMapping("/create")
    public StepResponse create(@RequestBody StepRequest request) {
        return service.process(request, "CREATE_SHIPMENT", false);
    }
    @PostMapping("/cancel")
    public StepResponse cancel(@RequestBody StepRequest request) {
        return service.process(request, "CANCEL_SHIPMENT", true);
    }
}
