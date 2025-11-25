# 🚀 5. Java 8+ Features

1. **Explain Streams API and how it improves functional programming.**  
   - Streams API provides a declarative approach to process collections using functional-style operations like `map`, `filter`, and `reduce`.  
   - It enables parallelism, improves readability, and avoids mutability issues.

2. **What are lambda expressions and their benefits?**  
   - Lambdas are anonymous functions introduced in Java 8.  
   - They reduce boilerplate code, make code more expressive, and support functional interfaces.

3. **Explain Optional and best practices for avoiding NullPointerException.**  
   - `Optional` is a container for potentially null values.  
   - Use methods like `orElse()`, `ifPresent()`, and `map()` instead of null checks. Avoid using `Optional` in class fields.

4. **What is the difference between map() and flatMap()?**  
   - `map()` transforms each element into another type.  
   - `flatMap()` flattens nested structures (like `Stream<Stream<T>>` → `Stream<T>`).

5. **How do default and static methods in interfaces work?**  
   - Default methods allow adding functionality to interfaces without breaking existing implementations.  
   - Static methods belong to the interface and can be called directly using the interface name.

6. **Explain Collectors.groupingBy() with examples.**  
   ```java
   Map<String, List<Employee>> groupByDept =
       employees.stream().collect(Collectors.groupingBy(Employee::getDepartment));
   ```  
   - It groups data based on a classifier function.

7. **What are functional interfaces and examples from java.util.function package?**  
   - Interfaces with a single abstract method, e.g., `Function<T,R>`, `Predicate<T>`, `Consumer<T>`, and `Supplier<T>`.

8. **How does LocalDateTime API differ from java.util.Date?**  
   - `LocalDateTime` is immutable and thread-safe, unlike `Date`.  
   - It supports better API design, zone handling, and clear separation between date and time.

9. **What improvements were introduced in Java 11 and Java 17 LTS?**  
   - Java 11: `var` in lambda, HTTP Client API, String enhancements.  
   - Java 17: Sealed classes, records, pattern matching for `instanceof`, and performance improvements.

10. **How does var, records, and sealed classes improve code design?**  
    - `var`: Reduces verbosity.  
    - `records`: Compact syntax for immutable data classes.  
    - `sealed`: Restricts subclassing for better control and maintainability.
