package com.example.coordinator.dto;
import com.example.coordinator.entity.*;
import java.util.List;
public final class ApiDtos {
 private ApiDtos(){}
 public record ImportResponse(int imported,int duplicates){}
 public record OrderDetails(OrderWorkflow order,List<OrderStep> steps,List<OrderEvent> events){}
}
