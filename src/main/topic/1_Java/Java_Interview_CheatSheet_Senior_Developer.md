# ☕ Java Interview Cheat Sheet for Senior Software Developers

Comprehensive set of **senior-level Java interview questions** covering core concepts, advanced topics, and system design-oriented discussions.

---

## 🧩 Core Java Concepts

### 1. Difference between `==` and `.equals()`?
- `==` compares object references.
- `.equals()` compares object values (can be overridden).

### 2. What are `hashCode()` and `equals()` contract?
- If two objects are equal using `.equals()`, they must have the same hashcode.
- Used by hash-based collections (`HashMap`, `HashSet`).

### 3. Explain immutability and give examples.
- Once created, an object’s state cannot be changed.  
- Example: `String`, `Integer`, `LocalDate`.

### 4. How does Java achieve platform independence?
- Java compiles code into **bytecode**, which runs on JVM, abstracting OS details.

### 5. What is the difference between `final`, `finally`, and `finalize()`?
| Keyword | Purpose |
|----------|----------|
| `final` | Constant, prevents overriding/inheritance |
| `finally` | Block always executed after try-catch |
| `finalize()` | Called by GC before object is destroyed (deprecated) |

---

## ⚙️ Collections & Concurrency

### 6. Difference between `ArrayList` and `LinkedList`?
- `ArrayList` → Fast random access, slower inserts/removals.
- `LinkedList` → Slower access, faster inserts/removals.

### 7. How does `HashMap` work internally?
- Stores key-value pairs in **buckets** using hashcode.
- Handles collisions via **chaining (linked list / tree)**.
- Uses `hash(key) % capacity` to locate buckets.

### 8. Concurrent Collections Examples
- `ConcurrentHashMap`, `CopyOnWriteArrayList`, `BlockingQueue`.

### 9. Explain thread safety in `ConcurrentHashMap`.
- Uses **segment locking (Java 7)** / **CAS and node-level locking (Java 8)** for better concurrency.

### 10. Difference between `synchronized` and `Lock`?
| Feature | synchronized | Lock |
|----------|--------------|------|
| Scope | Block / Method | Object level |
| Reentrant | Yes | Yes |
| Try-lock | No | Yes |
| Condition | No | Yes |

---

## 🚀 Multithreading & Concurrency

### 11. Explain `volatile` keyword.
- Ensures **visibility** of variable changes across threads.
- Doesn’t guarantee atomicity.

### 12. Difference between `wait()`, `sleep()`, and `yield()`?
| Method | Belongs to | Releases Lock? | Purpose |
|---------|-------------|----------------|----------|
| `wait()` | Object | ✅ | Pause thread until notified |
| `sleep()` | Thread | ❌ | Pause for fixed time |
| `yield()` | Thread | ❌ | Suggest to scheduler to give up CPU |

### 13. What are Executors in Java?
- High-level concurrency framework replacing manual thread creation.
- Types: `FixedThreadPool`, `CachedThreadPool`, `ScheduledThreadPool`.

### 14. What is the ForkJoinPool?
- Used for **divide-and-conquer** parallelism (e.g., parallel streams).

### 15. Explain Deadlock, Livelock, Starvation.
- **Deadlock:** Two threads waiting on each other’s locks.
- **Livelock:** Threads keep changing states without progress.
- **Starvation:** Thread never gets CPU time.

---

## ☁️ Java 8+ Features

### 16. Explain Streams API.
- Functional-style operations on collections: `map`, `filter`, `reduce`.
- Lazy evaluation and parallel processing supported.

### 17. Difference between `map()` and `flatMap()`?
- `map()` transforms each element.
- `flatMap()` flattens nested structures (e.g., List<List<T>>).

### 18. Functional Interfaces & Examples.
- Interface with one abstract method.
- Examples: `Runnable`, `Callable`, `Predicate`, `Function`, `Supplier`.

### 19. Optional class usage?
- Avoids null checks: `Optional.ofNullable(value).orElse(defaultValue)`.

### 20. Explain method references and lambda expressions.
- Short syntax for anonymous methods.  
  Example: `list.forEach(System.out::println);`

---

## 🧠 Memory Management & JVM Internals

### 21. JVM Memory Areas
- Heap, Stack, Method Area, PC Register, Native Stack.

### 22. Difference between Stack and Heap memory?
- Stack → local variables, method calls.  
- Heap → objects, shared among threads.

### 23. Explain Garbage Collection (GC) in Java.
- Automatic memory cleanup.
- Uses algorithms like **Mark-Sweep-Compact** and **G1 GC**.

### 24. What are Soft, Weak, and Phantom References?
| Type | Collected when? |
|------|----------------|
| Soft | Memory low |
| Weak | On next GC cycle |
| Phantom | After finalize, before deallocation |

### 25. How to diagnose memory leaks?
- Use tools: `jconsole`, `jvisualvm`, `jmap`, `jprofiler`, or `Flight Recorder`.

---

## 🏗️ Design Patterns in Java

### 26. Commonly used patterns
- **Creational:** Singleton, Factory, Builder, Prototype.  
- **Structural:** Adapter, Decorator, Facade, Composite.  
- **Behavioral:** Strategy, Observer, Command, Iterator.

### 27. How to implement a thread-safe Singleton?
```java
class Singleton {
    private static volatile Singleton instance;
    private Singleton() {}
    public static Singleton getInstance() {
        if (instance == null) {
            synchronized (Singleton.class) {
                if (instance == null) instance = new Singleton();
            }
        }
        return instance;
    }
}
```

### 28. Difference between Factory and Abstract Factory?
- **Factory:** Creates one family of objects.
- **Abstract Factory:** Creates families of related objects without specifying concrete classes.

### 29. Example of Decorator in Java?
- Java I/O: `BufferedReader` wraps `FileReader` for enhanced functionality.

### 30. Example of Adapter in Java?
- JDBC drivers adapt vendor-specific database APIs to standard JDBC interfaces.

---

## 🧩 Advanced Topics

### 31. What is Reflection API?
- Allows inspection and modification of classes, methods, and fields at runtime.

### 32. What is a ClassLoader?
- Loads classes into JVM dynamically.  
  - Types: Bootstrap, Extension, Application, Custom.
  - ClassLoader Type                        Loads Classes                                                   From Notes
    Bootstrap ClassLoader                   JAVA_HOME/jre/lib (core Java classes like java.lang.*)          It’s implemented in native code, not a Java class.
    Extension (Platform) ClassLoader        JAVA_HOME/jre/lib/ext or directories specified by java.ext.dirs Loads Java extension libraries, like javax.* APIs.
    Application (System) ClassLoader        Classpath specified by CLASSPATH or -cp argument                Loads user-defined classes and libraries.
    
  - Bootstrap ClassLoader
    ↑
    Extension (Platform) ClassLoader
    ↑
    Application (System) ClassLoader
    •	Delegation model: If a class isn’t found by the parent, the child ClassLoader attempts to load it.



### 33. Difference between Checked and Unchecked exceptions?
- **Checked:** Must be handled (IOException).  
- **Unchecked:** Runtime exceptions (NullPointerException).

### 34. How does Java handle serialization?
- Using `Serializable` interface.
- Can customize with `transient` fields or `readObject()` / `writeObject()`.

### 35. What are records and sealed classes (Java 17+)?
- **Records:** Immutable data carriers.
- **Sealed classes:** Restrict which classes can extend them.

- **Records vs enum:**
  - ### 🧾 Record vs Enum (Conceptual Difference)

- **Record**: Describes *what the data is*  
  **Example:**
  ```java
  Person(name, age)
  Point(x, y)
  Address(city, zip)

---

## ☁️ Microservices & Spring (Bonus for Senior Roles)

### 36. How does Dependency Injection work in Spring?
- Uses reflection and configuration to inject dependencies via constructors or setters.

### 37. Explain difference between Bean scopes.
| Scope | Description |
|--------|-------------|
| Singleton | One instance per container |
| Prototype | New instance per request |
| Request | New instance per HTTP request |
| Session | One instance per session |

### 38. What is Spring Boot Auto-Configuration?
- Automatically configures beans based on classpath dependencies and environment.

### 39. How to secure REST APIs?
- Use JWT, OAuth2, Spring Security filters, and method-level authorization.

### 40. Explain Circuit Breaker Pattern.
- Prevents system overload by stopping repeated failed calls (e.g., Resilience4J, Hystrix).

---

**Prepared by ChatGPT (GPT‑5)**  
For **Senior Software Engineer / Backend Developer** interviews.  
Stay confident — focus on clarity, design thinking, and practical trade-offs.
