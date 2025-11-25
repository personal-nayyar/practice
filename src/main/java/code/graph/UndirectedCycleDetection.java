package code.graph;

import java.util.*;

public class UndirectedCycleDetection {
    private int vertices;                  // Number of vertices in the graph
    private List<List<Integer>> adjList;   // Adjacency list representation

    public UndirectedCycleDetection(int vertices) {
        this.vertices = vertices;
        adjList = new ArrayList<>();
        for (int i = 0; i < vertices; i++) {
            adjList.add(new ArrayList<>()); // Initialize adjacency list
        }
    }

    // Add an undirected edge
    public void addEdge(int src, int dest) {
        adjList.get(src).add(dest);  // Add edge src → dest
        adjList.get(dest).add(src);  // Add edge dest → src (since undirected)
    }

    // DFS helper to detect cycle
    private boolean dfs(int node, boolean[] visited, int parent) {
        visited[node] = true; // Mark current node as visited

        // Traverse neighbors
        for (int neighbor : adjList.get(node)) {
            if (!visited[neighbor]) {
                // If neighbor not visited, recurse
                if (dfs(neighbor, visited, node)) {
                    return true; // Cycle found in recursion
                }
            // If neighbor is visited and not parent, cycle exists (self-loop)
            // e.g. 0 -- 1 --- 2 --- 0
            } else if (neighbor != parent) {
                return true;
            }
        }
        return false; // No cycle from this path
    }

    // Main function to check cycle in graph
    public boolean hasCycle() {
        boolean[] visited = new boolean[vertices]; // Track visited nodes

        // Graph may be disconnected, check each component
        for (int i = 0; i < vertices; i++) {
            if (!visited[i]) {
                if (dfs(i, visited, -1)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static void main(String[] args) {
        UndirectedCycleDetection g = new UndirectedCycleDetection(5);
        g.addEdge(0, 1);
        g.addEdge(1, 2);
        g.addEdge(2, 3);
        g.addEdge(3, 0); // Adding this creates a cycle
        g.addEdge(3, 4);

        System.out.println("Graph contains cycle: " + g.hasCycle());
    }
}