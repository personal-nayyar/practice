# 💾 6. Exception Handling & Best Practices

### 1. Difference between checked and unchecked exceptions.
Checked exceptions are verified at compile-time (e.g., `IOException`, `SQLException`), requiring explicit handling via try-catch or throws clause.  
Unchecked exceptions (e.g., `NullPointerException`, `IllegalArgumentException`) occur at runtime and need not be explicitly handled.

---

### 2. What happens if an exception is thrown in a finally block?
If an exception is thrown in a `finally` block, it can suppress the original exception from the `try` or `catch` block.  
To avoid this, limit logic inside `finally` and prefer **try-with-resources**.

---

### 3. How do you create a custom exception hierarchy?
Custom exceptions can extend `Exception` (checked) or `RuntimeException` (unchecked).  
Example:
```java
public class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }
}
```
Organize custom exceptions logically (e.g., `ValidationException`, `DatabaseException`).

---

### 4. Explain try-with-resources in Java.
Introduced in Java 7, this construct automatically closes resources implementing `AutoCloseable`:
```java
try (BufferedReader br = new BufferedReader(new FileReader("data.txt"))) {
    return br.readLine();
}
```
This ensures deterministic closure of resources even during exceptions.

---

### 5. How would you handle exceptions in streams and lambdas?
Use wrapper methods to handle checked exceptions inside streams:
```java
list.forEach(i -> {
    try {
        process(i);
    } catch (IOException e) {
        log.error("Error while processing", e);
    }
});
```
Alternatively, use helper functions that rethrow checked exceptions as unchecked.

---

### 6. What are common pitfalls in exception handling for large-scale systems?
- Catching generic `Exception` or `Throwable`.  
- Ignoring exceptions without logging or rethrowing.  
- Losing stack trace during rethrow.  
- Using exceptions for control flow.  
- Poorly structured custom exception hierarchy.  
- Not wrapping low-level exceptions into meaningful domain exceptions.

---
