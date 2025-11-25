# Hibernate Interview Questions and Answers (With JPA Examples)

---

## 🧠 Core Hibernate Interview Questions

### 1. What is Hibernate?
Hibernate is an ORM (Object-Relational Mapping) framework for Java. It maps Java objects to database tables and manages database operations using object-oriented principles.

### 2. How does Hibernate differ from JDBC?
| Feature | Hibernate | JDBC |
|----------|------------|------|
| Abstraction | ORM abstraction over SQL | Requires manual SQL |
| Entity Management | Automatic mapping | Manual data handling |
| Caching | Built-in first and second-level caching | No caching support |
| Transactions | Managed by Hibernate | Must be managed manually |

### 3. What is an Entity in Hibernate?
An entity represents a table in a database. Each entity instance corresponds to a row.

```java
@Entity
@Table(name = "employees")
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dept_id")
    private Department department;
}
```

### 4. Explain Hibernate architecture.
Hibernate consists of components like:
- **SessionFactory** — Singleton instance for DB connections.
- **Session** — Represents a single unit of work.
- **Transaction** — Manages database operations atomically.
- **Query** — Used to perform HQL/SQL operations.
- **Configuration** — Reads `hibernate.cfg.xml`.

### 5. Difference between `Session` and `SessionFactory`.
- **SessionFactory**: Heavyweight, thread-safe object used to create Sessions.
- **Session**: Lightweight, not thread-safe, represents one conversation with the database.

### 6. What are Hibernate states?
1. **Transient** — Object not associated with a session.
2. **Persistent** — Object associated with a Hibernate session.
3. **Detached** — Object was persistent but session closed.
4. **Removed** — Object scheduled for deletion.

### 7. Explain cascading in Hibernate.
Cascade defines how operations (persist, merge, remove) propagate to related entities.
```java
@OneToMany(mappedBy = "department", cascade = CascadeType.ALL)
private List<Employee> employees;
```

### 8. What is lazy loading?
Lazy loading delays loading of related data until accessed.
```java
@ManyToOne(fetch = FetchType.LAZY)
private Department department;
```

### 9. What is the difference between `get()` and `load()`?
| Method | Behavior |
|---------|-----------|
| `get()` | Returns null if not found |
| `load()` | Throws `ObjectNotFoundException` if not found |

### 10. Explain first-level and second-level cache.
- **First-level cache** — Session-specific, enabled by default.
- **Second-level cache** — SessionFactory-level, must be explicitly enabled using providers like Ehcache or Infinispan.

```xml
<property name="hibernate.cache.use_second_level_cache">true</property>
<property name="hibernate.cache.region.factory_class">org.hibernate.cache.ehcache.EhCacheRegionFactory</property>
```

---

## ⚙️ Intermediate-Level Questions

### 11. What is HQL?
Hibernate Query Language (HQL) is an object-oriented query language similar to SQL but operates on entity objects.
```java
String hql = "FROM Employee e WHERE e.department.name = :dept";
List<Employee> employees = session.createQuery(hql, Employee.class)
    .setParameter("dept", "HR")
    .list();
```

### 12. Difference between HQL and Criteria API.
| Aspect | HQL | Criteria API |
|--------|-----|---------------|
| Syntax | String-based | Type-safe Java API |
| Flexibility | Less dynamic | More flexible and composable |

### 13. What are native SQL queries in Hibernate?
Hibernate allows execution of raw SQL queries:
```java
List<Object[]> result = session.createNativeQuery("SELECT * FROM employees").list();
```

### 14. How to handle transactions in Hibernate?
```java
Transaction tx = session.beginTransaction();
session.save(emp);
tx.commit();
```

### 15. What are `@Embeddable` and `@Embedded`?
Used for value-type composition.
```java
@Embeddable
public class Address {
    private String city;
    private String state;
}

@Entity
public class Employee {
    @Embedded
    private Address address;
}
```

### 16. Explain optimistic and pessimistic locking.
- **Optimistic Locking**: Uses versioning to detect conflicts.
- **Pessimistic Locking**: Locks the row in DB until transaction completes.

```java
@Version
private int version;
```

### 17. How do you batch insert in Hibernate?
```java
Session session = sessionFactory.openSession();
Transaction tx = session.beginTransaction();
for (int i = 0; i < 1000; i++) {
    session.save(new Employee("Emp" + i));
    if (i % 50 == 0) {
        session.flush();
        session.clear();
    }
}
tx.commit();
```

### 18. Explain `@OneToOne`, `@OneToMany`, and `@ManyToMany`.
```java
@OneToOne(mappedBy = "employee")
private Passport passport;

@OneToMany(mappedBy = "department")
private List<Employee> employees;

@ManyToMany
@JoinTable(
  name = "student_course",
  joinColumns = @JoinColumn(name = "student_id"),
  inverseJoinColumns = @JoinColumn(name = "course_id"))
private List<Course> courses;
```

### 19. What is dirty checking in Hibernate?
Hibernate automatically detects changes in persistent objects and synchronizes them with the database when the session is flushed.

### 20. What are interceptors in Hibernate?
Interceptors allow custom logic during lifecycle events.
```java
public class AuditInterceptor extends EmptyInterceptor {
    @Override
    public boolean onSave(Object entity, Serializable id, Object[] state,
                          String[] propertyNames, Type[] types) {
        System.out.println("Entity Saved: " + entity);
        return super.onSave(entity, id, state, propertyNames, types);
    }
}
```

---

## 🚀 Advanced Hibernate Interview Questions

### 21. How to improve Hibernate performance?
- Enable second-level and query caching.
- Use batch fetching.
- Minimize joins with lazy loading.
- Use projections when full entity load isn’t required.

### 22. What is `@Fetch(FetchMode.JOIN)`?
Specifies fetch strategy.
```java
@OneToMany(fetch = FetchType.LAZY)
@Fetch(FetchMode.JOIN)
private List<Employee> employees;
```

### 23. What are the differences between `merge()` and `update()`?
| Method | Behavior |
|---------|-----------|
| `update()` | Fails if detached instance exists in session |
| `merge()` | Copies state to persistent instance |

### 24. Explain `@Inheritance` strategies.
- `SINGLE_TABLE`
- `JOINED`
- `TABLE_PER_CLASS`

```java
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class Payment {}

@Entity
public class CreditCardPayment extends Payment {}
```

### 25. How do you enable Hibernate statistics?
```xml
<property name="hibernate.generate_statistics">true</property>
```

```java
Statistics stats = sessionFactory.getStatistics();
System.out.println("Entity fetch count: " + stats.getEntityFetchCount());
```

---

## 💾 Bonus Section 1: Caching in Hibernate

### First-Level Cache
Default cache at the Session level. Cannot be disabled.

### Second-Level Cache
Shared among sessions.
```xml
<property name="hibernate.cache.use_second_level_cache">true</property>
<property name="hibernate.cache.region.factory_class">org.hibernate.cache.ehcache.EhCacheRegionFactory</property>
```

### Query Cache
```xml
<property name="hibernate.cache.use_query_cache">true</property>
```

---

## ☁️ Bonus Section 2: Multi-Tenancy in Hibernate

### Approaches
1. **DATABASE** — Separate databases per tenant.
2. **SCHEMA** — Separate schema per tenant.
3. **DISCRIMINATOR** — Shared schema, tenant column used.

```java
public class SchemaBasedTenantIdentifierResolver implements CurrentTenantIdentifierResolver {
    public String resolveCurrentTenantIdentifier() {
        return TenantContext.getCurrentTenant();
    }
}
```

---

## 🧩 Bonus Section 3: Common Interview Code Tasks

### Example: Fetch employees with department using Criteria
```java
CriteriaBuilder cb = session.getCriteriaBuilder();
CriteriaQuery<Employee> cq = cb.createQuery(Employee.class);
Root<Employee> root = cq.from(Employee.class);
root.fetch("department", JoinType.LEFT);
List<Employee> employees = session.createQuery(cq).getResultList();
```

### Example: Pagination
```java
Query<Employee> query = session.createQuery("FROM Employee", Employee.class);
query.setFirstResult(0);
query.setMaxResults(50);
List<Employee> result = query.list();
```

### Example: Dynamic filtering
```java
@Entity
@FilterDef(name = "activeFilter", parameters = @ParamDef(name = "isActive", type = "boolean"))
@Filters({ @Filter(name = "activeFilter", condition = "active = :isActive") })
public class Employee {}
```

---

## 🏁 Conclusion
This guide summarizes **frequently asked Hibernate interview questions** with **JPA examples**, **code snippets**, and **performance tips**. Perfect for senior developers preparing for backend or full-stack interviews.
