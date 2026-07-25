package com.example.notification.entity;
import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
@Entity @Table(name="shipping_notifications",uniqueConstraints=@UniqueConstraint(name="uk_notification_order",columnNames="orderId"))
@Getter @Setter @NoArgsConstructor
public class ShippingNotification {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(nullable=false) private String orderId;
 private String status; private Instant sentAt;
}
