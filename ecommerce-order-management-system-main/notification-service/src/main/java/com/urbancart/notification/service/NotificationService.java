package com.urbancart.notification.service;

import com.urbancart.shared.event.OrderConfirmedEvent;
import com.urbancart.shared.event.ShipmentCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {
    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    @RabbitListener(queues = "notification.order.confirmed.queue")
    public void sendOrderConfirmation(OrderConfirmedEvent event) {
        log.info("Sending order confirmation notification for orderId={}, customerId={}", event.orderId(), event.customerId());
    }

    @RabbitListener(queues = "notification.shipment.created.queue")
    public void sendShipmentNotification(ShipmentCreatedEvent event) {
        log.info("Sending shipment notification for orderId={}, trackingNumber={}", event.orderId(), event.trackingNumber());
    }
}
