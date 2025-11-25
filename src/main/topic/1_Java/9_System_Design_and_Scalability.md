# ⚡ System Design & Scalability — Java Interview Preparation

## 1. How would you design a scalable, fault-tolerant REST API in Java?
- Use **Spring Boot** with stateless REST endpoints.
- Employ **Load Balancers (NGINX, AWS ALB)** for distributing traffic.
- Store state externally (e.g., Redis, DB) to maintain statelessness.
- Use **Circuit Breakers (Resilience4j/Hystrix)** for fault tolerance.
- Leverage **horizontal scaling** and **container orchestration (Kubernetes)**.
- Implement proper **monitoring and logging** (Prometheus, ELK).

---

## 2. How do you manage asynchronous processing (e.g., Kafka, RabbitMQ)?
- Use **Kafka** or **RabbitMQ** for decoupled event-driven architecture.
- Employ **@Async**, **CompletableFuture**, or **Spring Cloud Stream** for async processing.
- Use **back-pressure** and **retry queues** to prevent message loss.
- Ensure **idempotency** in consumers to handle duplicate messages.

**Example:**
```java
@Async
public CompletableFuture<String> processOrder(Order order) {
    kafkaTemplate.send("order-topic", order);
    return CompletableFuture.completedFuture("Order queued");
}
```

---

## 3. Explain circuit breaker and retry patterns.
- **Circuit Breaker**: Prevents cascading failures by halting requests when a downstream service is unhealthy.
  - Tools: **Resilience4j**, **Spring Cloud Circuit Breaker**.
- **Retry Pattern**: Automatically retries failed operations before failing completely.

**Example using Resilience4j:**
```java
@CircuitBreaker(name = "paymentService", fallbackMethod = "fallbackPayment")
@Retry(name = "paymentRetry")
public String processPayment() {
    // external API call
}
```

---

## 4. How would you implement rate limiting?
- Rate limit per user/IP using **Redis** counters or **API Gateway (Kong, NGINX)**.
- Algorithms: **Token Bucket**, **Leaky Bucket**, **Fixed Window**.
- Use annotations or filters in Spring Boot.

**Example:**
```java
@RateLimiter(name = "apiRateLimiter")
public ResponseEntity<?> getUserData() {
    return ResponseEntity.ok(userService.fetchData());
}
```

---

## 5. Explain design patterns you’ve applied in real-world projects.
- **Singleton**: For shared configs like DB connections.
- **Factory**: For object creation based on type.
- **Strategy**: For algorithm selection at runtime.
- **Observer**: For event notifications between microservices.
- **Builder**: For constructing complex immutable objects.

---

## 6. What are microservices best practices in Java Spring Boot?
- Each service should have **its own DB** (database per service pattern).
- Use **Feign Clients** or **RestTemplate/WebClient** for inter-service calls.
- Externalize configuration with **Spring Cloud Config**.
- Implement **Service Discovery** (Eureka/Consul).
- Use **API Gateway** for routing and authentication.
- Apply **centralized logging and monitoring**.

---

## 7. How to ensure idempotency in distributed systems?
- Use **unique request IDs** or **deduplication keys**.
- Store processed IDs in a **cache/DB** to avoid reprocessing.
- Design APIs to perform the same action if the request repeats.
- Use **message offsets** in Kafka to ensure exactly-once delivery.

**Example:**
```java
public ResponseEntity<String> processPayment(String transactionId) {
    if (processedTxns.contains(transactionId)) {
        return ResponseEntity.ok("Already processed");
    }
    processedTxns.add(transactionId);
    // process payment
    return ResponseEntity.ok("Processed successfully");
}
```

---
