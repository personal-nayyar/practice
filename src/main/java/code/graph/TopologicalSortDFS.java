package code.graph;

import java.util.*;

public class TopologicalSortDFS {
    private int vertices;                     // Number of vertices
    private List<List<Integer>> adjList;      // Adjacency list

    public TopologicalSortDFS(int vertices) {
        this.vertices = vertices;
        adjList = new ArrayList<>();
        for (int i = 0; i < vertices; i++) {
            adjList.add(new ArrayList<>());   // Initialize adjacency list
        }
    }

    // Add directed edge src → dest
    public void addEdge(int src, int dest) {
        adjList.get(src).add(dest);
    }

    // DFS utility function for topological sort
    private boolean dfs(int node, boolean[] visited, boolean[] recStack, Stack<Integer> stack) {
        if (visited[node]) {
            return false; // Already processed
        }

        if (recStack[node]) {
            return true; // Cycle detected
        }

        visited[node] = true;
        recStack[node] = true; // Mark node in recursion stack

        // Visit all neighbors
        for (int neighbor : adjList.get(node)) {
            if (dfs(neighbor, visited, recStack, stack)) {
                return true; // Cycle found
            }
        }

        recStack[node] = false; // Backtrack
        stack.push(node);       // Push after visiting all neighbors
        return false;
    }

    // Perform Topological Sort
    public void topologicalSort() {
        boolean[] visited = new boolean[vertices];
        boolean[] recStack = new boolean[vertices]; // For cycle detection
        Stack<Integer> stack = new Stack<>();

        // Handle disconnected graph
        for (int i = 0; i < vertices; i++) {
            if (!visited[i]) {
                if (dfs(i, visited, recStack, stack)) {
                    System.out.println("Cycle detected! Topological sort not possible.");
                    return;
                }
            }
        }

        // Print topological order
        System.out.print("Topological Order: ");
        while (!stack.isEmpty()) {
            System.out.print(stack.pop() + " ");
        }
        System.out.println();
    }

    public static void main(String[] args) {
        TopologicalSortDFS g = new TopologicalSortDFS(6);

        // Example graph
        g.addEdge(5, 2);
        g.addEdge(5, 0);
        g.addEdge(4, 0);
        g.addEdge(4, 1);
        g.addEdge(2, 3);
        g.addEdge(3, 1);

        g.topologicalSort(); // Valid topo sort

        TopologicalSortDFS g2 = new TopologicalSortDFS(4);
        g2.addEdge(0, 1);
        g2.addEdge(1, 2);
        g2.addEdge(2, 3);
        g2.addEdge(3, 1); // Cycle

        g2.topologicalSort(); // Should detect cycle
    }
}