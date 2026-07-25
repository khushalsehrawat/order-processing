package com.example.coordinator.service;
import com.example.common.dto.*;
import com.example.common.enums.*;
import com.example.coordinator.client.WorkerClient;
import com.example.coordinator.entity.*;
import com.example.coordinator.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Supplier;

@Service @RequiredArgsConstructor
public class OrderProcessingService {
 private final OrderWorkflowRepository orders;
 private final OrderStepRepository steps;
 private final WorkerClient workers;
 private final AuditService audit;
 private final CompensationService compensation;
 @Qualifier("orderExecutor") private final Executor executor;
 @Value("${coordinator.retry.max-attempts:3}") private int maxAttempts;
 @Value("${coordinator.retry.initial-delay-ms:500}") private long initialDelayMs;

 public void submit(Long id){
   executor.execute(() -> {
     try{process(id);}
     catch(Exception e){
       audit.log(id,"ALL","PROCESS_ERROR",rootMessage(e),0);
     }
   });
 }

 public void process(Long id){
   OrderWorkflow order=orders.findById(id).orElseThrow();
   if(List.of(OrderStatus.PLACED,OrderStatus.SHIPPED,OrderStatus.CANCELLED,OrderStatus.NEEDS_ATTENTION).contains(order.getStatus())) return;
   audit.log(id,"ALL","PROCESSING_STARTED","Order processing started",0);
   order.setStatus(OrderStatus.IN_PROGRESS); order.setUpdatedAt(Instant.now()); orders.save(order);

   Map<StepName,CompletableFuture<Boolean>> futures=new EnumMap<>(StepName.class);
   OrderWorkflow submittedOrder=order;
   for(StepName step:StepName.values()){
     futures.put(step,CompletableFuture.supplyAsync(() -> executeStep(submittedOrder,step),executor)
       .exceptionally(e -> {
         audit.log(submittedOrder.getId(),step.name(),"FAILED",rootMessage(e),0);
         return false;
       }));
   }
   try{
     CompletableFuture.allOf(futures.values().toArray(CompletableFuture[]::new)).join();
   }catch(CompletionException ignored){}
   boolean all=futures.values().stream().allMatch(f -> f.getNow(false));
   if(all){
     order=orders.findById(id).orElseThrow(); order.setStatus(OrderStatus.PLACED);
     order.setUpdatedAt(Instant.now()); orders.save(order); audit.log(id,"ALL","PLACED","All four steps succeeded",1);
   } else compensation.compensate(id,false);
 }

 private boolean executeStep(OrderWorkflow order,StepName step){
   String key=order.getExternalOrderId()+":"+step+":EXECUTE";
   OrderStep s=steps.findByOrderIdAndStepNameAndActionType(order.getId(),step,ActionType.EXECUTE)
     .orElseGet(() -> newStep(order.getId(),step,ActionType.EXECUTE,key));
   if(s.getStatus()==StepStatus.SUCCEEDED) return true;
   try{
     s.setIdempotencyKey(key); s.setStatus(StepStatus.RUNNING); s.setStartedAt(Instant.now()); steps.save(s);
     StepRequest req=request(order,key,false);
     withRetry(() -> workers.execute(step,req),order.getId(),step,s);
     s.setStatus(StepStatus.SUCCEEDED); s.setCompletedAt(Instant.now()); s.setLastError(null); steps.save(s);
     audit.log(order.getId(),step.name(),"SUCCEEDED","Execution succeeded",s.getAttemptCount()); return true;
   }catch(Exception e){
     s.setStatus(StepStatus.FAILED); s.setLastError(rootMessage(e)); s.setCompletedAt(Instant.now()); steps.save(s);
     audit.log(order.getId(),step.name(),"FAILED",rootMessage(e),s.getAttemptCount()); return false;
   }
 }
 private OrderStep newStep(Long id,StepName step,ActionType action,String key){
   OrderStep s=new OrderStep(); s.setOrderId(id); s.setStepName(step); s.setActionType(action);
   s.setStatus(StepStatus.PENDING); s.setAttemptCount(0);
   s.setIdempotencyKey(key);
   try{return steps.saveAndFlush(s);}
   catch(DataIntegrityViolationException race){
     return steps.findByOrderIdAndStepNameAndActionType(id,step,action).orElseThrow();
   }
 }
 private StepResponse withRetry(Supplier<StepResponse> call,Long id,StepName step,OrderStep s){
   RuntimeException last=null;
   for(int i=1;i<=maxAttempts;i++){
    s.setAttemptCount(i); steps.save(s); audit.log(id,step.name(),"ATTEMPT","Attempt "+i,i);
    try{return call.get();}catch(RuntimeException e){last=e; if(i<maxAttempts) sleep(initialDelayMs*i);}
   }
   throw last==null?new IllegalStateException("Retry exhausted"):last;
 }
 private static void sleep(long ms){try{Thread.sleep(ms);}catch(InterruptedException e){Thread.currentThread().interrupt();}}
 static String rootMessage(Throwable e){Throwable t=e;while(t.getCause()!=null)t=t.getCause();return truncate(String.valueOf(t.getMessage()));}
 static String truncate(String value){return value!=null && value.length()>240?value.substring(0,240):value;}
 public static StepRequest request(OrderWorkflow o,String key,boolean manual){
   return new StepRequest(o.getExternalOrderId(),o.getCustomerId(),o.getProductId(),o.getQuantity(),o.getAmount(),key,o.getFailAt(),o.getCompFailAt(),manual);
 }
}
