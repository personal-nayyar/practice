# 🧠 Top 30 OOPs Interview Q&A for Senior Software Developers

Each answer is concise (2–3 lines) and includes examples when useful.

---

### 1. What are the main OOP principles?
**Answer:** Encapsulation, Abstraction, Inheritance, and Polymorphism. They improve modularity, reusability, and maintainability.

---

### 2. Explain Encapsulation with an example.
**Answer:** It hides internal data and provides access through public methods.
```java
class Account {
    private double balance;
    public void deposit(double amt) { balance += amt; }
}
```

---

### 3. What is Abstraction?
**Answer:** Showing only essential features while hiding implementation details.
```java
interface Payment { void pay(double amount); }
```

---

### 4. What is Inheritance?
**Answer:** Mechanism to derive a class from another, reusing code.
```java
class Dog extends Animal { void bark() {} }
```

---

### 5. What is Polymorphism?
**Answer:** Ability to perform one action in different ways.
```java
Animal a = new Dog(); a.sound();
```

---

### 6. What is the difference between compile-time and runtime polymorphism?
**Answer:** Compile-time → method overloading; Runtime → method overriding.

---

### 7. What is an Abstract Class?
**Answer:** Class with abstract methods and optional concrete methods.
```java
abstract class Shape { abstract void draw(); }
```

---

### 8. What is an Interface?
**Answer:** A contract defining methods without implementation. Promotes loose coupling.
```java
interface Flyable { void fly(); }
```

---

### 9. Abstract Class vs Interface?
**Answer:** Abstract class supports partial abstraction; Interface supports full abstraction and multiple inheritance.

---

### 10. Why were interfaces introduced if abstract classes exist?
**Answer:** To support **multiple inheritance of behavior** and **dependency inversion** between modules.

---

### 11. What is the “IS-A” relationship?
**Answer:** It represents inheritance — `Dog IS-A Animal`.

---

### 12. What is the “HAS-A” relationship?
**Answer:** It represents composition — `Car HAS-A Engine`.

---

### 13. What is Composition over Inheritance?
**Answer:** Favoring object composition (HAS-A) to reduce tight coupling from inheritance.

---

### 14. What is Dependency Injection (DI)?
**Answer:** Providing dependencies from outside rather than creating inside.
```java
class OrderService {
  private final PaymentGateway gateway;
  OrderService(PaymentGateway gateway) { this.gateway = gateway; }
}
```

---

### 15. How do interfaces decouple modules?
**Answer:** By allowing classes to depend on abstractions, enabling flexible implementation swapping.

---

### 16. What are SOLID principles?
**Answer:** 5 principles for clean OOP design: Single Responsibility, Open/Closed, Liskov Substitution, Interface Segregation, and Dependency Inversion.

---

### 17. Explain the Open/Closed Principle with example.
**Answer:** Classes should be open for extension but closed for modification.
```java
interface Discount { double apply(double price); }
class NewYearDiscount implements Discount { ... }
```

---

### 18. What is the Liskov Substitution Principle?
**Answer:** Subclasses should be replaceable with parent class objects without altering correctness.

---

### 19. What is the Strategy Pattern?
**Answer:** Behavioral pattern for runtime selection of algorithm.
```java
interface Payment { void pay(); }
class CardPayment implements Payment { ... }
```

---

### 20. What is the Factory Pattern?
**Answer:** Creates objects without exposing creation logic.
```java
class PaymentFactory {
  static Payment get(String type) { return type.equals("CARD") ? new CardPayment() : new UpiPayment(); }
}
```

---

### 21. What is the Singleton Pattern?
**Answer:** Ensures only one instance of a class exists.
```java
class Singleton {
  private static Singleton instance = new Singleton();
  private Singleton() {}
  public static Singleton getInstance() { return instance; }
}
```

---

### 22. What is the Observer Pattern?
**Answer:** Defines one-to-many dependency between objects.
```java
subject.add(observer);
```

---

### 23. What is the Decorator Pattern?
**Answer:** Dynamically adds new responsibilities to an object without modifying existing code.

---

### 24. What is Method Overloading?
**Answer:** Same method name with different parameters (compile-time polymorphism).

---

### 25. What is Method Overriding?
**Answer:** Subclass provides specific implementation of a parent class method.

---

### 26. What is an Immutable Class?
**Answer:** State cannot change once created. Example: `String` in Java.

---

### 27. What is the difference between Composition and Aggregation?
**Answer:** Composition → strong ownership (car-engine), Aggregation → weak (student-department).

---

### 28. How is OOP used in system design?
**Answer:** By modeling real-world entities as classes and interactions as relationships to ensure modularity and scalability.

---

### 29. What is Coupling and Cohesion?
**Answer:** Coupling = interdependency between modules (low preferred). Cohesion = relatedness of module’s functionality (high preferred).

---

### 30. Explain Interface Segregation Principle.
**Answer:** Prefer many small, specific interfaces instead of one large general-purpose one.

---

**Prepared by ChatGPT (GPT-5)**  
For Senior Software Developer Interview Preparation.
