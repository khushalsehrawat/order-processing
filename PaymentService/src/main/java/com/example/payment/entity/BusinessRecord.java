package com.example.payment.entity;
import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
@Entity
@Table(name="payments", uniqueConstraints=@UniqueConstraint(name="uk_payment_business", columnNames={"orderId","operationName"}))
@Getter @Setter @NoArgsConstructor
public class BusinessRecord {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(nullable=false) private String orderId;
    @Column(nullable=false) private String operationName;
    private String state;
    private Instant createdAt;
}
