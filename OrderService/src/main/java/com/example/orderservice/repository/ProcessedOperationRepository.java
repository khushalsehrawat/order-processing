package com.example.orderservice.repository;
import com.example.orderservice.entity.ProcessedOperation;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
public interface ProcessedOperationRepository extends JpaRepository<ProcessedOperation,Long> {
    Optional<ProcessedOperation> findByIdempotencyKey(String idempotencyKey);
}
