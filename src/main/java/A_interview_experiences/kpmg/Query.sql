Task: Write an SQL query that involves joining three tables: employee, department, and salary.
Requirements:

Tables:

employee: employee_id, department_id, name
department: department_id, department_name
salary: employee_id, amount

Query: Write an SQL query to find the total salary of each department.

SELECT department.department_name, SUM(salary.amount) as total_salary
FROM employee
JOIN department ON employee.department_id = department.department_id
JOIN salary ON employee.employee_id = salary.employee_id
GROUP BY department.department_name;

Query: Write an SQL query to find the average salary of each department.

SELECT department.department_name, AVG(salary.amount) as average_salary
FROM employee
JOIN department ON employee.department_id = department.department_id
JOIN salary ON employee.employee_id = salary.employee_id
GROUP BY department.department_name;


Query: Write an SQL query to find the highest salary in each department.

SELECT department.department_name, MAX(salary.amount) as highest_salary
FROM employee
JOIN department ON employee.department_id = department.department_id
JOIN salary ON employee.employee_id = salary.employee_id
GROUP BY department.department_name;


Query: Write an SQL query to find the lowest salary in each department.

SELECT department.department_name, MIN(salary.amount) as lowest_salary
FROM employee
JOIN department ON employee.department_id = department.department_id
JOIN salary ON employee.employee_id = salary.employee_id
GROUP BY department.department_name;

Query: Write an SQL query to find top 10 employees with highest salary.

SELECT employee.name, salary.amount
FROM employee
JOIN salary ON employee.employee_id = salary.employee_id
ORDER BY salary.amount DESC
LIMIT 10;

Query: Write an SQL query to find the number of employees in each department.

SELECT department.department_name, COUNT(employee.employee_id) as employee_count
FROM employee
JOIN department ON employee.department_id = department.department_id
GROUP BY department.department_name;

Query: Write an SQL query to find the employees with highest salary in each department.

SELECT department.department_name, employee.name, salary.amount
FROM employee
JOIN department ON employee.department_id = department.department_id
JOIN salary ON employee.employee_id = salary.employee_id
WHERE salary.amount = (
    SELECT MAX(salary.amount)
    FROM employee
    JOIN department ON employee.department_id = department.department_id
    JOIN salary ON employee.employee_id = salary.employee_id
    WHERE department.department_name = employee.department_name
)
GROUP BY department.department_name;


