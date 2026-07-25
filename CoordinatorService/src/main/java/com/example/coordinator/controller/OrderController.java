package com.example.coordinator.controller;

import com.example.common.dto.ShippedOrderResponse;
import com.example.common.enums.OrderStatus;
import com.example.coordinator.dto.ApiDtos.OrderDetails;
import com.example.coordinator.repository.*;
import com.example.coordinator.service.CompensationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.web.bind.annotation.*;
import java.time.Instant;
import java.util.List;
@RestController @RequestMapping("/api/orders") @RequiredArgsConstructor
@CrossOrigin(origins="*")
public class OrderController {
 private final com.example.coordinator.repository.OrderWorkflowRepository orders; private final OrderStepRepository steps;
 private final com.example.coordinator.repository.OrderEventRepository events; private final CompensationService compensation;
 @GetMapping public Page<?> list(@RequestParam(defaultValue="0") int page,@RequestParam(defaultValue="20") int size){
   return orders.findAllByOrderByCreatedAtDesc(PageRequest.of(page,size));
 }
 @GetMapping("/{id}") public OrderDetails detail(@PathVariable Long id){
   return new OrderDetails(orders.findById(id).orElseThrow(),steps.findByOrderIdOrderByIdAsc(id),events.findByOrderIdOrderByCreatedAtAsc(id));
 }
 @PostMapping("/{id}/mark-shipped") public void shipped(@PathVariable Long id){
   var o=orders.findById(id).orElseThrow();
   if(o.getStatus()==OrderStatus.SHIPPED) return;
   if(o.getStatus()!=OrderStatus.PLACED) throw new IllegalStateException("Only PLACED orders can be shipped");
   o.setStatus(OrderStatus.SHIPPED);o.setUpdatedAt(Instant.now());orders.save(o);
 }
 @PostMapping("/{id}/retry-compensation") public void retry(@PathVariable Long id){
   var o=orders.findById(id).orElseThrow();
   if(o.getStatus()!=OrderStatus.NEEDS_ATTENTION) throw new IllegalStateException("Only NEEDS_ATTENTION orders can retry compensation");
   compensation.retry(id);
 }
 @GetMapping("/internal/shipped") public List<ShippedOrderResponse> shippedOrders(){
   return orders.findByStatus(OrderStatus.SHIPPED).stream().map(o->new ShippedOrderResponse(String.valueOf(o.getId()),o.getExternalOrderId())).toList();
 }
}
