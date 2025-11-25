package code.graph;

import java.util.*;

class ShortestPathDijkstra {
    // Node class to store vertex and its distance
    static class Node implements Comparable<Node> {
        int vertex;
        int distance;

        Node(int v, int d) {
            vertex = v;
            distance = d;
        }

        // Min-Heap based on distance
        public int compareTo(Node other) {
            return this.distance - other.distance;
        }
    }

    private int vertices;
    private List<List<Node>> adjList;

    public ShortestPathDijkstra(int vertices) {
        this.vertices = vertices;
        adjList = new ArrayList<>();
        for (int i = 0; i < vertices; i++) {
            adjList.add(new ArrayList<>());
        }
    }

    // Add directed weighted edge
    public void addEdge(int src, int dest, int weight) {
        adjList.get(src).add(new Node(dest, weight));
    }

    // Dijkstra’s Algorithm
    public void dijkstra(int src) {
        int[] dist = new int[vertices];     // Distance from source
        Arrays.fill(dist, Integer.MAX_VALUE);
        dist[src] = 0;                      // Distance to source is 0

        PriorityQueue<Node> pq = new PriorityQueue<>();
        pq.add(new Node(src, 0));           // Start with source

        while (!pq.isEmpty()) {
            Node current = pq.poll();
            int u = current.vertex;

            // Explore neighbors
            for (Node neighbor : adjList.get(u)) {
                int v = neighbor.vertex;
                int weight = neighbor.distance;

                // Relaxation: check if new path is shorter
                if (dist[u] + weight < dist[v]) {
                    dist[v] = dist[u] + weight;
                    pq.add(new Node(v, dist[v])); // Push updated distance
                }
            }
        }

        // Print shortest distances
        System.out.println("Shortest distances from node " + src + ":");
        for (int i = 0; i < vertices; i++) {
            System.out.println("To " + i + " = " + dist[i]);
        }
    }

    public static void main(String[] args) {
        ShortestPathDijkstra g = new ShortestPathDijkstra(6);

//        // Create a weighted directed graph
//        g.addEdge(0, 1, 4);
//        g.addEdge(0, 2, 1);
//        g.addEdge(2, 1, 2);
//        g.addEdge(1, 3, 1);
//        g.addEdge(2, 3, 5);
//        g.addEdge(3, 4, 3);
//        g.addEdge(4, 5, 2);
//
//        g.dijkstra(0);


//        // Create a weighted directed graph
//        g.addEdge(0, 1, 10);
//        g.addEdge(0, 2, 5);
//        g.addEdge(1, 2, -8);
//
//        g.dijkstra(0);
//
//        // Create a weighted directed graph where Dijkstra's algorithm will fail to calculate shortest path
//        g.addEdge(0, 1, 10);
//        g.addEdge(0, 2, 5);
//        g.addEdge(1, 2, -8);
//        g.addEdge(2, 3, -1);
//
//        g.dijkstra(0);


        //(0) --4--> (1)
        //    |
        //    |
        //    5
        //    |
        //    v
        //   (2) --(-10)-> (1)

//        g.addEdge(0, 1, 4);
//        g.addEdge(0, 2, 5);
//        g.addEdge(2, 1, -10);
//
//        g.dijkstra(0);

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