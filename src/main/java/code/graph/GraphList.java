package code.graph;

import java.util.*;

public class GraphList {
    int vertices;                  // Number of vertices in the graph
    List<List<Integer>> adjList;   // Adjacency list to store graph connections

    public GraphList(int vertices) {
        this.vertices = vertices;
        adjList = new ArrayList<>();
        // Initialize adjacency list for each vertex
        for (int i = 0; i < vertices; i++) {
            adjList.add(new ArrayList<>());
        }
    }

    // Add edge (undirected)
    public void addEdge(int src, int dest) {
        adjList.get(src).add(dest);
        adjList.get(dest).add(src); // remove this line for directed graph
    }

    // Print adjacency list
    public void printGraph() {
        for (int i = 0; i < vertices; i++) {
            System.out.print(i + " -> ");
            for (int neighbor : adjList.get(i)) {
                System.out.print(neighbor + " ");
            }
            System.out.println();
        }
    }

    // Recursive utility function for DFS
    private void dfsUtil(int node, boolean[] visited) {
        System.out.print(node + " ");      // Print the current node
        visited[node] = true;              // Mark the current node as visited

        // Explore all unvisited neighbors
        for (int neighbor : adjList.get(node)) {
            if (!visited[neighbor]) {
                dfsUtil(neighbor, visited);  // Recursively call DFS on neighbor
            }
        }
    }

    // Public function to perform DFS starting from a given node
    public void dfs(int start) {
        boolean[] visited = new boolean[vertices];  // Track visited nodes
        dfsUtil(start, visited);                    // Start DFS traversal
    }

    // BFS traversal starting from a given node
    public void bfs(int start) {
        boolean[] visited = new boolean[vertices]; // Track visited nodes
        Queue<Integer> queue = new LinkedList<>(); // Queue for BFS traversal

        visited[start] = true;     // Mark start node as visited
        queue.add(start);          // Enqueue the start node

        while (!queue.isEmpty()) { // Continue until queue is empty
            int node = queue.poll();          // Dequeue a node
            System.out.print(node + " ");     // Print the node

            // Explore all unvisited neighbors
            for (int neighbor : adjList.get(node)) {
                if (!visited[neighbor]) {
                    visited[neighbor] = true; // Mark neighbor as visited because enqueuing for printing
                    queue.add(neighbor);      // Enqueue neighbor
                }
            }
        }
    }

    public static void main(String[] args) {
        GraphList g = new GraphList(4);
        g.addEdge(0, 1);
        g.addEdge(0, 2);
        g.addEdge(1, 2);
        g.addEdge(2, 3);

//        g.printGraph();

        g.dfs(0);
    }
}