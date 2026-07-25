package com.example.notification.repository;
import com.example.notification.entity.ShippingNotification;
import org.springframework.data.jpa.repository.JpaRepository;
public interface ShippingNotificationRepository extends JpaRepository<ShippingNotification,Long>{
 boolean existsByOrderId(String orderId);
}
