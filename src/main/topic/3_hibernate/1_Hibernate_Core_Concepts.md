
# 🧩 Core Hibernate Concepts — Interview Questions and Answers

## 1. What is Hibernate and how does it differ from JDBC or JPA?

**Answer:**
Hibernate is an **Object-Relational Mapping (ORM)** framework that automates the mapping between Java objects and database tables. It eliminates boilerplate JDBC code and handles SQL generation, caching, and transaction management.

**Differences:**
- **JDBC:** Manual SQL management, no caching or entity relationships.
- **JPA:** Specification (interface standard), while **Hibernate** is an **implementation** of JPA with additional features (e.g., advanced caching, HQL, lazy loading).

```java
// Example: Using Hibernate with JPA
@Entity
@Table(name = "employees")
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
}
```

---

## 2. What is the role of a Session and SessionFactory in Hibernate?

**Answer:**
- **SessionFactory:** A thread-safe, heavyweight object created once per application; used to create sessions.
- **Session:** Represents a single unit of work with the database; not thread-safe.

```java
SessionFactory factory = new Configuration().configure().buildSessionFactory();
Session session = factory.openSession();
session.beginTransaction();
// perform operations
session.getTransaction().commit();
session.close();
```

---

## 3. How does Hibernate manage object states (Transient, Persistent, Detached, Removed)?

**Answer:**
| State | Description |
|--------|--------------|
| Transient | Object not associated with session or database. |
| Persistent | Managed by Hibernate; changes auto-synced. |
| Detached | Was persistent but session closed. |
| Removed | Scheduled for deletion. |

```java
Employee e = new Employee(); // Transient
session.save(e); // Persistent
session.close(); // Detached
session.delete(e); // Removed
```

---

## 4. Explain the concept of ORM (Object Relational Mapping).

**Answer:**
ORM maps **Java objects ↔ Database tables**. It bridges the impedance mismatch between OOP and relational data.

Advantages:
- Eliminates manual SQL
- Easier maintenance
- Reduces boilerplate code

---

## 5. What is the purpose of hibernate.cfg.xml or persistence.xml?

**Answer:**
These configuration files define database connection properties, entity mappings, and Hibernate settings.

```xml
<hibernate-configuration>
  <session-factory>
    <property name="hibernate.connection.url">jdbc:mysql://localhost/db</property>
    <property name="hibernate.dialect">org.hibernate.dialect.MySQLDialect</property>
    <mapping class="com.example.Employee"/>
  </session-factory>
</hibernate-configuration>
```

`persistence.xml` serves the same purpose in **JPA-based** setups.

---

## 6. How does Hibernate manage entity identity and equality (equals() / hashCode())?

**Answer:**
Entities should override `equals()` and `hashCode()` based on **business keys**, not database IDs, to avoid inconsistent behavior before persistence.

```java
@Override
public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Employee)) return false;
    Employee e = (Employee) o;
    return Objects.equals(email, e.email);
}

@Override
public int hashCode() {
    return Objects.hash(email);
}
```

---

## 7. What is the difference between get() and load() methods?

**Answer:**
| Method | Behavior |
|---------|-----------|
| `get()` | Immediately hits the DB; returns null if not found. |
| `load()` | Returns a proxy; hits DB only when accessed; throws `ObjectNotFoundException` if not found. |

```java
Employee e1 = session.get(Employee.class, 1L);
Employee e2 = session.load(Employee.class, 2L);
```

---

## 8. How does Hibernate handle lazy loading vs eager fetching?

**Answer:**
- **Lazy Loading:** Data fetched on demand (default for `@OneToMany`).
- **Eager Loading:** Data fetched immediately with parent entity.

```java
@OneToMany(mappedBy = "department", fetch = FetchType.LAZY)
private List<Employee> employees;
```

Hibernate uses **proxies** for lazy-loaded entities and fetch joins for eager ones.

---

## 9. Explain cascading in Hibernate and different CascadeType options.

**Answer:**
Cascading allows related entities to be automatically persisted, merged, or deleted.

**CascadeType options:**
- `PERSIST` — Saves child when parent is saved
- `MERGE` — Merges changes automatically
- `REMOVE` — Deletes child with parent
- `REFRESH`, `DETACH`, `ALL`

```java
@OneToMany(mappedBy = "department", cascade = CascadeType.ALL)
private List<Employee> employees;
```

---

## 10. What’s the difference between merge() and update()?

**Answer:**
| Method | Description |
|---------|--------------|
| `update()` | Reattaches a **detached** entity directly. Throws error if another session holds same entity. |
| `merge()` | Creates a new managed instance with copied values. Safe for detached entities. |

```java
session.update(detachedEntity); // Error if already in session
Employee merged = (Employee) session.merge(detachedEntity);
```

---

**Summary:**  
This section covers Hibernate’s core mechanisms — sessions, configuration, ORM mapping, object states, and entity lifecycle management. Understanding these fundamentals is key before diving into relationships, caching, and optimization.
