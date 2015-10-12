package graph;

import core.Direction;

/**
 * Created by Anders on 16/09/15.
 */
public class Edge {
    public Vertex child;
    public Direction direction;

    public Edge(Vertex v2, Direction direction){
        this.child = v2;
        this.direction = direction;
    }
}
