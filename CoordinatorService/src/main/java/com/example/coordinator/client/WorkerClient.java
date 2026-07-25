package com.example.coordinator.client;
import com.example.common.dto.*;
import com.example.common.enums.StepName;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
@Component
public class WorkerClient {
 private final RestClient client;
 @Value("${services.order.url}") String orderUrl;
 @Value("${services.inventory.url}") String inventoryUrl;
 @Value("${services.payment.url}") String paymentUrl;
 @Value("${services.shipping.url}") String shippingUrl;
 public WorkerClient(RestClient client){this.client=client;}
 public StepResponse execute(StepName step,StepRequest r){
   return post(switch(step){
    case ORDER -> orderUrl+"/api/orders/create";
    case INVENTORY -> inventoryUrl+"/api/inventory/reserve";
    case PAYMENT -> paymentUrl+"/api/payments/charge";
    case SHIPPING -> shippingUrl+"/api/shipments/create";},r);
 }
 public StepResponse compensate(StepName step,StepRequest r){
   return post(switch(step){
    case ORDER -> orderUrl+"/api/orders/cancel";
    case INVENTORY -> inventoryUrl+"/api/inventory/release";
    case PAYMENT -> paymentUrl+"/api/payments/refund";
    case SHIPPING -> shippingUrl+"/api/shipments/cancel";},r);
 }
 private StepResponse post(String url,StepRequest r){
   StepResponse response=client.post().uri(url).body(r).retrieve().body(StepResponse.class);
   if(response==null || !response.success()) throw new IllegalStateException("Worker call failed: "+url);
   return response;
 }
}
