import java.util.ArrayList;

public class Graph {
    private final int V;//number of nodes
    private int E;//number of edges
    private ArrayList<Integer>[] adj;//adj matrix

    public Graph(int V){
        this.V = V;
        this.E = 0;
        adj =  (ArrayList<Integer>[]) new ArrayList[V];
        for(int v=0;v<V;v++) {
            adj[v] = new ArrayList<Integer>();
        }
    }
    public void addEdge(int v, int w){
        adj[v].add(w);
        E++;
    }
    public int getV(){
        return V;
    }

    public Iterable<Integer> adj(int v){
        return (Iterable<Integer>)adj[v];
    }
}
