package com.urbancart.shared.event;

import java.math.BigDecimal;
import java.time.Instant;

public record OrderConfirmedEvent(String orderId, String customerId, String productId, int quantity, BigDecimal amount, Instant createdAt) {}
