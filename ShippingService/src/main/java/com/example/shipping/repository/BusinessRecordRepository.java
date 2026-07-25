package com.example.shipping.repository;
import com.example.shipping.entity.BusinessRecord;
import org.springframework.data.jpa.repository.JpaRepository;
public interface BusinessRecordRepository extends JpaRepository<BusinessRecord,Long> {
    boolean existsByOrderIdAndOperationName(String orderId, String operationName);
}
