package com.example.inventory.repository;
import com.example.inventory.entity.BusinessRecord;
import org.springframework.data.jpa.repository.JpaRepository;
public interface BusinessRecordRepository extends JpaRepository<BusinessRecord,Long> {
    boolean existsByOrderIdAndOperationName(String orderId, String operationName);
}
