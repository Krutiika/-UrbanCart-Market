# Project Explanation Notes

## One-Line Summary

E-Commerce Order Management System manages product catalog, cart, inventory reservation, order placement, payment status, shipment tracking and notification using Java Spring Boot microservices.

## Main Flow

1. Customer places an order through Customer Website.
2. Request reaches Spring Cloud Gateway.
3. Order Service validates the order request.
4. Inventory Service reserves stock.
5. Order Service creates the order in PostgreSQL.
6. Order Service publishes `order.confirmed` event to RabbitMQ.
7. Shipping Service consumes the event and creates shipment.
8. Notification Service consumes events and logs/sends customer notification.

## Interview Points

- PostgreSQL is used for transactional data.
- MongoDB is used for flexible product and cart data.
- RabbitMQ is used for asynchronous communication.
- Idempotency key avoids duplicate order creation.
- Correlation ID can be added at gateway level for debugging.
- Optimistic locking can avoid overselling in Inventory Service.
