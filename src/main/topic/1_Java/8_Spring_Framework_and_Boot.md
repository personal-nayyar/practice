                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                       # 🌿 Spring Framework & Spring Boot Interview Questions

### 1. Difference between BeanFactory and ApplicationContext
- **BeanFactory** is the root interface of the Spring container providing basic dependency injection features.
- **ApplicationContext** extends BeanFactory, adding enterprise-level functionalities like AOP integration, event propagation, and resource loading.
- ApplicationContext eagerly initializes beans by default, unlike BeanFactory which initializes lazily.

---

### 2. Explain Dependency Injection (DI) and Inversion of Control (IoC)
- **Dependency Injection (DI)**: Objects are given their dependencies by the Spring container rather than creating them manually.
- **Inversion of Control (IoC)**: The control of object creation and lifecycle is shifted from application code to the container.
- Types of DI: Constructor Injection, Setter Injection, and Field Injection.

---

### 3. What are singleton and prototype bean scopes?
- **Singleton**: A single shared instance of a bean is created and cached within the container.
- **Prototype**: A new bean instance is created every time it’s requested.
```java
@Scope("singleton")
class SingletonBean {}

@Scope("prototype")
class PrototypeBean {}
```

---

### 4. Explain AOP (Aspect-Oriented Programming) with examples
- AOP modularizes cross-cutting concerns like logging, security, and transactions.
- Main concepts: Aspect, Advice, JoinPoint, Pointcut, and Weaving.
```java
@Aspect
@Component
public class LoggingAspect {
    @Before("execution(* com.example.service.*.*(..))")
    public void logBefore(JoinPoint joinPoint) {
        System.out.println("Executing: " + joinPoint.getSignature());
    }
}
```

---

### 5. How does Spring Boot autoconfiguration work?
- Uses **@EnableAutoConfiguration** and classpath scanning to automatically configure beans.
- Controlled through `META-INF/spring.factories` with conditional loading via `@ConditionalOnClass`, `@ConditionalOnMissingBean`, etc.

---

### 6. How does Spring Data JPA simplify repository design?
- Simplifies CRUD operations using **JpaRepository** and **CrudRepository** interfaces.
- Query methods like `findByEmail(String email)` are auto-generated.
- Custom queries can be defined using `@Query` annotations.

---

### 7. How to handle transactions across multiple databases?
- Use **JTA (Java Transaction API)** or **ChainedTransactionManager** for distributed transactions.
- Ensures ACID compliance across multiple datasources.
- In microservices, eventual consistency with Saga/Compensating Transactions can be used.

---

### 8. Explain caching strategies in Spring
- Enabled via `@EnableCaching`, `@Cacheable`, `@CacheEvict`, and `@CachePut` annotations.
- Supported providers: Ehcache, Redis, Caffeine, etc.
```java
@Cacheable("employees")
public Employee getEmployeeById(Long id) {
    return employeeRepository.findById(id).orElseThrow();
}
```

---

### 9. How does Spring Security handle authentication and authorization?
- Uses filters and the **SecurityContext** for user authentication and access control.
- **Authentication** verifies user identity; **Authorization** checks user permissions.
```java
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
            .antMatchers("/admin/**").hasRole("ADMIN")
            .anyRequest().authenticated()
            .and().formLogin();
    }
}
```
