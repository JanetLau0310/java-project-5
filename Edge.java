public class Edge {
    private int begin;
    private int end;
    private String label;
    private Edge edge;

    public Edge(int beg, int end, String label){
        this.begin = beg;
        this.end = end;
        this.label = label;
    }

    public String getLabel(){ return this.label; }

    public void setLabel(String label) {
        this.label = label;
    }

    public Edge getEdge() {
        return edge;
    }

    public void setEdge(Edge edge) {
        this.edge = edge;
    }

    public int getBegin() {
        return begin;
    }

    public void setBegin(int begin) { this.begin = begin; }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }
}
