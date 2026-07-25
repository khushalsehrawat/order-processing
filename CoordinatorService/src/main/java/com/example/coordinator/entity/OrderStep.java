package com.example.coordinator.entity;
import com.example.common.enums.*;
import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
@Entity
@Table(name="order_steps",uniqueConstraints={
 @UniqueConstraint(name="uk_step_key",columnNames="idempotencyKey"),
 @UniqueConstraint(name="uk_order_step_action",columnNames={"orderId","stepName","actionType"})
})
@Getter @Setter @NoArgsConstructor
public class OrderStep {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(nullable=false) private Long orderId;
 @Enumerated(EnumType.STRING) private StepName stepName;
 @Enumerated(EnumType.STRING) private ActionType actionType;
 @Enumerated(EnumType.STRING) private StepStatus status;
 @Column(nullable=false) private String idempotencyKey;
 private Integer attemptCount;
 @Column(length=2000) private String lastError;
 private Instant startedAt; private Instant completedAt;
}
