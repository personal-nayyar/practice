# 🧮 Java Collections Framework — Interview Questions & Answers

### 1. Difference between HashMap, LinkedHashMap, and TreeMap
- **HashMap** – Unordered, provides O(1) average lookup/insertion, not thread-safe.  
- **LinkedHashMap** – Maintains insertion order using a doubly-linked list.  
- **TreeMap** – Sorted according to natural order or a custom Comparator, based on Red-Black Tree, O(log n) operations.

```java
Map<String, Integer> map = new TreeMap<>();
map.put("B", 2);
map.put("A", 1);
System.out.println(map); // {A=1, B=2}
```

---

### 2. What is the load factor in HashMap?
- Load factor defines when the HashMap resizes (default **0.75**).  
- When `size >= capacity * loadFactor`, the HashMap capacity doubles.

---

### 3. How does ConcurrentHashMap ensure thread safety?
- Divides the map into **segments** internally (before Java 8) or uses **CAS (Compare-And-Swap)** with synchronized blocks (in Java 8+).  
- Multiple threads can operate safely without global locking.

```java
ConcurrentHashMap<String, Integer> cmap = new ConcurrentHashMap<>();
cmap.put("A", 1);
cmap.putIfAbsent("A", 2); // No overwrite
```

---

### 4. Explain CopyOnWriteArrayList and BlockingQueue
- **CopyOnWriteArrayList** – Creates a new copy of the list on each write. Best for **read-heavy** and **write-rare** scenarios.
- **BlockingQueue** – Thread-safe queue that blocks on insertion/removal when full/empty.

```java
BlockingQueue<String> queue = new ArrayBlockingQueue<>(3);
queue.put("Job1");
System.out.println(queue.take());
```

---

### 5. How does HashSet work internally?
- Backed by a **HashMap**. The element is stored as a key, and a constant dummy value is used.
```java
Set<String> set = new HashSet<>();
set.add("Java");
```

---

### 6. What happens if you override equals() but not hashCode()?
- Objects may become **unreachable** in hash-based collections (HashMap, HashSet).  
- Always override both together to maintain contract consistency.

---

### 7. Difference between ArrayList and Vector
| Feature | ArrayList | Vector |
|----------|------------|---------|
| Thread-Safety | Not synchronized | Synchronized |
| Performance | Faster | Slower |
| Growth | 50% increase | Doubles capacity |

---

### 8. When should you prefer EnumSet or EnumMap?
- **EnumSet** and **EnumMap** are optimized for enums — faster and more memory-efficient.
```java
EnumSet<Day> days = EnumSet.of(Day.MONDAY, Day.TUESDAY);
```

---

### 9. How to make a custom immutable collection?
- Use `Collections.unmodifiableList()` or custom wrapper without setters.

```java
List<String> list = Collections.unmodifiableList(Arrays.asList("A", "B"));
```

---

### 10. How do you handle null keys and values in different maps?
| Map Type | Null Keys | Null Values |
|-----------|------------|--------------|
| HashMap | 1 allowed | Multiple allowed |
| LinkedHashMap | 1 allowed | Multiple allowed |
| TreeMap | Not allowed | Allowed |
| ConcurrentHashMap | Not allowed | Not allowed |

---

**Interview Tip:** Knowing **internal implementations** (hashing, rehashing, load factor, fail-fast iterators) helps in deep Java interviews.
