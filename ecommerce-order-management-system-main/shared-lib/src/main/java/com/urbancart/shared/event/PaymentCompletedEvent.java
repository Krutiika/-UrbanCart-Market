package com.urbancart.shared.event;

import java.time.Instant;

public record PaymentCompletedEvent(String orderId, String paymentStatus, String paymentReference, Instant completedAt) {}
