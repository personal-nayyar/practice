
# Senior SQL Interview Questions & Answers

## 1. Nth Highest Salary
```sql
SELECT salary
FROM (
    SELECT salary,
           DENSE_RANK() OVER (ORDER BY salary DESC) AS rnk
    FROM employee
) t
WHERE rnk = N;   -- replace N
```

## 2. Highest Salary in Each Department
```sql
SELECT d.dept_name, e.first_name, e.last_name, e.salary
FROM employee e
JOIN department d ON e.department_id = d.id
WHERE (e.department_id, e.salary) IN (
    SELECT department_id, MAX(salary)
    FROM employee
    GROUP BY department_id
);
```

## 3. Full Name by Concatenation
```sql
SELECT CONCAT(first_name, ' ', last_name) AS full_name
FROM employee;
```

## 4. Employees Who Are Also Managers
```sql
SELECT DISTINCT m.*
FROM employee m
JOIN employee e ON e.manager_id = m.id;
```

## 5. Position-wise Min/Max Salary
```sql
SELECT position, MIN(salary) AS min_salary, MAX(salary) AS max_salary
FROM employee
GROUP BY position;
```

## 6. Duplicate Employee Records
```sql
SELECT email, COUNT(*) AS duplicate_count
FROM employee
GROUP BY email
HAVING COUNT(*) > 1;
```

## 7. Employees Working in Same Department
```sql
SELECT e1.*, e2.*
FROM employee e1
JOIN employee e2 ON e1.department_id = e2.department_id AND e1.id <> e2.id;
```

## 8. MySQL Current Date & DateTime
```sql
SELECT CURDATE();
SELECT NOW();
SELECT CURRENT_TIMESTAMP();
```
