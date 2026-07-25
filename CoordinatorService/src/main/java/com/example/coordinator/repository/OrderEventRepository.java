package com.example.coordinator.repository;
import com.example.coordinator.entity.OrderEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface OrderEventRepository extends JpaRepository<OrderEvent,Long>{
 List<OrderEvent> findByOrderIdOrderByCreatedAtAsc(Long orderId);
}
