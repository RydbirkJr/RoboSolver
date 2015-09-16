package graph;

/**
 * Created by Anders on 11/09/15.
 */
public class BfsNode {
    public int distance;
    public BfsNode parent;
    public Vertex vertex;

    public BfsNode(int distance, BfsNode parent, Vertex vertex){
        this.distance = distance;
        this.parent = parent;
        this.vertex = vertex;
    }
}
