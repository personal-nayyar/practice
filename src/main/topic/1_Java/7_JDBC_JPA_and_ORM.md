# ⚙️ JDBC, JPA, and ORM

### 1. Difference between JDBC, Hibernate, and JPA
- **JDBC (Java Database Connectivity)** is a low-level API that allows Java programs to connect and execute SQL statements directly against databases.
- **Hibernate** is an ORM (Object Relational Mapping) framework that abstracts database interactions using objects instead of SQL.
- **JPA (Java Persistence API)** is a specification for ORM. Hibernate is one of its implementations.
- **Summary:**
  - JDBC → Direct SQL
  - Hibernate → ORM implementation
  - JPA → ORM specification

### 2. How does connection pooling improve performance?
- Creating and destroying database connections for each request is expensive.
- **Connection pooling** reuses existing database connections, reducing latency and improving throughput.
- Libraries like **HikariCP**, **C3P0**, or **Apache DBCP** manage these pools.
- Key parameters: `maxPoolSize`, `connectionTimeout`, `idleTimeout`.

### 3. Explain N+1 problem and how to resolve it.
- Occurs when fetching a parent entity triggers N additional queries for its child entities.
- Example: Fetching 100 employees, each with 1 department, executes 1 + 100 queries.
- **Solutions:**
  - Use `fetch join` (JPQL: `SELECT e FROM Employee e JOIN FETCH e.department`).
  - Configure **FetchType.LAZY** appropriately.
  - Use **EntityGraph** or **BatchSize** in Hibernate.

### 4. What are transaction isolation levels?
Defines how transactions interact with each other in concurrent environments:
- **READ_UNCOMMITTED** – May see uncommitted data (dirty read).
- **READ_COMMITTED** – Prevents dirty reads.
- **REPEATABLE_READ** – Prevents dirty and non-repeatable reads.
- **SERIALIZABLE** – Strictest; prevents all concurrency anomalies.
- Controlled via database configuration or annotations: `@Transactional(isolation = Isolation.SERIALIZABLE)`.

### 5. What is the role of EntityManager vs SessionFactory?
- **EntityManager (JPA)**: Interface managing entities in persistence context (CRUD operations).
- **SessionFactory (Hibernate)**: Creates and manages **Session** objects; similar role but Hibernate-specific.
- `EntityManager` is thread-safe; a `Session` is not.
- In Spring, `EntityManager` is typically injected via `@PersistenceContext`.

### 6. How to handle lazy loading exceptions?
- Happens when accessing a lazy-loaded entity outside an open session (e.g., in the view layer).
- **Solutions:**
  - Use **fetch joins** in queries.
  - Use **OpenSessionInViewFilter** (carefully).
  - Initialize required relationships before closing session (`Hibernate.initialize(entity.getChild())`).
  - Avoid accessing lazy fields outside transactional context.

### 7. Explain @Transactional propagation in Spring.
- Controls how transactions relate when one method calls another transactional method.
- Common types:
  - **REQUIRED** (default): Join existing or create new transaction.
  - **REQUIRES_NEW**: Always starts a new transaction.
  - **SUPPORTS**: Runs in existing transaction if available.
  - **MANDATORY**: Must run within an existing transaction.
  - **NEVER**: Must not run within a transaction.
  - **NESTED**: Runs within a nested transaction (if supported).
- Example:
  ```java
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void processData() { ... }
  ```
