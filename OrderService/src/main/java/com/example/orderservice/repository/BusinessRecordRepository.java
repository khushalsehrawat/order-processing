package com.example.orderservice.repository;
import com.example.orderservice.entity.BusinessRecord;
import org.springframework.data.jpa.repository.JpaRepository;
public interface BusinessRecordRepository extends JpaRepository<BusinessRecord,Long> {
    boolean existsByOrderIdAndOperationName(String orderId, String operationName);
}
