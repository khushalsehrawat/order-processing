package com.example.inventory.controller;
import com.example.common.dto.*;
import com.example.inventory.service.InventoryActionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {
    private final InventoryActionService service;

    @PostMapping("/reserve")
    public StepResponse reserve(@RequestBody StepRequest request) {
        return service.process(request, "RESERVE", false);
    }
    @PostMapping("/release")
    public StepResponse release(@RequestBody StepRequest request) {
        return service.process(request, "RELEASE", true);
    }
}
