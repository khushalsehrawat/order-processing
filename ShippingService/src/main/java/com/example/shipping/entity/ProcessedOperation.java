package com.example.shipping.entity;
import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
@Entity
@Table(name="processed_operations", uniqueConstraints=@UniqueConstraint(name="uk_shipping_idempotency", columnNames="idempotencyKey"))
@Getter @Setter @NoArgsConstructor
public class ProcessedOperation {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(nullable=false) private String idempotencyKey;
    @Column(nullable=false) private String orderId;
    @Column(nullable=false) private String operationName;
    @Column(nullable=false) private String status;
    @Lob private String responseJson;
    private Instant createdAt;
    private Instant updatedAt;
}
