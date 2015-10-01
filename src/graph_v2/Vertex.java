package graph_v2;

import java.util.LinkedList;
import java.util.List;

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
