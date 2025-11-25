# 🔐 Common Issues & Best Practices in Hibernate

This section outlines frequent performance, design, and debugging issues encountered while working with Hibernate, along with recommended solutions and best practices.

---

### 1. What are common performance issues with Hibernate?
- **N+1 Select Problem:** Too many SQL queries for lazy associations.
- **Unnecessary Fetching:** Eager loading of unused relationships.
- **No Batching:** Missing `hibernate.jdbc.batch_size` configuration.
- **Inefficient Caching:** Misuse of second-level cache for write-heavy entities.

**Solution:** Use fetch joins, proper indexing, batch fetching, and profiling tools.

---

### 2. How to avoid memory leaks or session bloat in long-running transactions?
- Avoid keeping Hibernate sessions open for long durations.
- Use **session-per-request** pattern.
- Clear session cache periodically with `session.clear()`.
- Use **StatelessSession** for batch operations.

**Example:**
```java
for (int i = 0; i < employees.size(); i++) {
    session.save(employees.get(i));
    if (i % 20 == 0) {
        session.flush();
        session.clear();
    }
}
```

---

### 3. How to prevent lazy initialization exceptions?
Occurs when accessing lazy-loaded associations outside a session.

**Solutions:**
1. Use **JOIN FETCH** in HQL/JPQL.
2. Use **OpenSessionInView** (with caution).
3. Initialize collections explicitly inside the session.

**Example:**
```java
Employee emp = session.get(Employee.class, 1L);
Hibernate.initialize(emp.getDepartment());
```

---

### 4. What is the best way to handle DTO projections in Hibernate?
- Use **JPQL constructor expressions** or **CriteriaBuilder** projections.
- Reduces memory usage and speeds up query execution.

**Example:**
```java
List<EmployeeDTO> list = session.createQuery(
    "SELECT new com.example.EmployeeDTO(e.name, e.salary) FROM Employee e", EmployeeDTO.class
).getResultList();
```

---

### 5. Why is equals()/hashCode() important for persistent entities?
Hibernate uses these methods to track entity identity in sets and maps.

**Best Practice:**
- Base `equals()` and `hashCode()` on immutable unique fields (like `id` or `businessKey`).
- Avoid using generated IDs for transient entities.

**Example:**
```java
@Override
public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Employee)) return false;
    Employee e = (Employee) o;
    return Objects.equals(email, e.email);
}
```

---

### 6. How do you handle cascading deletes safely?
- Use `CascadeType.REMOVE` carefully; can cause unintended deletions.
- Prefer manual deletion or use `orphanRemoval = true` for child-only removal.

**Example:**
```java
@OneToMany(mappedBy = "dept", cascade = CascadeType.ALL, orphanRemoval = true)
private List<Employee> employees;
```

---

### 7. Why should you avoid bidirectional relationships unless necessary?
- Adds complexity and risk of circular references.
- Requires careful synchronization between owning/inverse sides.
- Can lead to infinite recursion in JSON serialization.

**Best Practice:** Use unidirectional mappings where possible.

---

### 8. What’s the impact of using FetchType.EAGER?
- Loads related entities **immediately**, even if not needed.
- Can lead to performance degradation and N+1 queries.

**Recommendation:** Default to `LAZY` and use explicit fetching when required.

---

### 9. How can you debug generated SQL queries in Hibernate?
Enable SQL logging and formatting in configuration.

**Example (application.properties):**
```properties
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE
```

---

### 10. What are the common pitfalls when using Hibernate with microservices?
- Shared databases across services cause coupling.
- Schema migrations can break independently deployed services.
- Cache invalidation becomes complex in distributed systems.
- Lazy loading across service boundaries is not supported.

**Best Practices:**
- Use **DTOs** or **API composition** for cross-service data.
- Each microservice should own its schema.
- Use database versioning tools like Flyway or Liquibase.
