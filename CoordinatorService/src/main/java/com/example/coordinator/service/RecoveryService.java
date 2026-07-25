package com.example.coordinator.service;
import com.example.common.enums.OrderStatus;
import com.example.coordinator.repository.OrderWorkflowRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.util.List;
@Service @RequiredArgsConstructor
public class RecoveryService {
 private final OrderWorkflowRepository orders;
 private final OrderProcessingService processing;
 private final CompensationService compensation;
 @Scheduled(fixedDelay=10000)
 public void recover(){
   for(var o:orders.findTop50ByStatusInOrderByUpdatedAtAsc(List.of(OrderStatus.PENDING,OrderStatus.IN_PROGRESS,OrderStatus.COMPENSATING))){
     if(o.getStatus()==OrderStatus.COMPENSATING) compensation.compensate(o.getId(),false);
     else processing.submit(o.getId());
   }
 }
}
