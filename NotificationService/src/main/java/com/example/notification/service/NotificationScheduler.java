package com.example.notification.service;
import com.example.common.dto.ShippedOrderResponse;
import com.example.notification.entity.ShippingNotification;
import com.example.notification.repository.ShippingNotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClient;
import java.time.Instant;
import java.util.Arrays;
@Service @RequiredArgsConstructor
public class NotificationScheduler {
 private final ShippingNotificationRepository repo; private final RestClient client;
 @Value("${coordinator.service.url}") String coordinatorUrl;
 @Scheduled(fixedDelayString="${notification.scheduler.fixed-delay-ms:900000}")
 public void run(){
   ShippedOrderResponse[] orders;
   try{
     orders=client.get().uri(coordinatorUrl+"/api/orders/internal/shipped").retrieve().body(ShippedOrderResponse[].class);
   }catch(RestClientException e){
     System.err.println("Notification scheduler could not read shipped orders: "+e.getMessage());
     return;
   }
   if(orders==null)return;
   Arrays.stream(orders).forEach(o->{
     if(repo.existsByOrderId(o.orderId()))return;
     try{
       ShippingNotification n=new ShippingNotification();n.setOrderId(o.orderId());n.setStatus("SENT");n.setSentAt(Instant.now());repo.saveAndFlush(n);
       System.out.println("Notification recorded for "+o.orderId());
     }catch(DataIntegrityViolationException ignored){}
   });
 }
}
