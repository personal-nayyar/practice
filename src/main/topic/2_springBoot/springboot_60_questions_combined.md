# Advanced Spring Boot Interview Questions — Combined (All Sections)

This document combines all 60 questions (10 per section) from the six Spring Boot sections.

---

## Advanced Spring Boot Interview Questions — Core Concepts


### 1. What are some key differences between Spring and Spring Boot?
- Spring Boot simplifies Spring configuration by providing auto-configuration, embedded servers, and starter dependencies.
- Traditional Spring requires explicit XML or Java configuration, whereas Boot focuses on convention over configuration.

### 2. How does Spring Boot auto-configuration work internally?
- It uses the `@EnableAutoConfiguration` annotation.
- Auto-config classes are loaded via `spring.factories` under `META-INF`.
- Conditions like `@ConditionalOnClass`, `@ConditionalOnMissingBean` control whether beans are instantiated.

### 3. What’s the purpose of `@SpringBootApplication` annotation?
- Combines `@Configuration`, `@EnableAutoConfiguration`, and `@ComponentScan`.
- It marks the main entry point for Spring Boot applications.

### 4. Explain how Spring Boot handles profiles.
- Profiles isolate environment-specific configurations using `@Profile` and `application-{profile}.yml`.
- Activate via `--spring.profiles.active=prod`.

### 5. How does Spring Boot support externalized configuration?
- Uses the `Environment` abstraction.
- Config values can come from properties, YAML files, environment variables, command-line args, or config servers.

### 6. What are Spring Boot starters and why are they useful?
- Predefined dependency bundles (e.g., `spring-boot-starter-web`, `spring-boot-starter-data-jpa`).
- Simplify dependency management and version compatibility.

### 7. How does Spring Boot manage application startup?
- The `SpringApplication` class bootstraps the app context.
- Uses `ApplicationContextInitializer` and `ApplicationListener` to customize startup.

### 8. How can you customize the banner in a Spring Boot application?
- Replace `banner.txt` in `resources/`.
- Or set `spring.main.banner-mode=off` to disable.

### 9. What’s the role of `CommandLineRunner` and `ApplicationRunner`?
- Execute code after Spring context loads.
- Useful for initializing data or triggering background tasks.

### 10. Explain how Spring Boot detects and configures embedded web servers.
- Detects server type from classpath (e.g., Tomcat, Jetty, Undertow).
- Configures a matching `WebServerFactory` bean automatically.

---


## Advanced Spring Boot Interview Questions — Microservices & Architecture


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
- Logs aggregated via ELK stack (Elasticsearch, Logstash, Kibana) or Loki + Grafana.

### 9. How can you version APIs?
- Use URL-based (`/v1/api`) or Header-based (`X-API-VERSION`) versioning.

### 10. How do you handle inter-service communication security?
- Use JWT or OAuth2 tokens validated by Gateway or Resource Server.
- Enable HTTPS and optionally mTLS.

---


## Advanced Spring Boot Interview Questions — Data Access (Spring Data JPA, Transactions)


### 1. How does Spring Data JPA simplify data access?
- Removes boilerplate DAO code using repository interfaces.
- Generates queries from method names.

### 2. Difference between JpaRepository and CrudRepository?
- JpaRepository extends CrudRepository and adds JPA-specific methods.

### 3. How to define custom queries?
```java
@Query("SELECT u FROM User u WHERE u.email = :email")
User findByEmail(@Param("email") String email);
```

### 4. How does transaction management work?
- Enabled via `@EnableTransactionManagement`.
- `@Transactional` defines transactional boundaries with rollback rules.

### 5. What happens when exceptions occur in a transaction?
- Runtime exceptions trigger rollback automatically.
- Checked exceptions need explicit `rollbackFor` config.

### 6. Explain lazy loading in JPA.
- Entities fetched on-demand unless marked as EAGER.
- Access outside transaction causes `LazyInitializationException`.

### 7. How can you improve performance?
- Use pagination, DTO projections, second-level caching, and indexing.

### 8. What is EntityManager?
- Manages entities, queries, and transactions.

### 9. How to handle database migrations?
- Use Flyway or Liquibase scripts executed at startup.

### 10. How to test repositories?
- Use `@DataJpaTest` for repository-layer integration tests with H2 DB.

---


## Advanced Spring Boot Interview Questions — Security (Spring Security & OAuth2)


### 1. How does Spring Security integrate with Spring Boot?
        - Auto-configures authentication/authorization filters by default.

        ### 2. Explain the security filter chain.
        - Ordered filters manage authentication and authorization via `SecurityFilterChain`.

        ### 3. How does authentication work?
        - `AuthenticationManager` validates credentials and stores result in `SecurityContextHolder`.

        ### 4. Common security annotations?
        - `@EnableWebSecurity`, `@PreAuthorize`, `@Secured`, and `@PostAuthorize`.

        ### 5. How to customize authentication?
        - Implement `UserDetailsService` and provide custom `PasswordEncoder`.

        ### 6. JWT authentication flow?
        - User logs in → server issues JWT → client sends JWT in headers → server validates signature and roles.

        ### 7. OAuth2 in Spring Boot?
- Framework for delegated authorization using clients, resource owners, and resource servers.

        ### 8. Difference between OAuth2 roles?
        - **Client**: app requesting access.
- **Resource Owner**: user.
- **Resource Server**: API provider.

        ### 9. How to secure REST APIs?
        - JWT/OAuth2, HTTPS, and CSRF protection.
- Use rate limiting and input validation.

        ### 10. How to enable method-level security?
        - `@EnableGlobalMethodSecurity(prePostEnabled = true)` + annotations like `@PreAuthorize("hasRole('ADMIN')")`.

---


## Advanced Spring Boot Interview Questions — Performance & Monitoring


### 1. How to improve startup time?
        - Disable unused auto-configurations.
- Use lazy initialization and profile startup metrics.

        ### 2. How to monitor Spring Boot apps?
        - Use Actuator endpoints, Prometheus, Grafana, or Micrometer.

        ### 3. What is Micrometer?
        - Vendor-neutral metrics collection for Prometheus, Datadog, etc.

        ### 4. Detect memory leaks?
- Use profiling tools (JProfiler, VisualVM) and GC logs.

        ### 5. Improve DB performance?
- Use HikariCP pooling, caching, and query tuning.

        ### 6. Caching strategies?
- Use `@Cacheable` and integrate Redis/Ehcache/Caffeine.

        ### 7. Manage thread pools?
- Configure `TaskExecutor` and monitor queue metrics.

        ### 8. Load testing?
- JMeter, Gatling, or k6 for stress and endurance tests.

        ### 9. Handle high concurrency?
- Use non-blocking I/O with WebFlux and avoid long transactions.

        ### 10. Setup alerts?
- Use Prometheus AlertManager or Grafana alerts integrated with Slack or PagerDuty.

---


## Advanced Spring Boot Interview Questions — Testing & DevOps Integration


### 1. How do you write unit tests?
- Use JUnit 5 and Mockito with `@SpringBootTest`, `@WebMvcTest`, or `@DataJpaTest`.

        ### 2. Difference between @SpringBootTest and @WebMvcTest?
- Full vs controller-layer test context.

        ### 3. Mock dependencies?
- `@MockBean` with Mockito `when()` and `verify()`.

        ### 4. Test REST APIs?
- MockMvc or RestAssured.

        ### 5. Manage test data?
- Use H2, @Sql, or @DataJpaTest.

### 6. CI/CD setup?
- Jenkins, GitHub Actions, GitLab CI with build-test-deploy.

        ### 7. Containerization?
- Multi-stage Docker builds with OpenJDK base image.

        ### 8. Kubernetes deployment?
- Use Deployment, Service, ConfigMap, and readiness/liveness probes.

### 9. Observability?
- Micrometer + Prometheus + Grafana + OpenTelemetry.

### 10. Testcontainers?
- Use for DB/Kafka integration tests.
  ```java
  @Container static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15");
  ```

---

