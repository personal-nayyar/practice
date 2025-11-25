package code.graph;

import java.util.*;

public class ShortestPathUnweighted {
    private int vertices;
    private List<List<Integer>> adjList;

    public ShortestPathUnweighted(int vertices) {
        this.vertices = vertices;
        adjList = new ArrayList<>();
        for (int i = 0; i < vertices; i++) {
            adjList.add(new ArrayList<>());
        }
    }

    // Add undirected edge
    public void addEdge(int src, int dest) {
        adjList.get(src).add(dest);
        adjList.get(dest).add(src); // remove this line if directed
    }

    // BFS to find shortest path from source to all nodes
    public void shortestPath(int src) {
        int[] dist = new int[vertices];     // Distance from source
        int[] parent = new int[vertices];   // To reconstruct path store parent of each node
        Arrays.fill(dist, -1);              // -1 means unvisited
        Arrays.fill(parent, -1);

        Queue<Integer> queue = new LinkedList<>();
        dist[src] = 0;                      // Source distance = 0
        queue.add(src);

        while (!queue.isEmpty()) {
            int node = queue.poll();
            for (int neighbor : adjList.get(node)) {
                if (dist[neighbor] == -1) { // Not visited
                    dist[neighbor] = dist[node] + 1; // Distance = parent distance + 1(default weight = 1)
                    parent[neighbor] = node;
                    queue.add(neighbor);
                }
            }
        }

        // Print shortest distance
        System.out.println("Shortest distances from node " + src + ":");
        for (int i = 0; i < vertices; i++) {
            System.out.println("To " + i + " = " + dist[i]);
        }

        // Example: Print path from src to destination (say 4)
        int dest = 4;
        if (dist[dest] != -1) {
            System.out.print("Path from " + src + " to " + dest + ": ");
            List<Integer> path = new ArrayList<>();
            for (int v = dest; v != -1; v = parent[v]) {
                path.add(v);
            }
            Collections.reverse(path);
            System.out.println(path);
        }
    }

    public static void main(String[] args) {
        ShortestPathUnweighted g = new ShortestPathUnweighted(6);

        // Create graph
        g.addEdge(0, 1);
        g.addEdge(0, 2);
        g.addEdge(1, 3);
        g.addEdge(2, 4);
        g.addEdge(3, 5);
        g.addEdge(4, 5);

        g.shortestPath(0);
    }
}