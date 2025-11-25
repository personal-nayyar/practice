# Advanced Spring Boot Interview Questions — Data Access (Spring Data JPA, Transactions)

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
- Database migrations automate and version schema changes using tools like Flyway or Liquibase, ensuring consistency, traceability, and zero manual intervention across environments.
- Use Flyway or Liquibase scripts executed at startup.

### 10. How to test repositories?
- Use `@DataJpaTest` for repository-layer integration tests with H2 DB.
