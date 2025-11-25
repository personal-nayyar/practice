package code.graph;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 *                                 1
 *                                - \
 *                              /    \
 *                             /      -
 *                            0 ----- 2
 *                                   /
 *                                  /
 *                                 -
 *                                 3------
 *                                 |     |
 *                                 |-----|
 * */

public class SerializeDeserializeGraph {
    public List<Integer> serialize(Graph root){
        int v =  root.v;
        List<Integer> adj[] =  root.adj;
        List<Integer> listResult = new ArrayList<>();
        boolean visited[] =  new boolean[v];
        for (int i = 0; i < v; i++) {
            if (!visited[i]){
                listResult.add(i);
                visited[i] = true;
                listResult.add(adj[i].size()); // put size as delimiter
                Iterator<Integer> iterator =  adj[i].iterator();
                while(iterator.hasNext()){
                    listResult.add(iterator.next());
                }
            }
        }
        return listResult;
    }
    public Graph deserialize(List<Integer> list){
        List<LinkedList<Integer>> adj = new ArrayList<>();
        for (int i = 0; i < list.size();) {
            LinkedList<Integer> list1 = new LinkedList<>();
            list1.add(list.get(i++)); // get vertex
            int edges = list.get(i++); // get edges for above vertex
            for (int j = 0; j < edges; j++) {
                list1.add(list.get(i++));
            }
            adj.add(list1);
        }
        return new Graph(adj.size(), adj.toArray(new LinkedList[0]));
    }

    public static void main(String []args)
    {

        // Create a graph given in the above diagram
        Graph g = new Graph(8);
        g.addEdge(0, 1);
        g.addEdge(0, 2);
        g.addEdge(1, 2);
        g.addEdge(3, 4);
        g.addEdge(4, 5);
        g.addEdge(6, 7);

        SerializeDeserializeGraph serializeDeserializeGraph =  new SerializeDeserializeGraph();
        List<Integer> serializedList = serializeDeserializeGraph.serialize(g);
        System.out.println(serializedList);
        Graph gg = serializeDeserializeGraph.deserialize(serializedList);
        System.out.println(gg);
    }
}