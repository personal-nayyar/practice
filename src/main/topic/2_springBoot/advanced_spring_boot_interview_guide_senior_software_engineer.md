# Advanced Spring Boot Interview Guide — Senior Software Engineer

> Compact preparation guide with high‑value answers, patterns, and code snippets. Use it to revise before interviews or to paste into an application.

---

## Table of contents

1. Core Spring Boot & Auto-configuration
2. Dependency Injection & Bean Scopes
3. Startup Flow
4. Customizing Auto-configuration
5. Microservices & Distributed Patterns
6. Security & Observability
7. Performance, Caching & Scalability
8. Data Layer & Reactive Systems
9. Testing & CI/CD
10. Design, Idempotency & Cloud-Native Best Practices

---

## 1. Core Spring Boot & Auto-configuration

**Q: How does Spring Boot auto-configuration work internally?**

**A:** Spring Boot scans `spring.factories` (Spring Boot 2) or `org.springframework.boot.autoconfigure.AutoConfiguration.imports` (Boot 3+) metadata inside JARs. Each auto-config class is annotated with `@Configuration` and conditional annotations (e.g., `@ConditionalOnClass`, `@ConditionalOnBean`, `@ConditionalOnProperty`) so configurations only apply when the environment matches. The `SpringFactoriesLoader` / `AutoConfigurationImportSelector` builds the candidate list and the `Condition` evaluation pipeline decides which configurations are applied.

**Key takeaways:** auto-config = metadata + condition evaluation; override by defining beans or properties.

## 2. Dependency Injection & Bean Scopes

**Q: Prototype vs Singleton DI behavior?**

**A:** Singleton beans are instantiated once per `ApplicationContext`. Prototype beans are created on each request. When injecting a prototype into a singleton, use `ObjectProvider<T>`, `Provider<T>` or `@Lookup` to ensure a fresh instance per use.

**Example:**

```java
@Component
public class SingletonService {
  private final ObjectProvider<PrototypeBean> provider;
  public SingletonService(ObjectProvider<PrototypeBean> provider) { this.provider = provider; }
  public void use() { PrototypeBean p = provider.getIfAvailable(); }
}
```

## 3. Startup Flow

**Q: Explain ********main()******** → embedded server flow.**

**A:** `SpringApplication.run()` builds `SpringApplication` → creates `ApplicationContext` (AnnotationConfigApplicationContext / SpringApplicationContext) → registers `Environment` and `ApplicationListeners` → loads `ApplicationContextInitializer`s → performs bean definition scanning & processing → applies `AutoConfiguration` → starts embedded servlet container (Tomcat/Jetty/Undertow) by creating `ServletWebServerApplicationContext` and invoking `WebServer` lifecycle. `ApplicationReadyEvent` signals readiness.

## 4. Customizing Auto-configuration

**Q: Ways to override auto-configuration?**

- Provide your own bean with the same type/name. Spring prefers user-defined beans.
- Disable specific auto-config via `spring.autoconfigure.exclude` or `@EnableAutoConfiguration(exclude=...)`.
- Use `@ConditionalOnX` at your configuration classes.
- Implement a `BeanFactoryPostProcessor` or `EnvironmentPostProcessor` for deep customizations.

## 5. Microservices & Distributed Patterns

**Q: Resilience patterns — Circuit Breaker, Bulkhead, Retry. How to apply?**

**A:** Use libraries like Resilience4j or Spring Cloud CircuitBreaker.

- **Circuit breaker:** fail fast and allow recovery—set failure thresholds and open state windows.
- **Bulkhead:** isolate resources—thread pools or semaphores per remote call.
- **Retry:** exponential backoff with caps; idempotent operations only.

**Q: Service-to-service comms — Feign vs RestTemplate vs WebClient?**

- **RestTemplate:** blocking, legacy (still useful for simple blocking apps). Deprecated in favor of WebClient for reactive.
- **Feign:** declarative HTTP client, integrates with Ribbon/Eureka; ideal for high-level service calls.
- **WebClient:** non-blocking, reactive; use when you need low threads per I/O and backpressure control.

## 6. Security & Observability

**Q: Spring Security filter chain internals?**

**A:** Security filters are ordered `Filter` beans registered by `SecurityFilterChain`. When a request arrives the chain executes filters (CSRF, CORS, AuthenticationFilter, AuthorizationFilter, etc.). Authentication is resolved often by `AuthenticationManager` and `SecurityContextHolder` stores auth info. For JWT/OAuth2, token parsing happens early in the chain.

**Q: Observability integration?**

- **Metrics:** Micrometer → Prometheus exporter. Instrument counters, timers, and gauges at service boundaries.
- **Tracing:** OpenTelemetry / Spring Cloud Sleuth (legacy) → propagate trace id via headers (`traceparent`/`b3`).
- **Logging:** structured logs (JSON), context enrichment (MDC) with trace ids.

## 7. Performance, Caching & Scalability

**Q: Diagnose slow startup or memory issues**

- Use JVM flags (`-Xmx`, `-Xms`, `-XX:+UseG1GC`), enable GC logging. Profile startup with `spring-boot-starter-actuator`'s `startup` metrics and `-javaagent` profilers.
- Use `spring.main.lazy-initialization=true` (temporary) to improve startup by deferring bean creation.

**Q: Caching strategies**

- Use `@Cacheable` for read-mostly expensive calls. Invalidate with `@CacheEvict`. Use Redis/HotRod/Ehcache for distributed caching.
- Cache key design: include relevant request parameters and tenant ids. Set TTL based on staleness tolerance.

**Q: DB connection pooling (HikariCP) best practices**

- Tune `maximumPoolSize` according to workload and DB capacity.
- Monitor pool usage, set `connectionTimeout` shorter than application-level timeouts.
- Use prepared statement caching if your DB supports it.

## 8. Data Layer & Reactive Systems

**Q: Spring Data JPA vs Spring Data JDBC**

- **JPA:** rich ORM with caching, relations, lazy loading — suited for complex domain models.
- **JDBC:** simpler, predictable SQL mapping, no lazy loading; better where SQL control & performance are critical.

**Q: Reactive programming (WebFlux & Reactor)**

- Use `Mono`/`Flux`. Design non-blocking endpoints and use reactive DB drivers (R2DBC) or reactive messaging. Handle backpressure with `onBackpressureBuffer`, `limitRate`, or `flatMapSequential`.

## 9. Testing & CI/CD

**Q: Structuring tests**

- **Unit tests:** Mockito + JUnit for fast logic checks.
- **Slice tests:** `@WebMvcTest`, `@DataJpaTest` to test layers in isolation.
- **Integration tests:** `@SpringBootTest` with Testcontainers for DB/Message systems.
- **Contract tests:** Pact or similar for inter-service contracts.

**Q: Mocking external dependencies**

- Use WireMock for HTTP services; Testcontainers for DB/Kafka; Mockito for Java dependencies.

**Q: Speeding up tests**

- Use in-memory DBs where applicable, isolate slow tests into a separate suite, parallelize tests with JUnit 5, and mock networked resources when integration-level fidelity is unnecessary.

## 10. Design, Idempotency & Cloud-Native Best Practices

**Q: Idempotency and event-driven consistency**

- Assign idempotency keys for operations (e.g., paymentId). Store operation state in an idempotency table and return cached response if repeated.
- Use **event sourcing** or persistent logs for durable replay and reconciliation. Combine with dedup tokens and idempotent handlers.

**Q: Graceful shutdown & health checks**

- Implement `@PreDestroy` and use `SpringApplication.exit()` hooks. For Kubernetes, set `readinessProbe` to `false` during shutdown and rely on `livenessProbe` for crash detection.

**Q: Schema migrations**

- Use Flyway or Liquibase. Prefer incremental, reversible migrations and CI checks that run migrations against a fresh instance.

---

## Appendix: Quick code snippets

**Circuit breaker (Resilience4j + annotations)**

```java
@CircuitBreaker(name = "inventory", fallbackMethod = "fallback")
public Item getItem(String id) { ... }

public Item fallback(String id, Throwable t) { /* fallback */ }
```

**Global exception handler**

```java
@RestControllerAdvice
public class GlobalHandler {
  @ExceptionHandler(ValidationException.class)
  public ResponseEntity<?> handle(ValidationException e) { return ResponseEntity.badRequest().body(...); }
}
```

---

## How to use this file

- Read top sections as high‑value talking points for interviews.
- Practice explaining tradeoffs, not just definitions; interviewers care about decisions and consequences.
- Convert snippets into small exercises (e.g., implement idempotency table + retry logic) and time-box yourself.

---

*Prepared by: Mohd Nayyar — Senior Software Engineer. Contact info: available on resume.*

