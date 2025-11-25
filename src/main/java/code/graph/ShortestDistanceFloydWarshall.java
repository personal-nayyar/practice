package code.graph;

import java.util.Arrays;

class ShortestDistanceFloydWarshall {
    private static final int INF = 1000000; // A large value to represent "infinity"

    // Function to run Floyd-Warshall algorithm
    public static void floydWarshall(int[][] graph) {
        int V = graph.length;

        // Copy graph into dist matrix
        int[][] dist = new int[V][V];
        for (int i = 0; i < V; i++) {
            dist[i] = Arrays.copyOf(graph[i], V);
        }

        // Run Floyd–Warshall
        for (int k = 0; k < V; k++) {           // Pick each vertex as intermediate
            for (int i = 0; i < V; i++) {       // Source vertex
                for (int j = 0; j < V; j++) {   // Destination vertex
                    if (dist[i][k] != INF && dist[k][j] != INF) {
                        dist[i][j] = Math.min(dist[i][j], dist[i][k] + dist[k][j]);
                    }
                }
            }
        }

        // Print final shortest distance matrix
        printSolution(dist);
    }

    // Utility to print distance matrix
    private static void printSolution(int[][] dist) {
        int V = dist.length;
        System.out.println("All-Pairs Shortest Path (Floyd-Warshall):");
        for (int i = 0; i < V; i++) {
            for (int j = 0; j < V; j++) {
                if (dist[i][j] == INF)
                    System.out.print("INF ");
                else
                    System.out.print(dist[i][j] + " ");
            }
            System.out.println();
        }
    }

    public static void main(String[] args) {
        // Graph adjacency matrix (INF means no edge)
        int INF = ShortestDistanceFloydWarshall.INF;
        int[][] graph = {
                {0,   3,   INF,  5},
                {2,   0,   INF,  4},
                {INF, 1,   0,    INF},
                {INF, INF, 2,    0}
        };

        floydWarshall(graph);
    }
}