package code.graph;

import java.util.*;

public class DirectedCycleDetection {
    private int vertices;
    private List<List<Integer>> adjList;

    public DirectedCycleDetection(int vertices) {
        this.vertices = vertices;
        adjList = new ArrayList<>();
        for (int i = 0; i < vertices; i++) {
            adjList.add(new ArrayList<>());
        }
    }

    // Add a directed edge src → dest
    public void addEdge(int src, int dest) {
        adjList.get(src).add(dest);
    }

    // DFS utility function
    private boolean dfs(int node, boolean[] visited, boolean[] recStack) {
        if (recStack[node]) {
            return true; // Node already in recursion stack → cycle found
        }
        if (visited[node]) {
            return false; // Node already processed → no cycle from here
        }

        // Mark node as visited and add to recursion stack
        visited[node] = true;
        recStack[node] = true;

        // Visit all neighbors
        for (int neighbor : adjList.get(node)) {
            if (dfs(neighbor, visited, recStack)) {
                return true;
            }
        }

        // Remove from recursion stack (backtrack)
        recStack[node] = false;
        return false;
    }

    // Main function to check cycle
    public boolean hasCycle() {
        boolean[] visited = new boolean[vertices];
        boolean[] recStack = new boolean[vertices];

        // Check each node (handles disconnected graphs)
        for (int i = 0; i < vertices; i++) {
            if (dfs(i, visited, recStack)) {
                return true;
            }
        }
        return false;
    }

    public static void main(String[] args) {
        DirectedCycleDetection g = new DirectedCycleDetection(4);
        g.addEdge(0, 1);
        g.addEdge(1, 2);
        g.addEdge(2, 3);
        g.addEdge(3, 1); // Creates a cycle

        System.out.println("Graph contains cycle: " + g.hasCycle());
    }
}