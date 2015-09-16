package graph;

import core.Direction;

/**
 * Created by Anders on 16/09/15.
 */
public class Edge {
    Vertex parent;
    Vertex child;
    Direction direction;

    public Edge(Vertex v1, Vertex v2, Direction direction){
        this.parent = v1;
        this.child = v2;
        this.direction = direction;
    }
}
