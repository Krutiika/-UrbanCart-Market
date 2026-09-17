package com.urbancart.order.config;

import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {
    @Bean
    DirectExchange urbancartExchange() { return new DirectExchange("urbancart.exchange"); }

    @Bean
    MessageConverter messageConverter() { return new Jackson2JsonMessageConverter(); }
}
