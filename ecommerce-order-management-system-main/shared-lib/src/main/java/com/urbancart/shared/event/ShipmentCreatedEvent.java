package com.urbancart.shared.event;

import java.time.Instant;

public record ShipmentCreatedEvent(String orderId, String trackingNumber, String courierPartner, Instant createdAt) {}
