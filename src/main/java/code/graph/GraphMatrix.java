package code.graph;

public class GraphMatrix {
    int vertices;
    int[][] adjMatrix;

    public GraphMatrix(int vertices) {
        this.vertices = vertices;
        adjMatrix = new int[vertices][vertices];
    }

    // Add edge (undirected)
    public void addEdge(int src, int dest) {
        adjMatrix[src][dest] = 1;
        adjMatrix[dest][src] = 1; // remove this line for directed graph
    }

    // Print matrix
    public void printGraph() {
        for (int i = 0; i < vertices; i++) {
            for (int j = 0; j < vertices; j++) {
                System.out.print(adjMatrix[i][j] + " ");
            }
            System.out.println();
        }
    }

    public static void main(String[] args) {
        GraphMatrix g = new GraphMatrix(4);
        g.addEdge(0, 1);
        g.addEdge(0, 2);
        g.addEdge(1, 2);
        g.addEdge(2, 3);

        g.printGraph();
    }
}