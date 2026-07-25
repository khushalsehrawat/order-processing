package com.example.coordinator.repository;
import com.example.coordinator.entity.OrderWorkflow;
import com.example.common.enums.OrderStatus;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.*;
public interface OrderWorkflowRepository extends JpaRepository<OrderWorkflow,Long>{
 boolean existsByExternalOrderId(String id);
 Optional<OrderWorkflow> findByExternalOrderId(String id);
 Page<OrderWorkflow> findAllByOrderByCreatedAtDesc(Pageable pageable);
 List<OrderWorkflow> findTop50ByStatusInOrderByUpdatedAtAsc(Collection<OrderStatus> statuses);
 List<OrderWorkflow> findByStatus(OrderStatus status);
 @Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
 @Query("select o from OrderWorkflow o where o.id = :id")
 Optional<OrderWorkflow> findWithLockById(@Param("id") Long id);
}
