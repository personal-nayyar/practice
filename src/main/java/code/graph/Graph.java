package code.graph;

import java.util.*;

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
class GraphMapImpl{
    Map<Integer, List<Integer>> g;

    GraphMapImpl(int v){
        for (int i = 0; i < v; i++) {
            g.put(i, new ArrayList<>());
        }
    }
}

public class Graph {
    int v;
    List<Integer> adj[]; // child

    Graph(int v){
        this.v = v;
        adj =  new ArrayList[v];
        for (int i = 0; i < v; i++) {
            adj[i] = new ArrayList<>();
        }
    }

    Graph(int v, List<Integer> adj[]){
        this.v = v;
        this.adj = adj;
    }

    void addEdge(int u, int v){
        adj[u].add(v);
//        adj[v].add(u); // for an undirected graph
    }

    void addEdgeUndirected(int u, int v){
        adj[u].add(v);
        adj[v].add(u);
    }

    // BFS
    void bfs(int root){
        boolean visited[] = new boolean[v];
        Queue<Integer> queue =  new LinkedList<>();
        queue.add(root);
        visited[root] = true;
        while (!queue.isEmpty()){
            Integer el =  queue.poll();
            System.out.println(el);
            Iterator<Integer> itr =  adj[el].iterator();
            while (itr.hasNext()){
                Integer next = itr.next();
                if (!visited[next]){
                    queue.add(next);
                    visited[next] = true;
                }
            }
        }
    }

    void dfs(){
        boolean visited[] =  new boolean[v];
        for (int i = 0; i < v; i++) {
            if (!visited[i])
                dfsUtil(i, visited);
        }
    }

    void dfsUtil(int v, boolean visited[]){
        System.out.println(v);
        visited[v] = true;

        Iterator<Integer> itr = adj[v].iterator();
        while (itr.hasNext()){
            Integer el = itr.next();
            if (!visited[el]){
                dfsUtil(el, visited);
            }
        }
    }

    void topologicalSort(){
        boolean visited[] = new boolean[v];
        Stack<Integer> stack =  new Stack<>();
        for (int i = 0; i < v; i++) {
            if (!visited[i])
                topologicalSortUtil(i, visited, stack);
        }

        while (stack.empty() == false)
            System.out.print(stack.pop() + " ");
    }
    void topologicalSortUtil(int v, boolean visited[], Stack<Integer> stack){
        visited[v] =  true;

        Iterator<Integer> itr = adj[v].iterator();
        while (itr.hasNext()){
            Integer next =  itr.next();
            if(!visited[next]){
                topologicalSortUtil(next, visited, stack);
            }
        }
        stack.push(v);
    }
}
