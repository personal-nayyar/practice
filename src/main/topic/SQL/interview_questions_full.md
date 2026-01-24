
# SQL & System Design Interview Handbook
A complete, structured, and professionally formatted guide containing all senior-level SQL, NoSQL, and system-design concepts discussed.

---

# Table of Contents
1. SQL Basics
2. Joins & Grouping
3. Indexing (Deep Dive)
4. Query Optimization
5. Subqueries & Joins
6. Transactions & Isolation Levels
7. MVCC, Deadlocks, Locking
8. Normalization & Denormalization
9. Partitioning vs Sharding (NoSQL)
10. Advanced SQL Coding Problems

---

# 1. SQL Basics

## WHERE vs HAVING
- WHERE filters rows **before** grouping.
- HAVING filters aggregated groups **after** GROUP BY.

Example:
```sql
SELECT user_id, COUNT(*)
FROM orders
WHERE amount > 100
GROUP BY user_id
HAVING COUNT(*) > 3;
```

## DISTINCT
Removes duplicate rows.
```sql
SELECT DISTINCT user_id FROM orders;
```

## ORDER BY
Sorts output.
```sql
SELECT * FROM orders ORDER BY amount DESC;
```

## NULL Handling
```sql
WHERE age IS NULL
```

---

# 2. Joins & Grouping

## INNER JOIN – only matching rows
```sql
SELECT u.name, o.amount
FROM users u
INNER JOIN orders o ON u.id = o.user_id;
```

## LEFT JOIN – all left rows + matching right
```sql
SELECT u.name, o.amount
FROM users u
LEFT JOIN orders o ON u.id = o.user_id;
```

## FULL OUTER JOIN (Postgres only)
Returns all rows from both tables.

---

# 3. Indexing (Deep Dive)

## What is an Index?
A B‑Tree structure used to speed up lookups.

Example:
```sql
CREATE INDEX idx_user ON orders(user_id);
```

## Composite Index
Order matters.

```sql
CREATE INDEX idx_user_date ON orders(user_id, order_date);
```

Supports:
- user_id
- user_id + order_date

Does NOT support:
- order_date alone

## Covering Index
Query served entirely from index.

```sql
CREATE INDEX idx_user_amount ON orders(user_id, amount);
```

## When Index Fails
- Using functions:
```sql
WHERE LOWER(email) = 'abc@example.com'
```
- Leading wildcard:
```sql
WHERE name LIKE '%abc'
```

---

# 4. Query Optimization

## EXPLAIN ANALYZE
Prints actual execution plan.
```sql
EXPLAIN ANALYZE SELECT * FROM orders WHERE user_id = 10;
```

## Avoid SELECT *
- Breaks covering indexes
- Wastes network bandwidth

## Pagination: OFFSET vs KEYSET
OFFSET pagination:
```sql
SELECT * FROM orders LIMIT 10 OFFSET 100000;
```
Very slow.

Keyset (recommended):
```sql
SELECT * FROM orders
WHERE order_id > last_seen
ORDER BY order_id
LIMIT 10;
```

## N+1 Query Problem
Issue:
```sql
SELECT * FROM orders;
SELECT * FROM order_items WHERE order_id = ?;  -- repeated
```

Fix:
```sql
SELECT o.*, i.*
FROM orders o
JOIN order_items i ON o.id = i.order_id;
```

---

# 5. Subqueries & Joins

## Correlated Subquery
Runs once per row.
```sql
SELECT *
FROM products p
WHERE price > (
   SELECT AVG(price) FROM products p2 WHERE p2.category = p.category
);
```

## EXISTS vs IN
EXISTS is faster for large datasets.

```sql
SELECT *
FROM orders o
WHERE EXISTS (
   SELECT 1 FROM users u WHERE u.id = o.user_id
);
```

---

# 6. Transactions & Isolation Levels

## ACID
- Atomicity
- Consistency
- Isolation
- Durability

## Isolation Levels with Problems

### READ COMMITTED
✔ Prevents dirty reads  
❌ Allows non-repeatable reads

### REPEATABLE READ
✔ Prevents non-repeatable reads  
❌ Allows phantom reads (except Postgres)

### SERIALIZABLE
Strongest level; avoids all anomalies.

---

# 7. MVCC, Locking & Deadlocks

## MVCC
Readers see snapshot, writers create new versions.

## Deadlock Example
Tx1:
```sql
UPDATE A SET x=1 WHERE id=1;
```
Tx2:
```sql
UPDATE A SET x=1 WHERE id=2;
```
Then both try to update each other's row → deadlock.

## Avoid Deadlocks
- Lock rows in consistent order
- Keep transactions short

---

# 8. Normalization & Denormalization

## Normal Forms
- 1NF: atomic fields
- 2NF: no partial dependency
- 3NF: no transitive dependency

## When to Denormalize?
- Read-heavy systems
- Reduce JOINs
- Precompute aggregates

Example: Store review_count in products table.

---

# 9. Partitioning vs Sharding in NoSQL

## Partitioning
Database internally manages data distribution.

Example: Cassandra
```sql
PRIMARY KEY ((country), user_id)
```

## Sharding
Application controls routing.

Example:
```
Shard1 → user_id 1–300M  
Shard2 → user_id 300M–600M  
Shard3 → user_id 600M–1B
```

Routing:
```java
shard = userId % 3;
```

---

# 10. Advanced SQL Coding Problems

## Second Highest Salary
```sql
SELECT salary
FROM (
  SELECT salary, DENSE_RANK() OVER (ORDER BY salary DESC) r
  FROM employees
) t
WHERE r = 2;
```

## Remove Duplicates (keep latest)
```sql
DELETE FROM users
WHERE id IN (
  SELECT id FROM (
    SELECT id,
           ROW_NUMBER() OVER (PARTITION BY email ORDER BY created_at DESC) rn
    FROM users
  ) x
  WHERE rn > 1
);
```

## Running Total
```sql
SELECT order_id,
       amount,
       SUM(amount) OVER (ORDER BY order_id) AS running_total
FROM orders;
```

## Previous Row Difference
```sql
SELECT order_id,
       amount - LAG(amount) OVER (ORDER BY order_id) AS diff
FROM orders;
```

## Top N per Group
```sql
SELECT *
FROM (
  SELECT *,
         ROW_NUMBER() OVER (PARTITION BY dept ORDER BY salary DESC) rn
  FROM employees
) t
WHERE rn <= 3;
```

---
