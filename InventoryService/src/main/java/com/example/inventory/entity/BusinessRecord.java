package com.example.inventory.entity;
import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
@Entity
@Table(name="inventory", uniqueConstraints=@UniqueConstraint(name="uk_inventory_business", columnNames={"orderId","operationName"}))
@Getter @Setter @NoArgsConstructor
public class BusinessRecord {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(nullable=false) private String orderId;
    @Column(nullable=false) private String operationName;
    private String state;
    private Instant createdAt;
}
