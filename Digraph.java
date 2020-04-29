import java.util.ArrayList;
import java.util.Stack;

class Digraph{
    private final int V;
    private int E;
    private ArrayList<Integer>[] adj;
    public Digraph(int V){
        this.V = V;
        this.E = 0;
        adj =  (ArrayList<Integer>[]) new ArrayList[V];
        for(int v=0;v<V;v++) {
            adj[v] = new ArrayList<Integer>();
        }
    }
    public void addEdge(int v,int w){
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
