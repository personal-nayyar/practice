package code.graph;

import java.util.ArrayList;
import java.util.List;

public class ConnectedComponents {

    public static List<List<Integer>> connectedComponents(Graph g){
        int v =  g.v;
        List<Integer> adj[] = g.adj;

        List<List<Integer>> resultList =  new ArrayList<>();
        boolean visited[] = new boolean[v];
        for (int i = 0; i < v; i++) {
            if(!visited[i]) {
                List<Integer> dfsList = new ArrayList<>();
                dfsUtil(i, visited, g, dfsList);
                resultList.add(dfsList);
            }
        }
        System.out.println(resultList);
        return resultList;
    }

    public static void dfsUtil(int v, boolean visited[], Graph g, List<Integer> dfsList){
        dfsList.add(v);
        visited[v] = true;
        for (Integer adjVer :g.adj[v]) {
            if(!visited[adjVer])
                dfsUtil(adjVer, visited, g, dfsList);
        }
    }

    public static void main(String[] args)
    {
        // Create a graph given in the above diagram
        Graph g = new Graph(
                5); // 5 vertices numbered from 0 to 4

        g.addEdgeUndirected(1, 0);
        g.addEdgeUndirected(2, 3);
        g.addEdgeUndirected(3, 4);
        System.out.println(
                "Following are connected components");
        ConnectedComponents.connectedComponents(g);
    }
}
