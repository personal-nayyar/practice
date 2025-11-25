        # Advanced Spring Boot Interview Questions — Performance & Monitoring

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
