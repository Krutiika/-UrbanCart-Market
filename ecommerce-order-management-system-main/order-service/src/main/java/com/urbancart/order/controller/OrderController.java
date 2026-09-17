package com.urbancart.order.controller;

import com.urbancart.order.model.OrderEntity;
import com.urbancart.order.service.OrderService;
import com.urbancart.shared.dto.ApiResponse;
import com.urbancart.shared.dto.OrderRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {
    private final OrderService service;
    public OrderController(OrderService service) { this.service = service; }

    @PostMapping
    public ApiResponse<OrderEntity> placeOrder(@RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey,
                                                @Valid @RequestBody OrderRequest request) {
        return ApiResponse.success("Order placed", service.placeOrder(request, idempotencyKey));
    }

    @GetMapping("/{orderId}")
    public ApiResponse<OrderEntity> get(@PathVariable String orderId) { return ApiResponse.success("Order fetched", service.findById(orderId)); }
    @GetMapping("/customer/{customerId}")
    public ApiResponse<List<OrderEntity>> byCustomer(@PathVariable String customerId) { return ApiResponse.success("Customer orders", service.findByCustomer(customerId)); }
}
