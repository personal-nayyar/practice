package code.graph;

import java.util.List;

public class UnreachableNode extends Graph {
    UnreachableNode(int v) {
        super(v);
    }

    UnreachableNode(int v, List<Integer>[] adj) {
        super(v, adj);
    }

    /*
    * if only needs to find unreachable node then simply do dfs from given node
    * if need to finds out all unreachable path b/w nodes
    *   - find the connected component of a graph
    *   - for evey pair of ele which are in diff component, unreachable path
    * */

    @Override
    void dfs(){
        for (int i = 0; i < v; i++) {
            boolean visited[] = new boolean[v];
            if(!visited[i]){
                System.out.println(i);
                dfsUtil(i, visited);
                for (int j = 0; j < v; j++) {
                    if (!visited[j] && i!=j)
                        System.out.print(j+" ");
                }
                System.out.println("");
            }
        }
    }

    void dfsUtil(int v, boolean visited[]){
        visited[v] = true;
        for (int x:adj[v]) {
            if (!visited[x])
                dfsUtil(x, visited);
        }
    }

    public static void main(String[] args)
    {
        Graph g =  new UnreachableNode(8);
        g.addEdgeUndirected(0,1);
        g.addEdgeUndirected(1, 2);
        g.addEdgeUndirected( 3, 4);
        g.addEdgeUndirected( 4, 5);
        g.addEdgeUndirected(6, 7);

        g.dfs();
    }
}
