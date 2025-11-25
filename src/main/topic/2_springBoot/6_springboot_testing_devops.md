        # Advanced Spring Boot Interview Questions — Testing & DevOps Integration

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
