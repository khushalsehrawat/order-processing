package com.example.shipping.entity;
import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
@Entity
@Table(name="shipments", uniqueConstraints=@UniqueConstraint(name="uk_shipping_business", columnNames={"orderId","operationName"}))
@Getter @Setter @NoArgsConstructor
public class BusinessRecord {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(nullable=false) private String orderId;
    @Column(nullable=false) private String operationName;
    private String state;
    private Instant createdAt;
}
