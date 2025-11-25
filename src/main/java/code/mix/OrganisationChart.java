package code.mix;

import java.util.*;

/**
 you're given emp with their name and manager_id, print organisation chart.
 Employee with m_id as null is the top most employee in chart.
 id | name | m_id
 1    john   null
 2    lucifer   1
 3    adam      2
 4    lucy      1
 5    alan      2
 6    amie      4
 7    jackson   5

 john
    - lucifer
        - adam
        - alan
            - jackson
    - lucy
        - amie
 */
class Employee{
    long id;
    String name;
    long m_id;

    public Employee(long id, String name, long m_id) {
        this.id = id;
        this.name = name;
        this.m_id = m_id;
    }
}
public class OrganisationChart {
    static List<Employee> employeeList = Arrays.asList(
            new Employee(1, "john",   0),
            new Employee(2, "lucifer",1),
            new Employee(3, "adam",   2),
            new Employee(4, "john",   1),
            new Employee(5, "john",   2),
            new Employee(6, "john",   4),
            new Employee(7, "john",   5)
    );

    public static Map<Long,List<Long>> generateEmployeeMap(){
        Map<Long, List<Long>> employeeMap = new HashMap<>();
        employeeList.stream().forEach(employee -> {
            List<Long> reporting = employeeMap.getOrDefault(employee.m_id, new ArrayList<>());
            reporting.add(employee.id);
            employeeMap.put(employee.m_id,reporting);
        });
        employeeMap.entrySet().forEach(longListEntry -> System.out.println(longListEntry.getKey() +"--"+longListEntry.getValue()));
        return employeeMap;
    }

    public static void printEmployeeMap(Map<Long, List<Long>> employeeMap, Long m_id, int n){
        if(employeeMap.get(m_id)  == null)
            return;
        for (Long e: employeeMap.get(m_id)) {
            System.out.print(Collections.nCopies(n, "---").toString().replaceAll("[\\[,\\]]", ""));
            System.out.println(e);
            printEmployeeMap(employeeMap, e, n+1);
        }
    }

    public static void main(String[] args) {
        Map<Long,List<Long>> map  = generateEmployeeMap();
        printEmployeeMap(map, 0L, 0);
    }
}
