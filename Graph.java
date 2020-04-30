import java.util.ArrayList;

public class Graph {
    private int V;//number of nodes
    private ArrayList<Integer>[] adj;//adj matrix
    private boolean[] visited;

    public Graph(int V){
        this.V = V;
        adj =  (ArrayList<Integer>[]) new ArrayList[V];
        for(int v=0;v<V;v++) {
            adj[v] = new ArrayList<Integer>();
        }
    }
    public void addEdge(int v, int w){
        adj[v].add(w);
    }
    public int getSize(){
        return V;
    }
    public Iterable<Integer> adj(int v){
        return (Iterable<Integer>)adj[v];
    }
    // check whether visited or not

    public Graph(Graph G,int s){
        visited = new boolean[G.getSize()];
        dfs(G,s);
    }

    public Graph(Graph G,Iterable<Integer> sources){
        visited = new boolean[G.getSize()];
        for(int s:sources) {
            if (!visited[s]) {
                dfs(G, s);
            }
        }
    }
    // for every recursive, go through its adjoin table
    private void dfs(Graph G,int v){
        visited[v] = true;
        for(int w:G.adj(v)) {
            if (!visited[w]) {
                dfs(G, w);
            }
        }
    }
    public boolean visited(int v){ return visited[v]; }
}
