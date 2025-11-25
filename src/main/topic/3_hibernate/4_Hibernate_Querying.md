# 🧾 Hibernate Querying in Hibernate — Interview Questions & Answers

## 1. Explain the differences between:
### HQL (Hibernate Query Language)
- Object-oriented query language similar to SQL but works with entity names and properties instead of table names and columns.
- Database-agnostic and automatically translated to SQL by Hibernate.
- Example: `from Employee e where e.department = :dept`

### Criteria API
- Type-safe, programmatic approach to building queries.
- Useful for dynamic query construction.
- Example:
  ```java
  CriteriaBuilder cb = session.getCriteriaBuilder();
  CriteriaQuery<Employee> cq = cb.createQuery(Employee.class);
  Root<Employee> root = cq.from(Employee.class);
  cq.select(root).where(cb.equal(root.get("department"), "HR"));
  List<Employee> results = session.createQuery(cq).getResultList();
  ```

### Native SQL
- Uses raw SQL queries directly against the database.
- Useful when leveraging database-specific features.
- Example: `session.createNativeQuery("SELECT * FROM employee WHERE dept = ?", Employee.class)`

### JPQL (Java Persistence Query Language)
- Standardized query language defined by JPA.
- Very similar to HQL but JPA-compliant.
- Example: `SELECT e FROM Employee e WHERE e.department = :dept`

---

## 2. How does Hibernate translate HQL to SQL under the hood?
Hibernate uses the **QueryTranslatorFactory** to parse HQL into an **AST (Abstract Syntax Tree)**, then translates it to SQL according to the underlying database dialect. The **SessionFactory** maintains metadata mappings, ensuring entity attributes are mapped to corresponding database columns.

---

## 3. How can you use projections or aggregations in Criteria queries?
You can use **CriteriaBuilder** methods like `count()`, `sum()`, `avg()`, `max()`, and `min()` for aggregations. Projections allow selecting only specific columns instead of full entities.

Example:
```java
CriteriaBuilder cb = session.getCriteriaBuilder();
CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);
Root<Employee> root = cq.from(Employee.class);
cq.multiselect(root.get("department"), cb.avg(root.get("salary")));
cq.groupBy(root.get("department"));
List<Object[]> results = session.createQuery(cq).getResultList();
```

---

## 4. What is the difference between createQuery() and createNativeQuery()?
| Method | Description |
|--------|--------------|
| **createQuery()** | Used for HQL or JPQL queries (entity-based). |
| **createNativeQuery()** | Used for raw SQL queries directly executed on the database. |

---

## 5. How do you prevent SQL injection when using dynamic queries in Hibernate?
- Always use **named parameters** (`:paramName`) instead of string concatenation.
- Example:
  ```java
  Query<Employee> query = session.createQuery("from Employee e where e.name = :name", Employee.class);
  query.setParameter("name", userInput);
  ```
- Avoid concatenating user input directly into query strings.

---

## 6. How do you handle pagination using Hibernate (setFirstResult(), setMaxResults())?
Hibernate supports pagination using query methods:
```java
Query<Employee> query = session.createQuery("from Employee", Employee.class);
query.setFirstResult(10);  // starting row
query.setMaxResults(20);   // number of results to fetch
List<Employee> employees = query.list();
```
This approach ensures efficient data retrieval for large datasets.

---

## 7. What is the @NamedQuery annotation and when is it useful?
`@NamedQuery` defines a static, reusable HQL query at the entity level.
```java
@Entity
@NamedQuery(name="Employee.findByDept", query="from Employee e where e.department = :dept")
public class Employee { ... }
```
It is precompiled at startup, reducing parsing overhead and promoting reusability.

---

## 8. What are the advantages of the Criteria API over HQL for complex queries?
- Type-safe and IDE-friendly (detects field/property errors at compile time).
- Easier to construct dynamic queries.
- Reduces risk of runtime syntax errors.
- Useful for modular or layered systems.

---

## 9. Explain how the JOIN FETCH clause works and when to use it.
`JOIN FETCH` allows fetching associated entities eagerly in a single query, reducing the N+1 select problem.
```java
from Department d JOIN FETCH d.employees where d.id = :id
```
It ensures both `Department` and its `employees` are loaded together efficiently.

---

## 10. What are QueryHints and how are they used?
**QueryHints** provide vendor-specific optimizations (like cache usage, timeouts, or fetch size).
Example using JPA:
```java
Query query = em.createQuery("from Employee e");
query.setHint("org.hibernate.cacheable", true);
query.setHint("javax.persistence.query.timeout", 5000);
```
They help fine-tune performance without modifying the query itself.
