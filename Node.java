import java.util.ArrayList;

public class Node {
    private int state;
    private static int ID=0; // count the number of nodes
    public boolean isStart;
    public boolean isEnd;
    ArrayList<Character> label;
    ArrayList<Node> desNode;// adjacent array

    public Node(){
        this.state=ID++;
        label = new ArrayList<>();
        desNode = new ArrayList<>();
        isStart = false;
        isEnd = false;
    } // make sure that every state is special

    public void addEdge(char s, Node d){
        label.add(s);
        desNode.add(d);
    }

    public int getState(){
        return this.state;
    }
}
