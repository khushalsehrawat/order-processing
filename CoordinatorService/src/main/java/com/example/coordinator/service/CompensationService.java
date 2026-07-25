package com.example.coordinator.service;
import com.example.common.enums.*;
import com.example.coordinator.client.WorkerClient;
import com.example.coordinator.entity.*;
import com.example.coordinator.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.*;
@Service @RequiredArgsConstructor
public class CompensationService {
 private final OrderWorkflowRepository orders;
 private final OrderStepRepository steps;
 private final WorkerClient workers;
 private final AuditService audit;
 @Value("${coordinator.retry.max-attempts:3}") private int maxAttempts;
 @Value("${coordinator.retry.initial-delay-ms:500}") private long initialDelayMs;

 public void compensate(Long orderId,boolean manual){
   OrderWorkflow order=orders.findById(orderId).orElseThrow();
   order.setStatus(OrderStatus.COMPENSATING); order.setUpdatedAt(Instant.now()); orders.save(order);
   boolean failed=false;
   for(StepName step:List.of(StepName.SHIPPING,StepName.PAYMENT,StepName.INVENTORY,StepName.ORDER)){
     var exec=steps.findByOrderIdAndStepNameAndActionType(orderId,step,ActionType.EXECUTE);
     if(exec.isEmpty() || exec.get().getStatus()!=StepStatus.SUCCEEDED) continue;
     String key=order.getExternalOrderId()+":"+step+":COMPENSATE";
     OrderStep comp=steps.findByOrderIdAndStepNameAndActionType(orderId,step,ActionType.COMPENSATE)
       .orElseGet(() -> create(orderId,step,key));
     if(comp.getStatus()==StepStatus.COMPENSATED) continue;
     try{
       comp.setIdempotencyKey(key); comp.setStatus(StepStatus.COMPENSATING); steps.save(comp);
       RuntimeException last=null;
       for(int i=1;i<=maxAttempts;i++){
         comp.setAttemptCount(i); steps.save(comp); audit.log(orderId,step.name(),"COMPENSATE_ATTEMPT","Attempt "+i,i);
         try{workers.compensate(step,OrderProcessingService.request(order,key,manual));last=null;break;}
         catch(RuntimeException e){last=e;if(i<maxAttempts)sleep(initialDelayMs*i);}
       }
       if(last!=null) throw last;
       comp.setStatus(StepStatus.COMPENSATED); comp.setCompletedAt(Instant.now()); comp.setLastError(null); steps.save(comp);
     }catch(Exception e){
       failed=true; comp.setStatus(StepStatus.COMPENSATION_FAILED); comp.setLastError(rootMessage(e));
       comp.setCompletedAt(Instant.now()); steps.save(comp);
     }
   }
   order=orders.findById(orderId).orElseThrow();
   order.setStatus(failed?OrderStatus.NEEDS_ATTENTION:OrderStatus.CANCELLED);
   order.setUpdatedAt(Instant.now()); orders.save(order);
   audit.log(orderId,"ALL",order.getStatus().name(),"Compensation completed with status "+order.getStatus(),1);
 }
 public void retry(Long id){compensate(id,true);}
 private OrderStep create(Long id,StepName step,String key){
   OrderStep s=new OrderStep();s.setOrderId(id);s.setStepName(step);s.setActionType(ActionType.COMPENSATE);
   s.setStatus(StepStatus.PENDING);s.setAttemptCount(0);s.setIdempotencyKey(key);
   try{return steps.saveAndFlush(s);}
   catch(DataIntegrityViolationException race){
     return steps.findByOrderIdAndStepNameAndActionType(id,step,ActionType.COMPENSATE).orElseThrow();
   }
 }
 private static void sleep(long ms){try{Thread.sleep(ms);}catch(InterruptedException e){Thread.currentThread().interrupt();}}
 private static String rootMessage(Throwable e){return OrderProcessingService.rootMessage(e);}
}
