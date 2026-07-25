package com.example.coordinator.repository;
import com.example.coordinator.entity.OrderStep;
import com.example.common.enums.*;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface OrderStepRepository extends JpaRepository<OrderStep,Long>{
 List<OrderStep> findByOrderIdOrderByIdAsc(Long orderId);
 Optional<OrderStep> findByOrderIdAndStepNameAndActionType(Long orderId,StepName step,ActionType action);
}
