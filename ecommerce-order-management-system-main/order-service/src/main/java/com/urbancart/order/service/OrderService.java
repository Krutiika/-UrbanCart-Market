package com.urbancart.order.service;

import com.urbancart.order.model.OrderEntity;
import com.urbancart.order.repository.OrderRepository;
import com.urbancart.shared.dto.InventoryRequest;
import com.urbancart.shared.dto.OrderRequest;
import com.urbancart.shared.event.OrderConfirmedEvent;
import com.urbancart.shared.exception.BusinessException;
import jakarta.transaction.Transactional;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class OrderService {
    private final OrderRepository repository;
    private final RabbitTemplate rabbitTemplate;
    private final RestTemplate restTemplate = new RestTemplate();

    public OrderService(OrderRepository repository, RabbitTemplate rabbitTemplate) {
        this.repository = repository;
        this.rabbitTemplate = rabbitTemplate;
    }

    @Transactional
    public OrderEntity placeOrder(OrderRequest request, String idempotencyKey) {
        if (idempotencyKey != null) {
            var existing = repository.findByIdempotencyKey(idempotencyKey);
            if (existing.isPresent()) return existing.get();
        }

        restTemplate.postForObject("http://localhost:8083/inventory/reserve", new InventoryRequest(request.productId(), request.quantity()), Object.class);

        OrderEntity order = new OrderEntity();
        order.setId("ORD-" + UUID.randomUUID());
        order.setCustomerId(request.customerId());
        order.setProductId(request.productId());
        order.setQuantity(request.quantity());
        order.setAmount(request.amount());
        order.setPaymentMode(request.paymentMode());
        order.setDeliveryAddress(request.deliveryAddress());
        order.setOrderStatus("CONFIRMED");
        order.setPaymentStatus("PAYMENT_PENDING");
        order.setIdempotencyKey(idempotencyKey);
        order.setUpdatedAt(Instant.now());
        OrderEntity saved = repository.save(order);

        rabbitTemplate.convertAndSend("urbancart.exchange", "order.confirmed",
                new OrderConfirmedEvent(saved.getId(), saved.getCustomerId(), saved.getProductId(), saved.getQuantity(), saved.getAmount(), Instant.now()));
        return saved;
    }

    public OrderEntity findById(String id) { return repository.findById(id).orElseThrow(() -> new BusinessException("Order not found: " + id)); }
    public List<OrderEntity> findByCustomer(String customerId) { return repository.findByCustomerId(customerId); }
}
