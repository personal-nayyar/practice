# 🧠 Java Multithreading & Concurrency Interview Questions and Answers

## 1. Explain the difference between process and thread.
- **Process**: Independent execution unit with its own memory space.
- **Thread**: Lightweight subunit of a process sharing the same memory.
- Threads are faster to start, switch, and communicate compared to processes.

---

## 2. How does the Java Memory Model (JMM) ensure visibility and ordering?
- JMM defines **how threads interact through shared memory**.
- It ensures **visibility**, **atomicity**, and **ordering** through constructs like `volatile`, `synchronized`, and `final`.
- **Happens-before** relationships define when updates become visible to other threads.

Example:
```java
class Example {
    private volatile boolean flag = false;
    public void writer() { flag = true; }
    public void reader() {
        if (flag) System.out.println("Visible across threads!");
    }
}
```

---

## 3. Difference between synchronized, ReentrantLock, and ReadWriteLock.
| Feature | synchronized | ReentrantLock | ReadWriteLock |
|----------|---------------|---------------|----------------|
| Reentrant | Yes | Yes | Yes |
| Try-lock | No | Yes | Yes |
| Fairness Policy | No | Optional | Optional |
| Read/Write Separation | No | No | Yes |
| Performance | Simpler | Flexible | Ideal for read-heavy systems |

---

## 4. What are volatile variables used for?
- Ensures **visibility** of variable updates across threads.
- Prevents caching of variables in thread-local memory.
- Does **not guarantee atomicity**.

Example:
```java
private volatile boolean running = true;
```

---

## 5. How does ThreadLocal work internally?
- Provides **thread-confined storage**.
- Each thread has its own independent copy of the variable.
- Backed by a `ThreadLocalMap` inside each `Thread` object.

Example:
```java
ThreadLocal<Integer> counter = ThreadLocal.withInitial(() -> 0);
counter.set(counter.get() + 1);
```

---

## 6. Explain ExecutorService, Callable, and Future.
- **ExecutorService**: Manages and reuses threads efficiently.
- **Callable**: Returns a value and can throw exceptions.
- **Future**: Represents the result of an asynchronous computation.

Example:
```java
ExecutorService executor = Executors.newFixedThreadPool(3);
Future<Integer> result = executor.submit(() -> 10 * 2);
System.out.println(result.get());
executor.shutdown();
```

---

## 7. What is a deadlock, and how do you detect and prevent it?
- **Deadlock**: When two or more threads wait indefinitely for resources locked by each other.
- Prevention techniques:
  - Acquire locks in the same order.
  - Use `tryLock()` with timeout.
  - Use high-level concurrency utilities (like `ConcurrentHashMap`).
- Detection tools: Thread dumps (`jstack`), JConsole, VisualVM.

---

## 8. What is ForkJoinPool and CompletableFuture?
- **ForkJoinPool**: Designed for divide-and-conquer parallel tasks.
- **CompletableFuture**: Supports asynchronous programming with non-blocking callbacks.

Example:
```java
CompletableFuture.supplyAsync(() -> "Hello")
                 .thenApply(msg -> msg + " World")
                 .thenAccept(System.out::println);
```

---

## 9. Difference between parallel streams and traditional threading.
| Aspect | Parallel Stream | Traditional Threads |
|---------|-----------------|---------------------|
| Abstraction | High-level | Manual control |
| Thread Management | Automatic (ForkJoinPool) | Manual (Thread, Executor) |
| Tuning | Limited | Full control |
| Exception Handling | Harder | Explicit |

---

## 10. How to design a thread-safe singleton or shared resource manager?
### Example: Thread-safe Singleton (Double-Checked Locking)
```java
class Singleton {
    private static volatile Singleton instance;
    private Singleton() {}
    public static Singleton getInstance() {
        if (instance == null) {
            synchronized (Singleton.class) {
                if (instance == null)
                    instance = new Singleton();
            }
        }
        return instance;
    }
}
```

✅ Uses `volatile` for visibility.
✅ Synchronizes only during initialization.

---

### ✅ Key Takeaways
- Prefer higher-level concurrency APIs (`ExecutorService`, `CompletableFuture`).
- Avoid shared mutable state.
- Use concurrent collections like `ConcurrentHashMap`.
- Profile multithreaded performance using JMH or VisualVM.
