# E-Commerce Order Management System

A GitHub-ready Java Spring Boot microservices prototype for interview preparation and project explanation videos.

> This project is created for educational and interview-preparation purposes. Client name and business flow are fictional.

## Architecture

```
Customer Website / Admin Portal
        |
        v
Spring Cloud Gateway
        |
        v
Product | Cart | Inventory | Order | Payment | Shipping | Notification
        |        |           |       |         |          |
   MongoDB/Elasticsearch   PostgreSQL        RabbitMQ Events
```

## Tech Stack

- Java 17
- Spring Boot 3.3.6
- Spring Cloud Gateway
- REST APIs
- PostgreSQL
- MongoDB
- RabbitMQ
- Docker Compose
- Maven multi-module structure

## Microservices

| Service | Port | Responsibility |
|---|---:|---|
| api-gateway | 8080 | Single entry point and request routing |
| product-service | 8081 | Product catalog and search-ready product data |
| cart-service | 8082 | Customer cart management |
| inventory-service | 8083 | Stock check, reserve and release inventory |
| order-service | 8084 | Order placement and order lifecycle |
| payment-service | 8085 | Payment simulation and payment event publishing |
| shipping-service | 8086 | Shipment creation and tracking |
| notification-service | 8087 | Async notification consumer |

## Run locally

### 1. Start infra

```bash
docker compose up -d postgres mongodb rabbitmq
```

### 2. Build all modules

```bash
mvn clean install
```

### 3. Start services

Open separate terminals:

```bash
mvn -pl api-gateway spring-boot:run
mvn -pl product-service spring-boot:run
mvn -pl cart-service spring-boot:run
mvn -pl inventory-service spring-boot:run
mvn -pl order-service spring-boot:run
mvn -pl payment-service spring-boot:run
mvn -pl shipping-service spring-boot:run
mvn -pl notification-service spring-boot:run
```

## Sample API Flow

### Create product

```bash
curl -X POST http://localhost:8080/products \
  -H "Content-Type: application/json" \
  -d '{"name":"Wireless Headphones","category":"Electronics","price":2499,"available":true}'
```

### Add stock

```bash
curl -X POST http://localhost:8080/inventory \
  -H "Content-Type: application/json" \
  -d '{"productId":"P1001","availableQuantity":50}'
```

### Place order

```bash
curl -X POST http://localhost:8080/orders \
  -H "Content-Type: application/json" \
  -H "Idempotency-Key: order-key-101" \
  -d '{"customerId":"C101","productId":"P1001","quantity":2,"amount":4998,"paymentMode":"CARD","deliveryAddress":"Kota, Rajasthan"}'
```

## Interview Explanation

This project demonstrates an enterprise e-commerce order lifecycle. When the customer places an order, Order Service validates the request, reserves inventory through Inventory Service, creates an order in PostgreSQL, calls Payment Service, publishes RabbitMQ events, and then Shipping and Notification services handle their work asynchronously.

## Important Concepts Covered

- Microservices architecture
- API Gateway routing
- Synchronous REST communication
- Asynchronous RabbitMQ events
- PostgreSQL transactional data
- MongoDB product/cart document data
- Idempotency key for duplicate order prevention
- Centralized exception handling
- Basic production-style logging and correlation ID
