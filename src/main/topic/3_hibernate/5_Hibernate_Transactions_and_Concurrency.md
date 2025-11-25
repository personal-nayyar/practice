# 🧮 Transactions and Concurrency in Hibernate

This section covers how Hibernate manages transactions, concurrency, and locking mechanisms to ensure data consistency and integrity in multi-threaded or multi-user environments.

---

### 1. How does Hibernate manage transactions?
Hibernate provides an abstraction over transaction management using the `Transaction` API or through JTA (Java Transaction API).

**Example:**
```java
Session session = sessionFactory.openSession();
Transaction tx = session.beginTransaction();

Employee emp = new Employee("Alice", 5000);
session.save(emp);

tx.commit();  // commits transaction
session.close();
```

Hibernate ensures that all changes made within a transaction boundary are persisted atomically.

---

### 2. What’s the difference between @Transactional in Spring and Hibernate transaction management?
- **Hibernate Transaction** is manual and handled explicitly by developers.
- **Spring’s @Transactional** provides declarative transaction management.
  - It automatically begins, commits, or rolls back transactions based on runtime exceptions.
  - It integrates with multiple providers (Hibernate, JPA, JDBC).

**Example:**
```java
@Service
public class EmployeeService {
    @Transactional
    public void saveEmployee(Employee emp) {
        employeeRepository.save(emp);
    }
}
```

---

### 3. Explain optimistic vs pessimistic locking.

| Type | Description | Mechanism |
|------|--------------|------------|
| **Optimistic Locking** | Assumes no conflicts, checks version before update. | Uses `@Version` column. |
| **Pessimistic Locking** | Locks rows in DB to prevent concurrent updates. | Uses DB-level `SELECT ... FOR UPDATE`. |

**Optimistic Example:**
```java
@Entity
public class Account {
    @Id
    private Long id;

    @Version
    private int version;
}
```

---

### 4. How do you implement versioning in Hibernate (@Version annotation)?
You annotate a field (integer, timestamp, etc.) with `@Version`. Hibernate updates this version automatically after every successful commit.

**Example:**
```java
@Entity
public class Product {
    @Id
    private Long id;

    @Version
    private int version;
}
```
If another transaction modifies the same row first, Hibernate throws an `OptimisticLockException`.

---

### 5. What are dirty reads, non-repeatable reads, and phantom reads?

| Phenomenon | Description |
|-------------|--------------|
| **Dirty Read** | A transaction reads uncommitted changes from another transaction. |
| **Non-repeatable Read** | Data changes between two reads in the same transaction. |
| **Phantom Read** | New rows appear in a subsequent query within the same transaction. |

---

### 6. How can you handle concurrency conflicts gracefully?
- Use **Optimistic Locking** with `@Version`
- Catch `OptimisticLockException` and retry the transaction
- For critical updates, use **Pessimistic Locking**

**Example:**
```java
try {
    // Perform update
} catch (OptimisticLockException e) {
    // Retry or notify user
}
```

---

### 7. How does Hibernate participate in JTA (Java Transaction API)?
Hibernate integrates with JTA to manage distributed (XA) transactions across multiple resources (like DB + JMS).

Configuration example in `persistence.xml`:
```xml
<transaction-type>JTA</transaction-type>
<jta-data-source>java:/MyDataSource</jta-data-source>
```

---

### 8. What happens when you call flush() vs commit() on a session?

| Method | Description |
|---------|--------------|
| `flush()` | Synchronizes the session state with the database but does **not** commit the transaction. |
| `commit()` | Commits the transaction, finalizing all database changes. |

---

### 9. Explain propagation levels and isolation levels in Hibernate context.
- **Propagation Levels (Spring):**
  - `REQUIRED`, `REQUIRES_NEW`, `MANDATORY`, etc.
- **Isolation Levels:**
  - `READ_UNCOMMITTED`, `READ_COMMITTED`, `REPEATABLE_READ`, `SERIALIZABLE`
  - Configured via DB or Spring `@Transactional(isolation = Isolation.REPEATABLE_READ)`

---

### 10. What happens when you use Session.clear() or Session.evict()?
- **`clear()`** → Removes all entities from the session cache.
- **`evict(entity)`** → Removes a specific entity from the cache.

**Example:**
```java
session.evict(employee); // removes single object
session.clear(); // clears entire persistence context
```
