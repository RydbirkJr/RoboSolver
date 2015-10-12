package graph;

import core.Direction;
import core.Field;

/**
 * Created by Anders on 12/10/15.
 */
public class Graph {

    private Vertex[][] vertices;
    private Field[][] fields;

    public Graph(Field[][] fields){
        this.fields = fields;
        vertices = new Vertex[16][16];

        Field field;
        Vertex vertex;
        Direction[] directions = new Direction[] {Direction.NORTH, Direction.WEST};

        //Instantiate the vertices
        for(int row = 0; row < fields.length; row++){
            for(int col = 0; col < fields[0].length; col++){
                field = fields[row][col];

                vertex = getEdges(new Vertex(row, col), field, directions);

                vertices[row][col] = vertex;
            }
        }

        directions = new Direction[]{Direction.EAST, Direction.SOUTH};

        for(int row = 15; row >= 0; row--){
            for(int col = 15; col >= 0; col--){
                field = fields[row][col];
                vertex = vertices[row][col];

                //Since it only gets a pointer, it's already applied on the Vertex
                getEdges(vertex, field, directions);
            }
        }
    }

    public Vertex getVertex(int row, int col){
        return vertices[row][col];
    }

    private Vertex getEdges(Vertex vertex, Field field, Direction[] directions){
        for(Direction dir : directions){
            if(field.canMove(dir)){
                vertex.edges.add(new Edge(
                        getEdgeVertex(
                                field.row,
                                field.col,
                                dir),
                        dir));
            }
        }

        return vertex;
    }

    private Vertex getEdgeVertex(int row, int col, Direction dir){
        Vertex nextVertex = getNextVertex(row, col, dir);

        for (Edge e : nextVertex.edges) {
            if(e.direction == dir){
                return e.child;
            }
        }

        return nextVertex;
    }

    private Vertex getNextVertex(int row, int col, Direction direction){
        switch (direction){
            case NORTH: row--;
                break;
            case EAST: col++;
                break;
            case SOUTH: row++;
                break;
            case WEST: col--;
        }

        return vertices[row][col];
    }

    /**
     * There is NO error check in case a robot is placed on a field where another robot is already standing.
     * This should be handled by the solver itself, ensuring no robot can be placed on a field where another stands.
     * @param graph the graph.
     * @param fields the fields - needed for the can-move properties.
     * @param row 0-indexed robotRow number for insertion of a robot.
     * @param col 0-indexed robotCol number for insertion of a robot.
     */
    public void placeRobot(int row, int col) {
        Direction[] directions = new Direction[]{Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};

        outerloop:
        for (Direction dir : directions) {
            //Case 1: Cannot move
            if (!fields[row][col].canMove(dir)) continue;

            //Case 2: Can move, no edge present
            Edge e = getEdge(vertices[row][col], dir);
            if (e == null) {
                Vertex v = getNextVertex(row, col, dir);
                Edge e2 = getEdge(v, getOppositeDirection(dir));
                v.edges.remove(e2);
                continue;
            }

            //Case 3: Can move, edge present, child cannot move
            Vertex child = e.child;

            //Case 4: Can move, edge present, child can move
            if (fields[child.row][child.col].canMove(dir)) {
                child = getNextVertex(child.row, child.col, dir);
            }

            Vertex newChild = getNextVertex(row, col, dir);
            //Loop back through all fields between child and newChild

            boolean isSameVertex;
            Direction oppositeDirection = getOppositeDirection(dir);

            do {
                isSameVertex = (newChild.row == child.row && newChild.col == child.col);
                e = getEdge(child, oppositeDirection);
                //I know e is never null at this point

                if (isSameVertex) {
                    child.edges.remove(e);
                    continue outerloop;

                } else {
                    e.child = newChild;
                }

                child = getNextVertex(child.row, child.col, oppositeDirection);

            } while (!isSameVertex);

        }
    }

    /**
     * Removes a robot and updates the related vertices' edges to extend beyond the removed robot.
     * There is NO error checking, i.e., it's not even tested if a robot even stands on the field.
     * The related vertices will be updated anyhow.
     * @param graph The graph.
     * @param fields The fields, used for can-move properties.
     * @param row 0-indexed robotRow number for the robot to be removed.
     * @param col 0-indexed robotCol number for the robot to be removed.
     */
    public void removeRobot(int row, int col){
        updateOrientation(row, col, true);
        updateOrientation(row, col, false);
    }

    /**
     * Updates a specific direction, i.e., horizontal or vertical. Each related vertex is visited only once
     * during the update-edge phase.
     *
     * A From and To vertex is computed for each direction. In most cases these vertices is the same, however,
     * in case the two robots R1 and R2's start positions are on the same robotCol / robotRow and no obstacles in between, when removing
     * R1 and updating the direction towards R2, R2's Vertex will be the From Vertex while R1's edge in R2's direction's child
     * Vertex will be the To Vertex. In case R1 do not have any Edge in R2's direction, R1's Vertex will be the To Vertex.
     *
     * @param graph The graph.
     * @param fields The fields, used for can-move properties.
     * @param row 0-indexed robotRow number for the robot to be removed.
     * @param col 0-indexed robotCol number for the robot to be removed.
     * @param isHorizontal Indicates if horizontal or not.
     */
    private void updateOrientation(int row, int col, boolean isHorizontal){
        Direction dir = isHorizontal ? Direction.EAST : Direction.NORTH;

        Direction oppositeDirection = getOppositeDirection(dir);
        Vertex v1to = getToVertex(dir, row, col);
        Vertex v2to = getToVertex(oppositeDirection, row, col);
        Vertex v1from = getFromVertex(dir, v1to.row, v1to.col);
        Vertex v2from = getFromVertex(oppositeDirection, v2to.row, v2to.col);

        updateDirection(oppositeDirection, v1from, v2to);
        updateDirection(dir, v2from, v1to);
    }

    /**
     * Updates all Edges in Direction dir for each vertex between Vertex From (included) and Vertex To
     * (not included). Vertex To is set as the child for each edge in the given direction.
     *
     * @param dir
     * @param graph
     * @param from
     * @param to
     */
    private void updateDirection(Direction dir, Vertex from, Vertex to){

        //While they are not the same
        Vertex curVertex = from;
        while( !(curVertex.row == to.row && curVertex.col == to.col) ){

            Edge e = getEdge(curVertex, dir);
            if(e == null){
                e = new Edge(to, dir);
                curVertex.edges.add(e);
            } else {
                e.child = to;
            }

            curVertex = getNextVertex(curVertex.row, curVertex.col, dir);
        }

    }

    /**
     * Given the To Vertex,
     * @param direction The direction to process.
     * @param graph The given graph.
     * @param fields The given fields.
     * @param row 0-indexed robotRow number for the to-vertex.
     * @param col 0-indexed robotCol number for the to-vertex.
     * @return
     */
    private Vertex getFromVertex(Direction direction, int row, int col){
        if(fields[row][col].canMove(direction)){
            return getNextVertex(row, col, direction);
        } else {
            return vertices[row][col];
        }
    }

    /**
     * Returns the child vertex of the edge in the given direction. If no edge present, return self.
     *
     * @param direction
     * @param graph
     * @param row
     * @param col
     * @return
     */
    private Vertex getToVertex(Direction direction, int row, int col){

        Edge edge = getEdge(vertices[row][col], direction);

        if(edge != null){
            return edge.child;
        } else {
            return vertices[row][col];
        }

    }

    /**
     * O(k) where k = elements in the edge list -> k <= 4.
     * @param vertex
     * @param direction
     * @return Returns null if no edge in the given direction present.
     */
    private Edge getEdge(Vertex vertex, Direction direction){
        for(Edge edge : vertex.edges){
            if(edge.direction == direction){
                return edge;
            }
        }

        return null;
    }


    /**
     * Maps the given direction to the opposite direction.
     * @param dir
     * @return Given NORTH, returns SOUTH, etc.
     */
    private Direction getOppositeDirection(Direction dir){
        switch (dir){
            case NORTH: return Direction.SOUTH;
            case EAST: return Direction.WEST;
            case SOUTH: return Direction.NORTH;
            case WEST: return Direction.EAST;
            default: return Direction.NONE;
        }
    }
}
