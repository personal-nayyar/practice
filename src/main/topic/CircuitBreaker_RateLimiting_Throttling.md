# Resilience Patterns in Microservices

This guide explains three critical resilience patterns used in distributed systems:
- Circuit Breaker
- Rate Limiting
- Throttling

---

## 1. Circuit Breaker Pattern

### Concept
When one microservice depends on another (e.g., `OrderService -> PaymentService`) and the downstream service becomes slow or unavailable, continuous retrying can overload the system. The **Circuit Breaker** pattern prevents this by stopping calls to the failing service temporarily.

### How It Works
| State | Description |
|--------|--------------|
| **Closed** | Normal operation; all requests flow normally. |
| **Open** | Too many failures; block all requests for a cooldown period. |
| **Half-Open** | Allow a few trial requests to check recovery. |

### Example (Spring Boot + Resilience4j)
```java
@RestController
public class OrderController {

    @Autowired
    private PaymentServiceClient paymentClient;

    @GetMapping("/placeOrder")
    @CircuitBreaker(name = "paymentService", fallbackMethod = "paymentFallback")
    public String placeOrder() {
        return paymentClient.makePayment();
    }

    public String paymentFallback(Throwable t) {
        return "Payment Service unavailable, please try again later!";
    }
}
```

**YAML Configuration:**
```yaml
resilience4j.circuitbreaker:
  instances:
    paymentService:
      failureRateThreshold: 50
      waitDurationInOpenState: 10s
      permittedNumberOfCallsInHalfOpenState: 3
```

**Benefit:** Prevents cascading failures and allows the system to recover gracefully.

---

## 2. Rate Limiting

### Concept
Controls how many requests a user or client can send within a fixed period of time. Prevents abuse and ensures fair use of resources.

### Common Algorithms
- **Token Bucket** – Fixed number of tokens refilled periodically.
- **Leaky Bucket** – Processes requests at a constant rate.
- **Fixed / Sliding Window Counter** – Tracks requests per time window.

### Example (Spring Boot + Bucket4j)
```java
@Component
public class RateLimitFilter extends OncePerRequestFilter {

    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
        throws ServletException, IOException {

        String user = request.getHeader("X-User-Id");
        Bucket bucket = buckets.computeIfAbsent(user, k -> createNewBucket());

        if (bucket.tryConsume(1)) {
            chain.doFilter(request, response);
        } else {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        }
    }

    private Bucket createNewBucket() {
        Refill refill = Refill.greedy(10, Duration.ofSeconds(1));
        Bandwidth limit = Bandwidth.classic(10, refill);
        return Bucket.builder().addLimit(limit).build();
    }
}
```

**Benefit:** Prevents system overload and abusive behavior.

---

## 3. Throttling

### Concept
Throttling controls **overall traffic** to ensure system stability. It either delays or rejects requests when the system load exceeds a threshold.

### Types
- **User-level throttling:** Per-user limits.
- **System-level throttling:** Based on CPU/memory usage.
- **API Gateway throttling:** Enforced at the gateway level.

### Example (Semaphore-based)
```java
Semaphore semaphore = new Semaphore(100); // allow 100 concurrent calls

public String handleRequest() {
    if (semaphore.tryAcquire()) {
        try {
            return processRequest();
        } finally {
            semaphore.release();
        }
    } else {
        throw new TooManyRequestsException("System busy, please retry later");
    }
}
```

**API Gateway Example (AWS):**
```yaml
throttle:
  burstLimit: 100
  rateLimit: 50
```

**Benefit:** Keeps the system responsive and prevents crashes during high load.

---

## 4. Comparison Table
| Feature | Circuit Breaker | Rate Limiting | Throttling |
|----------|-----------------|----------------|-------------|
| **Purpose** | Prevent cascading failures | Limit requests per client | Protect overall system |
| **Focus** | Service-to-service reliability | Client fairness | System stability |
| **Common Tools** | Resilience4j, Hystrix | Bucket4j, Redis | API Gateway, Semaphores |
| **Behavior** | Opens on repeated failures | Rejects excess requests | Queues or rejects requests |

---

## 5. Real-World Example (E-commerce System)
- **Circuit Breaker:** When Payment Service is down, Order Service uses fallback logic or queues the order.
- **Rate Limiting:** Prevents users from spamming checkout or search endpoints.
- **Throttling:** Ensures API Gateway maintains total request throughput within system capacity.

---

These three patterns together ensure **fault tolerance**, **stability**, and **fair resource usage** in distributed microservices architectures.
