package com.example.payment.repository;
import com.example.payment.entity.BusinessRecord;
import org.springframework.data.jpa.repository.JpaRepository;
public interface BusinessRecordRepository extends JpaRepository<BusinessRecord,Long> {
    boolean existsByOrderIdAndOperationName(String orderId, String operationName);
}
