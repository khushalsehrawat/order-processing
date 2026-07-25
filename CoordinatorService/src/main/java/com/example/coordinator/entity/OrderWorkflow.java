package com.example.coordinator.entity;
import com.example.common.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.Instant;
@Entity @Table(name="order_workflows")
@Getter @Setter @NoArgsConstructor
public class OrderWorkflow {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(nullable=false,unique=true) private String externalOrderId;
 private String customerId; private String productId; private Integer quantity;
 @Column(precision=15,scale=2) private BigDecimal amount;
 @Enumerated(EnumType.STRING) private OrderStatus status;
 private String failAt; private String compFailAt;
 private Instant createdAt; private Instant updatedAt;
 @Version private Long version;
}
