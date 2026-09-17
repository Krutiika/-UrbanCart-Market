package com.urbancart.notification.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {
    @Bean DirectExchange urbancartExchange() { return new DirectExchange("urbancart.exchange"); }
    @Bean MessageConverter messageConverter() { return new Jackson2JsonMessageConverter(); }
    @Bean Queue orderConfirmedNotificationQueue() { return new Queue("notification.order.confirmed.queue", true); }
    @Bean Queue shipmentCreatedNotificationQueue() { return new Queue("notification.shipment.created.queue", true); }
    @Bean Binding orderNotificationBinding(Queue orderConfirmedNotificationQueue, DirectExchange urbancartExchange) { return BindingBuilder.bind(orderConfirmedNotificationQueue).to(urbancartExchange).with("order.confirmed"); }
    @Bean Binding shipmentNotificationBinding(Queue shipmentCreatedNotificationQueue, DirectExchange urbancartExchange) { return BindingBuilder.bind(shipmentCreatedNotificationQueue).to(urbancartExchange).with("shipment.created"); }
}
