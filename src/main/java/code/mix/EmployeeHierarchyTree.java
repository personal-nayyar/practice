package code.mix;

import java.util.ArrayList;
import java.util.List;

class EmployeeNode {
    String employee;
    List<EmployeeNode> reportees;

    public EmployeeNode(String employee) {
        this.employee = employee;
        this.reportees = new ArrayList<>();
    }

    public void addReportee(EmployeeNode reportee) {
        this.reportees.add(reportee);
    }

    public List<EmployeeNode> getReportees() {
        return reportees;
    }
}

public class EmployeeHierarchyTree {
    private EmployeeNode root;

    // Build the employee hierarchy tree
    public void buildHierarchy(String manager, List<String> reportees) {
        if (root == null) {
            root = new EmployeeNode(manager);
        }

        EmployeeNode managerNode = findEmployeeNode(root, manager);
        if (managerNode == null) {
            throw new IllegalArgumentException("Manager not found in the hierarchy.");
        }

        for (String reportee : reportees) {
            managerNode.addReportee(new EmployeeNode(reportee));
        }
    }

    // Check if the employee is a manager
    public boolean isManager(String employee) {
        return findEmployeeNode(root, employee) != null;
    }

    // List all reportees of a manager
    public List<String> getReportees(String manager) {
        EmployeeNode managerNode = findEmployeeNode(root, manager);
        if (managerNode != null) {
            List<String> reportees = new ArrayList<>();
            for (EmployeeNode reportee : managerNode.getReportees()) {
                reportees.add(reportee.employee);
            }
            return reportees;
        } else {
            return new ArrayList<>();
        }
    }

    private EmployeeNode findEmployeeNode(EmployeeNode node, String employee) {
        if (node == null) {
            return null;
        }

        if (node.employee.equals(employee)) {
            return node;
        }

        for (EmployeeNode reportee : node.reportees) {
            EmployeeNode foundNode = findEmployeeNode(reportee, employee);
            if (foundNode != null) {
                return foundNode;
            }
        }

        return null;
    }

    public static void main(String[] args) {
        EmployeeHierarchyTree employeeTree = new EmployeeHierarchyTree();

        // Building the employee hierarchy tree
        employeeTree.buildHierarchy("A", List.of("B", "C", "D"));
        employeeTree.buildHierarchy("K", List.of("I", "J"));
        employeeTree.buildHierarchy("L", List.of("G", "A", "E"));

        // Checking if an employee is a manager
        String employeeToCheck = "A";
        if (employeeTree.isManager(employeeToCheck)) {
            // Listing reportees of the manager
            List<String> reportees = employeeTree.getReportees(employeeToCheck);
            System.out.println("Employee " + employeeToCheck + " is a manager. Reportees: " + reportees);
        } else {
            System.out.println("Employee " + employeeToCheck + " is not a manager.");
        }
    }
}