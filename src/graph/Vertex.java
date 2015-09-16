package graph;

import core.Direction;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by Anders on 07/09/15.
 */
public class Vertex {
    int row, col;
    List<Edge> edges;

    public Vertex(int row, int col){
        this.row = row;
        this.col = col;
        this.edges = new LinkedList<Edge>();
    }

}
