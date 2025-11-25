# 🧩 9. Practical / Coding-Oriented Hibernate Questions

This section covers **hands-on Hibernate questions** with practical Java examples for interviews.

---

### 1. Write a query to fetch all employees with salary > X using HQL

```java
String hql = "FROM Employee e WHERE e.salary > :minSalary";
List<Employee> employees = session.createQuery(hql, Employee.class)
        .setParameter("minSalary", 50000.0)
        .getResultList();
```
✅ **Explanation:**  
HQL operates on entities, not tables. It’s case-sensitive for entity names and supports parameters via `:namedParam`.

---

### 2. Fetch an entity with its lazy-loaded child entities efficiently

```java
String hql = "SELECT e FROM Department e JOIN FETCH e.employees WHERE e.id = :deptId";
Department dept = session.createQuery(hql, Department.class)
        .setParameter("deptId", 1L)
        .uniqueResult();
```
✅ **Explanation:**  
Using `JOIN FETCH` avoids `LazyInitializationException` and fetches associated entities in a single SQL query.

---

### 3. Configure a second-level cache for Employee entity using Ehcache

**Step 1:** Add configuration in `hibernate.cfg.xml`

```xml
<hibernate-configuration>
  <session-factory>
    <property name="hibernate.cache.use_second_level_cache">true</property>
    <property name="hibernate.cache.region.factory_class">
        org.hibernate.cache.ehcache.EhCacheRegionFactory
    </property>
  </session-factory>
</hibernate-configuration>
```

**Step 2:** Annotate the entity

```java
@Entity
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Employee {
    @Id
    private Long id;
    private String name;
    private double salary;
}
```

✅ **Explanation:**  
Ehcache stores entity data across sessions for faster lookups without repeated DB hits.

---

### 4. Demonstrate pagination in Hibernate query results

```java
String hql = "FROM Employee ORDER BY id";
List<Employee> employees = session.createQuery(hql, Employee.class)
        .setFirstResult(0)      // offset
        .setMaxResults(10)      // limit
        .getResultList();
```
✅ **Explanation:**  
Use `setFirstResult()` and `setMaxResults()` for pagination. Useful for UI-driven pagination or API responses.

---

### 5. Example of optimistic locking failure scenario and resolution

**Entity:**

```java
@Entity
public class Product {
    @Id
    private Long id;

    @Version
    private int version;

    private double price;
}
```

**Scenario:**  
- User A and User B load the same Product (version = 1).  
- Both modify the price.  
- User A commits first → version increments to 2.  
- When User B commits, Hibernate detects version mismatch and throws `OptimisticLockException`.

✅ **Resolution:**  
Handle via retry logic or user notification in service layer.

---

### 🧠 Summary

These questions emphasize **real-world Hibernate usage**, from query design to caching and concurrency handling.
