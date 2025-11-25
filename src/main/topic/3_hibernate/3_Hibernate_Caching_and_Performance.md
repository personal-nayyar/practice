# ⚙️ Hibernate Caching and Performance Optimization

## 1. Explain the difference between first-level and second-level caching in Hibernate.

- **First-level cache** is associated with a Hibernate `Session`. It’s **mandatory** and enabled by default.
- **Second-level cache** is associated with the `SessionFactory` and can be shared across sessions.
- Example:  
  ```java
  // First-level cache example
  Session session = sessionFactory.openSession();
  Employee e1 = session.get(Employee.class, 1);
  Employee e2 = session.get(Employee.class, 1); // fetched from cache, not DB
  ```

## 2. What is the role of Session in the first-level cache?
- Each Hibernate `Session` maintains its own cache of persistent objects.
- When an entity is fetched, it is stored in the session cache.
- If the same entity is requested again, it’s retrieved from the cache instead of executing a new SQL query.

## 3. What caching providers can be used for the second-level cache (e.g., Ehcache, Infinispan)?
- Hibernate supports several providers:
  - **Ehcache**
  - **Infinispan**
  - **Hazelcast**
  - **Caffeine**
  - **OSCache**
- Configuration example in `hibernate.cfg.xml`:
  ```xml
  <property name="hibernate.cache.region.factory_class">
      org.hibernate.cache.ehcache.EhCacheRegionFactory
  </property>
  <property name="hibernate.cache.use_second_level_cache">true</property>
  ```

## 4. What is the Query Cache and how does it work with the second-level cache?
- The **query cache** stores the result set of executed queries.
- It works **alongside** the second-level cache.
- To use it, both caches must be enabled:
  ```java
  Query query = session.createQuery("from Employee e where e.department = :dept");
  query.setParameter("dept", "HR");
  query.setCacheable(true);
  List<Employee> employees = query.list();
  ```

## 5. What are the trade-offs between caching and database consistency?
- Pros: Reduces database load and improves performance.
- Cons: May lead to **stale data** if cache is not properly synchronized.
- Solution: Use proper **CacheConcurrencyStrategy** and eviction policies.

## 6. How do you configure caching annotations (@Cache, CacheConcurrencyStrategy)?
- Example:
  ```java
  @Entity
  @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
  public class Employee {
      @Id
      private Long id;
      private String name;
  }
  ```
- Common strategies:
  - `READ_ONLY`
  - `NONSTRICT_READ_WRITE`
  - `READ_WRITE`
  - `TRANSACTIONAL`

## 7. What are some performance tuning strategies for Hibernate queries?
- Use **lazy loading** effectively.
- Use **fetch joins** or **batch fetching**.
- Minimize use of **N+1 selects**.
- Use **projections** and **DTOs** for read-only queries.
- Enable SQL logging for profiling.

## 8. How can you batch inserts or updates in Hibernate?
- Batch processing reduces round trips to the database.
- Example configuration:
  ```xml
  <property name="hibernate.jdbc.batch_size">30</property>
  ```
- Example code:
  ```java
  for (int i = 0; i < 100; i++) {
      session.save(new Employee("Emp" + i));
      if (i % 30 == 0) {
          session.flush();
          session.clear();
      }
  }
  ```

## 9. What is fetch size and batch size in Hibernate?
- **Fetch size**: Number of rows fetched from DB per round trip.
- **Batch size**: Number of operations (insert/update/select) executed in one go.
- Example:
  ```xml
  <property name="hibernate.jdbc.fetch_size">50</property>
  <property name="hibernate.jdbc.batch_size">20</property>
  ```

## 10. How to profile Hibernate queries or analyze slow SQL logs?
- Enable SQL logging:
  ```xml
  <property name="hibernate.show_sql">true</property>
  <property name="hibernate.format_sql">true</property>
  <property name="hibernate.generate_statistics">true</property>
  ```
- Use Hibernate Statistics API:
  ```java
  Statistics stats = sessionFactory.getStatistics();
  System.out.println("Entity fetch count: " + stats.getEntityFetchCount());
  ```
