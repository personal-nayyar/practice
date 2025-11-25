
# 🧠 Entity Mapping & Relationships — Interview Questions and Answers

## 1. Explain the various types of associations in Hibernate

### a. One-to-One
Represents a one-to-one mapping between two entities (each record corresponds to exactly one in the other).

```java
@Entity
public class Employee {
    @Id
    private Long id;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "address_id")
    private Address address;
}

@Entity
public class Address {
    @Id
    private Long id;
    private String city;
}
```

### b. One-to-Many / Many-to-One
A department has many employees (One-to-Many), and each employee belongs to one department (Many-to-One).

```java
@Entity
public class Department {
    @Id
    private Long id;

    @OneToMany(mappedBy = "department", cascade = CascadeType.ALL)
    private List<Employee> employees;
}

@Entity
public class Employee {
    @Id
    private Long id;

    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department;
}
```

### c. Many-to-Many
Represents a many-to-many relationship using a join table.

```java
@Entity
public class Student {
    @Id
    private Long id;
    private String name;

    @ManyToMany
    @JoinTable(
        name = "student_course",
        joinColumns = @JoinColumn(name = "student_id"),
        inverseJoinColumns = @JoinColumn(name = "course_id")
    )
    private Set<Course> courses;
}

@Entity
public class Course {
    @Id
    private Long id;
    private String title;
}
```

---

## 2. What are mappedBy, joinColumn, and inverse attributes used for?

**Answer:**
- `mappedBy` — Defines the **inverse side** of a bidirectional relationship (used in OneToMany/ManyToMany).
- `@JoinColumn` — Specifies the **foreign key** column in the owning entity.
- `inverse` — (XML-based) marks the **non-owning side** of the relationship.

```java
@OneToMany(mappedBy = "department")
private List<Employee> employees;

@ManyToOne
@JoinColumn(name = "department_id")
private Department department;
```

---

## 3. How do you map a composite primary key using @EmbeddedId or @IdClass?

### Using `@EmbeddedId`
```java
@Embeddable
public class OrderId implements Serializable {
    private Long orderId;
    private Long productId;
}

@Entity
public class Order {
    @EmbeddedId
    private OrderId id;
    private int quantity;
}
```

### Using `@IdClass`
```java
@Entity
@IdClass(OrderId.class)
public class Order {
    @Id
    private Long orderId;
    @Id
    private Long productId;
    private int quantity;
}
```

**Difference:**
- `@EmbeddedId` — Encapsulates composite key as an embedded class.
- `@IdClass` — Uses multiple `@Id` fields directly.

---

## 4. How can you handle bidirectional relationships and avoid circular references?

**Answer:**
Use `mappedBy` on the inverse side and control JSON serialization using annotations like `@JsonManagedReference` and `@JsonBackReference` (for REST APIs).

```java
@Entity
public class Department {
    @OneToMany(mappedBy = "department")
    @JsonManagedReference
    private List<Employee> employees;
}

@Entity
public class Employee {
    @ManyToOne
    @JsonBackReference
    private Department department;
}
```

**Avoid:** Recursive toString() or equals() methods referencing both sides.

---

## 5. What is an @Embeddable class?

**Answer:**
`@Embeddable` allows embedding a class as a value type inside an entity without being an entity itself.

```java
@Embeddable
public class Address {
    private String city;
    private String zipCode;
}

@Entity
public class Employee {
    @Id
    private Long id;

    @Embedded
    private Address address;
}
```

It promotes **composition over inheritance** and reusability.

---

## 6. How can you map inheritance in Hibernate (SINGLE_TABLE, JOINED, TABLE_PER_CLASS)?

| Strategy | Description | Pros | Cons |
|-----------|--------------|------|------|
| `SINGLE_TABLE` | All entities in one table | Fast queries | Null columns, data redundancy |
| `JOINED` | Separate tables joined by foreign key | Normalized schema | More joins |
| `TABLE_PER_CLASS` | Each subclass has its own table | Independent entities | No polymorphic queries |

```java
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Vehicle { @Id private Long id; }

@Entity
public class Car extends Vehicle { private int doors; }

@Entity
public class Bike extends Vehicle { private boolean sports; }
```

---

## 7. What is the N+1 select problem in relationships? How can you prevent it?

**Answer:**
Occurs when fetching a collection results in **N additional queries** for N related entities.

Example:
```java
List<Department> depts = session.createQuery("from Department", Department.class).list();
for (Department d : depts) {
    System.out.println(d.getEmployees().size()); // triggers multiple queries
}
```

**Solutions:**
- Use `JOIN FETCH` in HQL
- Enable **batch fetching** (`@BatchSize(size = X)`)
- Use **Entity Graphs**

```java
@Query("SELECT d FROM Department d JOIN FETCH d.employees")
List<Department> findAllWithEmployees();
```

---

**Summary:**
This section covers all Hibernate association mappings, key annotations (`@OneToMany`, `@JoinColumn`, `@EmbeddedId`), inheritance strategies, and optimization tips for avoiding N+1 queries. Mastery here ensures efficient entity relationships and database performance.
