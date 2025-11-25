# 🧾 10. Practical / Scenario-Based

### 1. You have a slow API — how would you diagnose the root cause?
- **Check application metrics:** response time, throughput, and latency trends.  
- **Profile the code:** use tools like VisualVM, JProfiler, or YourKit to find bottlenecks.  
- **Inspect database queries:** look for N+1 queries, missing indexes, or long-running queries.  
- **Analyze logs and traces:** leverage distributed tracing tools (Jaeger, Zipkin).  
- **Network latency:** check API gateways, load balancers, or slow downstream services.  

---

### 2. What metrics would you monitor in a Java production service?
- **JVM metrics:** heap/non-heap memory, GC frequency, and thread counts.  
- **Application metrics:** request latency, throughput (RPS), error rate.  
- **Database metrics:** connection pool usage, query execution time.  
- **System metrics:** CPU, disk I/O, and network traffic.  
- **Custom business metrics:** transactions processed, queue depth, etc.  

---

### 3. How do you reduce GC pauses in a high-throughput service?
- Use **G1GC** or **ZGC** for low-latency environments.  
- Tune heap size and set appropriate **-Xms / -Xmx**.  
- Avoid object churn; prefer object reuse (e.g., `StringBuilder`, connection pooling).  
- Monitor GC logs using **GCViewer** or **JDK Mission Control**.  

---

### 4. How would you optimize Hibernate queries in a microservice?
- Use **join fetch** or **batch fetching** to reduce N+1 queries.  
- Enable **second-level and query caching** for read-heavy operations.  
- Use **DTO projections** instead of loading full entities when not needed.  
- Add **database indexes** and monitor slow query logs.  
- Paginate results instead of fetching large datasets.  

---

### 5. How do you debug deadlocks or thread contention?
- Capture **thread dumps (jstack)** and analyze waiting threads.  
- Use **synchronized blocks** sparingly; prefer `ReentrantLock` for better control.  
- Use **thread contention analysis tools** like Java Flight Recorder or VisualVM.  
- Implement **timeout mechanisms** and **proper lock ordering**.  

---

### 6. How do you ensure smooth CI/CD with Java-based services?
- Use **build automation** (Maven/Gradle) and **pipeline tools** (Jenkins, GitHub Actions).  
- Maintain **test coverage** (unit + integration) before deployment.  
- Use **Docker** for consistent environment setup.  
- Integrate **SonarQube**, **Checkstyle**, and **OWASP Dependency Check**.  
- Implement **blue-green** or **canary deployments** for zero downtime.  

---

**💡 Tip:** Always combine proactive monitoring, alerting, and profiling to ensure production-grade reliability and performance.
