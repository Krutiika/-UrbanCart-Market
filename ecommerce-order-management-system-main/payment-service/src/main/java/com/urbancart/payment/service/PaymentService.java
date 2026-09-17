package com.urbancart.payment.service;

import com.urbancart.payment.model.PaymentTransaction;
import com.urbancart.payment.repository.PaymentRepository;
import com.urbancart.shared.dto.PaymentRequest;
import com.urbancart.shared.event.PaymentCompletedEvent;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.UUID;

@Service
public class PaymentService {
    private final PaymentRepository repository;
    private final RabbitTemplate rabbitTemplate;
    public PaymentService(PaymentRepository repository, RabbitTemplate rabbitTemplate) { this.repository = repository; this.rabbitTemplate = rabbitTemplate; }

    public PaymentTransaction process(PaymentRequest request) {
        PaymentTransaction tx = new PaymentTransaction();
        tx.setId("PAY-" + UUID.randomUUID());
        tx.setOrderId(request.orderId());
        tx.setAmount(request.amount());
        tx.setPaymentMode(request.paymentMode());
        tx.setPaymentStatus("SUCCESS");
        tx.setPaymentReference("PGW-" + UUID.randomUUID().toString().substring(0, 8));
        PaymentTransaction saved = repository.save(tx);
        rabbitTemplate.convertAndSend("urbancart.exchange", "payment.completed",
                new PaymentCompletedEvent(saved.getOrderId(), saved.getPaymentStatus(), saved.getPaymentReference(), Instant.now()));
        return saved;
    }
}
