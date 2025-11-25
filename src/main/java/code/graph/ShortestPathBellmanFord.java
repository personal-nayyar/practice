package code.graph;

import java.util.*;

class ShortestPathBellmanFord {
    // Edge representation
    static class Edge {
        int src, dest, weight;
        Edge(int s, int d, int w) {
            src = s;
            dest = d;
            weight = w;
        }
    }

    private int vertices;
    private List<Edge> edges;

    public ShortestPathBellmanFord(int vertices) {
        this.vertices = vertices;
        edges = new ArrayList<>();
    }

    // Add directed weighted edge
    public void addEdge(int src, int dest, int weight) {
        edges.add(new Edge(src, dest, weight));
    }

    // Bellman-Ford Algorithm
    public void bellmanFord(int src) {
        // Step 1: Initialize distances
        int[] dist = new int[vertices];
        Arrays.fill(dist, Integer.MAX_VALUE);
        dist[src] = 0;

        // Step 2: Relax all edges V-1 times
        for (int i = 1; i <= vertices - 1; i++) {
            for (Edge e : edges) {
                if (dist[e.src] != Integer.MAX_VALUE && dist[e.src] + e.weight < dist[e.dest]) {
                    dist[e.dest] = dist[e.src] + e.weight;
                }
            }
        }

        // Step 3: Check for negative-weight cycles
        for (Edge e : edges) {
            if (dist[e.src] != Integer.MAX_VALUE && dist[e.src] + e.weight < dist[e.dest]) {
                System.out.println("Graph contains negative weight cycle!");
                return;
            }
        }

        // Print shortest distances
        System.out.println("Shortest distances from node " + src + ":");
        for (int i = 0; i < vertices; i++) {
            System.out.println("To " + i + " = " + dist[i]);
        }
    }

    public static void main(String[] args) {
        ShortestPathBellmanFord g = new ShortestPathBellmanFord(5);

//        // Create graph with a negative edge
//        g.addEdge(0, 1, -1);
//        g.addEdge(0, 2, 4);
//        g.addEdge(1, 2, 3);
//        g.addEdge(1, 3, 2);
//        g.addEdge(1, 4, 2);
//        g.addEdge(3, 2, 5);
//        g.addEdge(3, 1, 1);
//        g.addEdge(4, 3, -3);
//
//        g.bellmanFord(0);

        // Test case: Graph with negative weight cycle
        // 0---10---3---6
        // |    |    |
        // 5---2    -4
        // |
        // 9
        ShortestPathBellmanFord g2 = new ShortestPathBellmanFord(6);
        g2.addEdge(0, 1, 10);
        g2.addEdge(0, 5, -2);
        g2.addEdge(1, 2, 3);
        g2.addEdge(2, 3, 6);
        g2.addEdge(3, 4, 4);
        g2.addEdge(2, 4, -1);
        g2.addEdge(5, 4, 9);
//        g2.addEdge(3,1,-10); // Creates a cycle
        g2.bellmanFord(0);
    }
}