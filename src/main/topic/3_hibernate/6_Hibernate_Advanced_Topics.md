# 🚀 Advanced Topics in Hibernate

This section explores internal mechanisms, integration patterns, and advanced features in Hibernate used for performance tuning, extensibility, and architectural scalability.

---

### 1. How does Hibernate handle lazy loading proxies internally?
- Hibernate uses **bytecode enhancement** or **runtime proxies** (via CGLIB or ByteBuddy).
- When you mark a relationship as `LAZY`, Hibernate creates a proxy subclass of the entity.
- The actual data is fetched only when a method on the proxy is invoked.

**Example:**
```java
@Entity
public class Department {
    @OneToMany(fetch = FetchType.LAZY)
    private List<Employee> employees;
}
```
If `employees` is never accessed, Hibernate won’t hit the database.

---

### 2. Explain the difference between save(), persist(), and saveOrUpdate().

| Method | Description | Returns | Cascade |
|---------|--------------|----------|----------|
| **save()** | Immediately inserts record (even without transaction). | Serializable ID | Legacy Hibernate |
| **persist()** | JPA-compliant, makes entity managed but insert happens on flush. | void | JPA standard |
| **saveOrUpdate()** | Saves new or updates existing based on identifier presence. | void | Hibernate-specific |

**Example:**
```java
session.save(emp);
session.persist(emp);
session.saveOrUpdate(emp);
```

---

### 3. What are “dirty checking” and “automatic flushing”?
- **Dirty Checking:** Hibernate detects modified entities by comparing snapshot state with current state.
- **Automatic Flushing:** Before executing a query or committing, Hibernate synchronizes changes in memory with the DB.

**Example:**
```java
employee.setSalary(80000); // Hibernate detects change
session.flush(); // SQL UPDATE fired automatically
```

---

### 4. How does Hibernate’s event system work (listeners, interceptors)?
Hibernate provides hooks for listening to entity lifecycle events:
- PreInsert, PostInsert, PreUpdate, PostUpdate, PreDelete, PostDelete, etc.

**Using an Event Listener:**
```java
public class AuditListener {
    @PrePersist
    public void beforeInsert(Object entity) {
        System.out.println("Before insert: " + entity);
    }
}
```

**Using Interceptor:**
```java
public class AuditInterceptor extends EmptyInterceptor {
    @Override
    public boolean onFlushDirty(Object entity, Serializable id, Object[] currentState,
                               Object[] previousState, String[] propertyNames, Type[] types) {
        // custom logic
        return true;
    }
}
```

---

### 5. What is the difference between Hibernate Interceptor and EventListener?

| Aspect | Interceptor | EventListener |
|---------|--------------|---------------|
| Scope | Session or global | Configurable per event type |
| Flexibility | Limited methods | Fine-grained event control |
| Use Case | Logging, auditing | Lifecycle handling (pre/post events) |

---

### 6. How do you integrate Hibernate with Spring Boot or Jakarta EE?
**Spring Boot Integration:**
- Add `spring-boot-starter-data-jpa`
- Define entities and repositories
- Configure `application.yml` with datasource and dialect

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/test
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
```

**Jakarta EE Integration:**
- Use `@PersistenceContext` and `EntityManager` injection
- Manage transactions with JTA

---

### 7. How does Hibernate handle database schema generation (hbm2ddl.auto)?

| Option | Description |
|---------|--------------|
| `validate` | Validates schema without modifying |
| `update` | Updates schema if necessary |
| `create` | Drops and recreates schema each startup |
| `create-drop` | Creates schema and drops on shutdown |

**Example:**
```properties
hibernate.hbm2ddl.auto=update
```

---

### 8. What is StatelessSession and when would you use it?
- A **StatelessSession** does not maintain a persistence context or cache.
- It is suitable for **bulk inserts, updates, and reads** when you don’t need lifecycle callbacks or caching.

**Example:**
```java
StatelessSession session = sessionFactory.openStatelessSession();
Transaction tx = session.beginTransaction();

session.insert(new Employee("John", 50000));

tx.commit();
session.close();
```

---

### 9. What is multi-tenancy in Hibernate and how can it be implemented?
Multi-tenancy allows multiple tenants (clients) to share a single application instance.

**Types:**
1. **DATABASE** → Each tenant has its own database.
2. **SCHEMA** → Shared DB, separate schemas.
3. **DISCRIMINATOR** → Shared table, tenant column filter.

**Example (Schema-based):**
```properties
hibernate.multiTenancy=SCHEMA
hibernate.tenant_identifier_resolver=com.example.TenantResolver
```

---

### 10. How does Hibernate handle entity graphs (@NamedEntityGraph) for fetch control?
Entity graphs define how associations are fetched dynamically without modifying mappings.

**Example:**
```java
@Entity
@NamedEntityGraph(
    name = "Employee.detail",
    attributeNodes = @NamedAttributeNode("department")
)
public class Employee { ... }

EntityGraph<?> graph = em.getEntityGraph("Employee.detail");
Map<String, Object> props = new HashMap<>();
props.put("javax.persistence.fetchgraph", graph);

Employee emp = em.find(Employee.class, 1L, props);
```
This allows **dynamic fetch optimization** to reduce unnecessary joins.
