# Advanced Spring Boot Interview Questions — Microservices & Architecture

### 1. How does Spring Boot simplify microservice development?
- Provides embedded servers for independent deployment.
- Uses Spring Cloud for discovery, configuration, load balancing, and resilience.

### 2. Explain service discovery in Spring Cloud.
- Uses Eureka, Consul, or Zookeeper for registration and discovery.
- Services register themselves and lookup others dynamically.

### 3. How does Spring Boot handle distributed configuration?
- With Spring Cloud Config Server, configurations are stored in Git or Vault.
- Clients fetch configs at startup or refresh via `/actuator/refresh`.

### 4. What’s the difference between synchronous and asynchronous communication?
- **Synchronous**: REST calls (tight coupling).
- **Asynchronous**: Messaging (Kafka, RabbitMQ) for decoupling.

### 5. How can you achieve fault tolerance?
- Use Resilience4j for circuit breakers, retries, and rate limiting.
- Implement fallback methods and bulkhead isolation.

### 6. Explain API Gateway in Spring Cloud.
- Spring Cloud Gateway handles routing, rate limiting, and authentication.
- Uses a reactive model via WebFlux.

### 7. How do you handle distributed tracing?
- Use Spring Cloud Sleuth and Zipkin.
- Adds trace IDs to logs for request tracking.

### 8. What is centralized logging?
- Centralized Logging allows all distributed systems to push logs to one place for real-time search, correlation, and alerting — drastically improving observability, debugging speed, and system reliability.
- Logs aggregated via ELK stack (Elasticsearch, Logstash, Kibana) or Loki + Grafana.

### 9. How can you version APIs?
- Use URL-based (`/v1/api`) or Header-based (`X-API-VERSION`) versioning.

### 10. How do you handle inter-service communication security?
- Use JWT or OAuth2 tokens validated by Gateway or Resource Server.
- Enable HTTPS and optionally mTLS.
