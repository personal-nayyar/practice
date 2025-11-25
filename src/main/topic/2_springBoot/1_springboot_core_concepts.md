# Advanced Spring Boot Interview Questions — Core Concepts

### 1. What are some key differences between Spring and Spring Boot?
- Spring Boot simplifies Spring configuration by providing auto-configuration, embedded servers, and starter dependencies.
- Traditional Spring requires explicit XML or Java configuration, whereas Boot focuses on convention over configuration.

### 2. How does Spring Boot auto-configuration work internally?
- It uses the `@EnableAutoConfiguration` annotation.
- Auto-config classes are loaded via `spring.factories` under `META-INF`.
- Conditions like `@ConditionalOnClass`, `@ConditionalOnMissingBean` control whether beans are instantiated.

### 3. What’s the purpose of `@SpringBootApplication` annotation?
- Combines `@Configuration`, `@EnableAutoConfiguration`, and `@ComponentScan`.
- It marks the main entry point for Spring Boot applications.

### 4. Explain how Spring Boot handles profiles.
- Profiles isolate environment-specific configurations using `@Profile` and `application-{profile}.yml`.
- Activate via `--spring.profiles.active=prod`.

### 5. How does Spring Boot support externalized configuration?
- Uses the `Environment` abstraction.
- Config values can come from properties, YAML files, environment variables, command-line args, or config servers.

### 6. What are Spring Boot starters and why are they useful?
- Predefined dependency bundles (e.g., `spring-boot-starter-web`, `spring-boot-starter-data-jpa`).
- Simplify dependency management and version compatibility.

### 7. How does Spring Boot manage application startup?
- The `SpringApplication` class bootstraps the app context.
- Uses `ApplicationContextInitializer` and `ApplicationListener` to customize startup.

### 8. How can you customize the banner in a Spring Boot application?
- Replace `banner.txt` in `resources/`.
- Or set `spring.main.banner-mode=off` to disable.

### 9. What’s the role of `CommandLineRunner` and `ApplicationRunner`?
- Execute code after Spring context loads.
- Useful for initializing data or triggering background tasks.

### 10. Explain how Spring Boot detects and configures embedded web servers.
- Detects server type from classpath (e.g., Tomcat, Jetty, Undertow).
- Configures a matching `WebServerFactory` bean automatically.
