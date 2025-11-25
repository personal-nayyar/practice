# 🧠 2. Java Memory Management & Exceptions

## 1. Explain Java memory model and its main areas.
Java memory is divided into several key areas:
- **Heap:** Stores objects and instance variables. Managed by the Garbage Collector.
- **Stack:** Stores method calls and local variables.
- **Metaspace (Java 8+):** Stores class metadata.
- **PC Register & Native Method Stack:** For JVM instruction tracking and native code execution.

---

## 2. What is the difference between Stack and Heap memory?
| Aspect | Stack | Heap |
|--------|--------|------|
| Storage | Method frames, local variables | Objects, instance variables |
| Access Speed | Faster | Slower |
| Lifetime | Exists per thread | Exists till GC removes objects |
| Managed by | Thread | Garbage Collector |

---

## 3. How does Garbage Collection work in Java?
Garbage Collection automatically frees memory occupied by unreachable objects.
Common algorithms:
- **Mark and Sweep**
- **Copying Collector**
- **Generational GC (Young & Old generation)**
GC tuning is done via JVM parameters (`-Xms`, `-Xmx`, `-XX:+UseG1GC` etc.).

---

## 4. What is memory leak in Java and how can you prevent it?
A **memory leak** occurs when objects are no longer used but still referenced.
**Prevention:**
- Avoid static references to large objects.
- Close connections and streams.
- Use tools like VisualVM or JProfiler.

---

## 5. What are strong, weak, soft, and phantom references?
- **Strong:** Normal reference, prevents GC.
- **Soft:** Collected only when memory is low.
- **Weak:** Collected as soon as not referenced.
- **Phantom:** Used for cleanup actions before GC.

---

## 6. Explain final, finally, and finalize().
- **final:** Keyword for constants, methods, or classes (prevents modification).
- **finally:** Block executed regardless of exceptions.
- **finalize():** Method invoked before GC (deprecated in Java 9+).

---

## 7. What is Exception Handling mechanism in Java?
Java uses **try-catch-finally** blocks and the **Throwable hierarchy**:
- **Checked exceptions:** Must be handled or declared.
- **Unchecked exceptions:** Runtime exceptions.

---

## 8. Difference between Checked and Unchecked exceptions.
| Type | Example | Compile-time check |
|-------|----------|-------------------|
| Checked | IOException, SQLException | Yes |
| Unchecked | NullPointerException, ArithmeticException | No |

---

## 9. What is the difference between throw and throws?
- **throw:** Used to actually throw an exception.  
  Example: `throw new IOException("Error!");`
- **throws:** Declares possible exceptions in method signature.  
  Example: `void read() throws IOException`

---

## 10. What are best practices for exception handling?
- Catch specific exceptions.
- Never swallow exceptions silently.
- Use custom exceptions for clarity.
- Log exceptions meaningfully.
- Avoid using exceptions for control flow.
