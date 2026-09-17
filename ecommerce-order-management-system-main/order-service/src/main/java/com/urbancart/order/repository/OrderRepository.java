package com.urbancart.order.repository;

import com.urbancart.order.model.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<OrderEntity, String> {
    List<OrderEntity> findByCustomerId(String customerId);
    Optional<OrderEntity> findByIdempotencyKey(String idempotencyKey);
}
