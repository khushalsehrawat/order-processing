package com.example.coordinator.service;
import com.example.coordinator.entity.OrderEvent;
import com.example.coordinator.repository.OrderEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.Instant;
@Service @RequiredArgsConstructor
public class AuditService {
 private final OrderEventRepository repo;
 public void log(Long orderId,String step,String type,String message,int attempt){
   OrderEvent e=new OrderEvent(); e.setOrderId(orderId); e.setStepName(step); e.setEventType(type);
   e.setMessage(OrderProcessingService.truncate(message)); e.setAttemptNumber(attempt); e.setCreatedAt(Instant.now()); repo.save(e);
 }
}
