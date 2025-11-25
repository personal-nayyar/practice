# 🧠 OOP Interview Cheat Sheet for Senior Software Engineers

## 1. Core OOP Concepts
| Concept | Explanation | Example |
|----------|--------------|----------|
| **Encapsulation** | Wrapping data and methods that operate on that data in a single unit (class). | `private fields + public getters/setters` |
| **Abstraction** | Hiding complex implementation details, exposing only necessary functionality. | `interface PaymentGateway { void pay(); }` |
| **Inheritance** | Reusing behavior by extending base class. | `class Dog extends Animal` |
| **Polymorphism** | One interface, multiple implementations. | `List<String> list = new ArrayList<>();` |

---

## 2. Abstract Class vs Interface
| Feature | Abstract Class | Interface |
|----------|----------------|------------|
| Inheritance | Single | Multiple |
| Purpose | Code reusability + partial abstraction | 100% abstraction (contract) |
| Use When | Shared state or default behavior | Common API across unrelated classes |

**Example:**
```java
abstract class Animal {
    abstract void sound();
    void eat() { System.out.println("eating"); }
}

interface Flyable {
    void fly();
}
```

---

## 3. Why Interfaces When Abstract Classes Exist?
- Abstract class = partial abstraction; only one allowed per class.
- Interface = multiple inheritance of behavior, **decouples modules**.
- Enables **Dependency Inversion** & **Mocking in tests**.

**Example:**
```java
public interface NotificationService { void send(String msg); }
public class EmailNotification implements NotificationService { ... }
public class SMSNotification implements NotificationService { ... }
```

---

## 4. How Interfaces Help Decouple Modules
- Depend on **abstractions**, not concrete implementations.
- Enables **plug-and-play architecture** and **testability**.

**Example:**
```java
public class OrderService {
    private final PaymentGateway gateway;
    public OrderService(PaymentGateway gateway) { this.gateway = gateway; }
}
```

---

## 5. SOLID Principles
| Principle | Definition | Example |
|------------|-------------|----------|
| **S**ingle Responsibility | One reason to change | `Logger` class just logs |
| **O**pen/Closed | Open for extension, closed for modification | Strategy pattern |
| **L**iskov Substitution | Subclass should substitute parent | `Bird` vs `FlyingBird` |
| **I**nterface Segregation | Prefer small, specific interfaces | `Readable`, `Writable` |
| **D**ependency Inversion | Depend on abstraction | `PaymentService` depends on `IPaymentGateway` |

---

## 6. Design Patterns (with Examples)
| Pattern   | Type        | Definition                                                                                   | Example Usage                                         |
|-----------|-------------|----------------------------------------------------------------------------------------------|-------------------------------------------------------|
| Singleton | Creational  | Only single object can be created                                                            | DB connection pool                                    |
| Factory   | Creational  | Provide a factory to create similar objects                                                  | `NotificationFactory` returning Email/SMS             |
| Builder   | Creational  | construct complex objects step by step                                                       | `HttpResponse` returning Response wih optional fields |
| Strategy  | Behavioral  | Define strategy and provide custom impl                                                      | Different payment strategies                          |
| Observer  | Behavioral  | Observer changes of one object's state                                                       | Event-based notifications                             |
| Iterator  | Behavioral  | way to traverse elements of a collection without exposing its internal structure             | Java Collections Framework iterators                  |
| Decorator | Structural  | dynamically add new behavior or responsibilities to an object without modifying it           | Coffee decorator                                      |
| Adapter   | Structural  | allows incompatible interfaces to work together by acting as a bridge between them           | USBToEthernet Adapter                                 |
| Facade    | Structural  | unified interface to a complex subsystem — hiding internal details and reducing dependencies | HomeTheaterFacade                                     |

---

## 7. Real-World Scenario Questions
1. How would you design a payment system that supports multiple gateways (Razorpay, Stripe, PayPal)?  
   → Use **Strategy + Interface** for gateway abstraction.

2. How do interfaces promote loose coupling in microservices?  
   → Define common contracts for inter-service communication.

3. How do you refactor a tightly coupled module using Dependency Injection?  
   → Introduce interfaces and inject dependencies via constructors.

4. What’s the role of OOP principles in system scalability and maintainability?  
   → Enables modular architecture and independent deployability.

---

## 8. Advanced OOP Topics
- **Composition over inheritance**
- **Polymorphism through interfaces**
- **Immutable objects design**
- **Lombok vs explicit OOP patterns**
- **SOLID + Clean Code practices**

---

## 🧩 Quick Practice Exercises
1. Design a **digital wallet** using OOP & SOLID.  
2. Implement a **Pub/Sub system** using the **Observer pattern**.  
3. Model an **ATM system** with interface-based design.  
4. Refactor a legacy monolith module using **Dependency Injection** and **interfaces**.

---

**Prepared by ChatGPT (GPT-5)**  
For senior-level backend and system design interviews.
