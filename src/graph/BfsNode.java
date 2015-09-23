package graph;

import core.Direction;

/**
 * Created by Anders on 11/09/15.
 */
public class BfsNode {
    public int distance;
    public BfsNode parent;
    public Vertex vertex;
    public Direction direction;

    public BfsNode(int distance, BfsNode parent, Vertex vertex, Direction direction){
        this.distance = distance;
        this.parent = parent;
        this.vertex = vertex;
        this.direction = direction;
    }
}
