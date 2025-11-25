package code.graph;

import java.util.*;

public class TopologicalSortKahn {
    private int vertices;                     // Number of vertices
    private List<List<Integer>> adjList;      // Graph as adjacency list

    public TopologicalSortKahn(int vertices) {
        this.vertices = vertices;
        adjList = new ArrayList<>();
        for (int i = 0; i < vertices; i++) {
            adjList.add(new ArrayList<>());   // Initialize adjacency list
        }
    }

    // Add a directed edge src → dest
    public void addEdge(int src, int dest) {
        adjList.get(src).add(dest);
    }

    // Perform topological sort using Kahn's algorithm
    public void topologicalSort() {
        int[] indegree = new int[vertices]; // Store in-degree of each vertex

        // Step 1: Compute in-degrees
        for (int i = 0; i < vertices; i++) {
            for (int neighbor : adjList.get(i)) {
                indegree[neighbor]++;
            }
        }

        // Step 2: Collect nodes with in-degree 0
        Queue<Integer> queue = new LinkedList<>();
        for (int i = 0; i < vertices; i++) {
            if (indegree[i] == 0) {
                queue.add(i);
            }
        }

        List<Integer> topoOrder = new ArrayList<>(); // Store result order

        // Step 3: Process queue
        while (!queue.isEmpty()) {
            int node = queue.poll();
            topoOrder.add(node);

            // Reduce in-degree of neighbors
            for (int neighbor : adjList.get(node)) {
                indegree[neighbor]--;
                if (indegree[neighbor] == 0) {
                    queue.add(neighbor);
                }
            }
        }

        // Step 4: Check if topological sort possible
        if (topoOrder.size() == vertices) {
            System.out.println("Topological Order: " + topoOrder);
        } else {
            System.out.println("Cycle detected! Topological sort not possible.");
        }
    }

    public static void main(String[] args) {
        TopologicalSortKahn g = new TopologicalSortKahn(6);

        // Example graph
        g.addEdge(5, 2);
        g.addEdge(5, 0);
        g.addEdge(4, 0);
        g.addEdge(4, 1);
        g.addEdge(2, 3);
        g.addEdge(3, 1);

        g.topologicalSort(); // Valid topo sort exists

        TopologicalSortKahn g2 = new TopologicalSortKahn(4);
        g2.addEdge(0, 1);
        g2.addEdge(1, 2);
        g2.addEdge(2, 3);
        g2.addEdge(3, 1); // Cycle here

        g2.topologicalSort(); // Should detect cycle
    }
}