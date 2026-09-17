package com.urbancart.gateway;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayRoutesConfig {
    @Bean
    RouteLocator gatewayRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("product-service", route -> route.path("/products/**").uri("http://localhost:8081"))
                .route("cart-service", route -> route.path("/cart/**").uri("http://localhost:8082"))
                .route("inventory-service", route -> route.path("/inventory/**").uri("http://localhost:8083"))
                .route("order-service", route -> route.path("/orders/**").uri("http://localhost:8084"))
                .route("payment-service", route -> route.path("/payments/**").uri("http://localhost:8085"))
                .route("shipping-service", route -> route.path("/shipments/**").uri("http://localhost:8086"))
                .route("notification-service", route -> route.path("/notifications/**").uri("http://localhost:8087"))
                .build();
    }
}