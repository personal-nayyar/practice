# 🧩 Core Java & OOPs Interview Questions

## 1. Explain OOP principles and how Java implements them (with examples).
- **Encapsulation:** Bundling data (fields) and methods that operate on that data within a single unit (class). Example: Using private variables with public getters/setters.
- **Inheritance:** Allows one class to inherit features (fields and methods) from another. Example: `class Employee extends Person {}`
- **Polymorphism:** The ability of a method or object to take many forms, achieved via overriding and overloading.
- **Abstraction:** Hiding implementation details and exposing only functionality. Achieved via abstract classes or interfaces.

## 2. What is the difference between abstract classes and interfaces in Java 8+?
- Abstract classes can have constructors, instance variables, and non-static methods.
- Interfaces (from Java 8 onwards) can have `default` and `static` methods but cannot hold state.
- Multiple interfaces can be implemented, but a class can extend only one abstract class.

## 3. How does method overloading differ from method overriding?
- **Overloading:** Compile-time polymorphism; same method name, different parameter lists.
- **Overriding:** Runtime polymorphism; subclass redefines a superclass method with same signature.

## 4. What are inner classes and when would you use them?
- Inner classes are classes defined within another class.
- Used to logically group classes, improve encapsulation, and access private members of the outer class.
- Types: Static nested class, non-static inner class, local class, and anonymous inner class.

## 5. How does Java achieve platform independence?
- Java compiles code into **bytecode**, which runs on the **Java Virtual Machine (JVM)**.
- JVM abstracts the underlying OS, allowing “write once, run anywhere.”

## 6. What are Immutable objects and why are they useful in multithreading?
- Immutable objects cannot be changed after creation (e.g., `String`, `Integer`).
- They are inherently thread-safe since shared data cannot be modified concurrently.

## 7. Explain fail-fast vs fail-safe iterators.
- **Fail-fast:** Throw `ConcurrentModificationException` if the collection is modified while iterating (e.g., `ArrayList`, `HashMap`).
- **Fail-safe:** Work on a clone of the collection; no exception (e.g., `CopyOnWriteArrayList`, `ConcurrentHashMap`).

## 8. Difference between Comparable and Comparator interfaces.
- **Comparable:** Natural ordering (`compareTo()` method within the class).
- **Comparator:** Custom ordering (`compare()` method external to class).

## 9. What is the difference between shallow and deep cloning in Java?
- **Shallow clone:** Copies object’s references, not nested objects (`Object.clone()` default behavior).
- **Deep clone:** Copies all nested objects recursively (requires custom logic or serialization).

## 10. Explain covariant return types in overriding.
- Allows overriding method to return a subtype of the parent method’s return type.
- Example:
  ```java
  class Animal { Animal get() { return this; } }
  class Dog extends Animal { Dog get() { return this; } }
  ```
