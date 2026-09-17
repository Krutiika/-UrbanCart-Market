package com.urbancart.shipping.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {
    @Bean DirectExchange urbancartExchange() { return new DirectExchange("urbancart.exchange"); }
    @Bean MessageConverter messageConverter() { return new Jackson2JsonMessageConverter(); }
    @Bean Queue orderConfirmedQueue() { return new Queue("shipping.order.confirmed.queue", true); }
    @Bean Binding shippingBinding(Queue orderConfirmedQueue, DirectExchange urbancartExchange) { return BindingBuilder.bind(orderConfirmedQueue).to(urbancartExchange).with("order.confirmed"); }
}
