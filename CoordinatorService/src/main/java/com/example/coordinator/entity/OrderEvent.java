package com.example.coordinator.entity;
import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
@Entity @Table(name="order_events")
@Getter @Setter @NoArgsConstructor
public class OrderEvent {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 private Long orderId; private String eventType; private String stepName;
 @Column(length=2000) private String message;
 private Integer attemptNumber; private Instant createdAt;
}
