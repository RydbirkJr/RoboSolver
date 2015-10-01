package graph_v2;

import core.*;
import minimum_moves.MinimumMoves;

import java.util.*;

/**
 * Created by Anders on 07/09/15.
 */
public class GraphSolver_v2 implements IGameSolver {
    private Vertex[][] graph;
    private Field[][] fields;
    private int robotRow, robotCol; //Goal robot robotRow robotCol
    private Goal goal;
    private Queue<GameState> queue;
    private int bestResult = Integer.MAX_VALUE;
    private HashSet<String> existingStates;

    public GraphSolver_v2(){
    }

    @Override
    public GameResult solveGame(Game game) {
        this.fields = game.fields;
        this.graph = buildGraph(game.fields);
        this.queue = new LinkedList<GameState>();
        this.existingStates = new HashSet<String>();
        this.goal = game.goal;

        Robot[] OR = new Robot[3];
        Robot GR = null;

        int i = 0;
        for(Robot robot : game.robots){
            if(robot.color == game.goal.color){
                GR = robot;
            } else {
                OR[i] = robot;
                i++;
            }
        }

        int[][] minMoves = MinimumMoves.minimumMoves(this.fields, game.goal);

        GameState bestState = null;
        BfsNode bestBfs = null;

        GameState gameState = getInitialState(game.robots, game.goal.color);
        existingStates.add(gameState.toString());

        queue.add(gameState);

        while(!queue.isEmpty()){
            //get element
            gameState = queue.poll();

            if( bestResult <= (gameState.moves + minMoves[GR.startField.row][GR.startField.col]) ){
                //satisfied the search
                break;
            }

            for(RobotState robot : gameState.states.values()){
                this.placeRobot(graph, fields, robot.row, robot.col);
            }

            BfsNode result = applyBfsSearch(graph[robotRow][robotCol], bestResult - gameState.moves, minMoves);

            if(result != null){
                int res = result.distance + gameState.moves;
                //System.out.println("Best: " + bestResult + " New result:BFS: " + result.distance + " GS:Moves:" + gameState.moves);
                if(res < bestResult) {
                    bestResult = res;
                    bestState = gameState;
                    bestBfs = result;
                }
            }

            //Add goal robot before making different moves.
            placeRobot(graph, fields, robotRow, robotCol);

            //Remove robots from field
            //Move robots and create new game states now that we're at it.
            for(RobotState robot : gameState.states.values()){

                for(Edge edge : graph[robot.row][robot.col].edges){
                    GameState temp = new GameState(gameState, new RobotState(robot.color, robot.moves + 1, edge.child.row, edge.child.col, edge.direction));

                    //If state already exists, continue
                    String lookup = temp.toString();

                    if(!existingStates.contains(lookup)){
                        //else, create new state and add to queue
                        existingStates.add(lookup);
                        queue.add(temp);
                    }
                }
            }

            for(RobotState robot : gameState.states.values()) {
                this.removeRobot(graph, fields, robot.row, robot.col);
            }
            removeRobot(graph, fields, robotRow, robotCol);
        }
        return getResult(bestState, bestBfs);
    }

    public Vertex[][] buildGraph(Field[][] fields){
        Vertex[][] graph = new Vertex[16][16];

        Field field;
        Vertex vertex;
        Direction[] directions = new Direction[] {Direction.NORTH, Direction.WEST};

        //Instantiate the vertices
        for(int row = 0; row < fields.length; row++){
            for(int col = 0; col < fields[0].length; col++){
                field = fields[row][col];

                vertex = getEdges(new Vertex(row, col), field, directions, graph);

                graph[row][col] = vertex;
            }
        }

        directions = new Direction[]{Direction.EAST, Direction.SOUTH};

        for(int row = 15; row >= 0; row--){
            for(int col = 15; col >= 0; col--){
                field = fields[row][col];
                vertex = graph[row][col];

                //Since it only gets a pointer, it's already applied on the Vertex
                getEdges(vertex, field, directions, graph);
            }
        }

        return graph;
    }

    private Vertex getEdges(Vertex vertex, Field field, Direction[] directions, Vertex[][] graph){
        for(Direction dir : directions){
            if(field.canMove(dir)){
                vertex.edges.add(new Edge(
                        getEdgeVertex(
                                field.row,
                                field.col,
                                graph, dir),
                        dir));
            }
        }

        return vertex;
    }

    private Vertex getEdgeVertex(int row, int col, Vertex[][] graph, Direction dir){
        Vertex nextVertex = getNextVertex(row, col, graph, dir);

        for (Edge e : nextVertex.edges) {
            if(e.direction == dir){
                return e.child;
            }
        }

        return nextVertex;
    }

    private Vertex getNextVertex(int row, int col, Vertex[][] graph, Direction direction){
        switch (direction){
            case NORTH: row--;
                break;
            case EAST: col++;
                break;
            case SOUTH: row++;
                break;
            case WEST: col--;
        }

        return graph[row][col];
    }

    /**
     * There is NO error check in case a robot is placed on a field where another robot is already standing.
     * This should be handled by the solver itself, ensuring no robot can be placed on a field where another stands.
     * @param graph the graph.
     * @param fields the fields - needed for the can-move properties.
     * @param row 0-indexed robotRow number for insertion of a robot.
     * @param col 0-indexed robotCol number for insertion of a robot.
     */
    public void placeRobot(Vertex[][] graph, Field[][] fields, int row, int col) {
        Direction[] directions = new Direction[]{Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};

        outerloop:
        for (Direction dir : directions) {
            //Case 1: Cannot move
            if (!fields[row][col].canMove(dir)) continue;

            //Case 2: Can move, no edge present
            Edge e = getEdge(graph[row][col], dir);
            if (e == null) {
                Vertex v = getNextVertex(row, col, graph, dir);
                Edge e2 = getEdge(v, getOppositeDirection(dir));
                v.edges.remove(e2);
                continue;
            }

            //Case 3: Can move, edge present, child cannot move
            Vertex child = e.child;

            //Case 4: Can move, edge present, child can move
            if (fields[child.row][child.col].canMove(dir)) {
                child = getNextVertex(child.row, child.col, graph, dir);
            }

            Vertex newChild = getNextVertex(row, col, graph, dir);
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

                child = getNextVertex(child.row, child.col, graph, oppositeDirection);

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
    public void removeRobot(Vertex[][] graph, Field[][] fields, int row, int col){
        updateOrientation(graph, fields, row, col, true);
        updateOrientation(graph, fields, row, col, false);
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
    private void updateOrientation(Vertex[][] graph, Field[][] fields, int row, int col, boolean isHorizontal){
        Direction dir = isHorizontal ? Direction.EAST : Direction.NORTH;

        Direction oppositeDirection = getOppositeDirection(dir);
        Vertex v1to = getToVertex(dir, graph, row, col);
        Vertex v2to = getToVertex(oppositeDirection, graph, row, col);
        Vertex v1from = getFromVertex(dir, graph, fields, v1to.row, v1to.col);
        Vertex v2from = getFromVertex(oppositeDirection, graph, fields, v2to.row, v2to.col);

        updateDirection(oppositeDirection, graph, v1from, v2to);
        updateDirection(dir, graph, v2from, v1to);
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
    private void updateDirection(Direction dir, Vertex[][] graph, Vertex from, Vertex to){

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

            curVertex = getNextVertex(curVertex.row, curVertex.col, graph, dir);
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
    private Vertex getFromVertex(Direction direction, Vertex[][] graph, Field[][] fields, int row, int col){
        if(fields[row][col].canMove(direction)){
            return getNextVertex(row, col, graph, direction);
        } else {
            return graph[row][col];
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
    private Vertex getToVertex(Direction direction, Vertex[][] graph, int row, int col){

        Edge edge = getEdge(graph[row][col], direction);

        if(edge != null){
            return edge.child;
        } else {
            return graph[row][col];
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


    private BfsNode applyBfsSearch(Vertex root, int max, int[][] minMoves){
        BfsNode[][] result = new BfsNode[16][16];
        BfsNode rootNode = new BfsNode(0, null, root, Direction.NONE);

        result[root.row][root.col] = rootNode;

        Queue<BfsNode> queue = new LinkedList<BfsNode>();

        queue.add(rootNode);

        //int count = 0;
        while (!queue.isEmpty()){
            //count++;
            BfsNode node = queue.poll();

            //Since every node is investigated in incremental manner
            //If max is reached, the bfs is not relevant
            if(node.distance == max) break;
            if((minMoves[node.vertex.row][node.vertex.col] + node.distance) >= max) continue;

            for(Edge edge : node.vertex.edges){
                Vertex vertex = edge.child;
                BfsNode temp = result[vertex.row][vertex.col];
                if(temp == null){
                    temp = new BfsNode(node.distance+1,node, vertex, edge.direction);
                    result[vertex.row][vertex.col] = temp;
                    queue.add(temp);
                }
            }
        }

        return result[goal.row][goal.col];
    }

    private GameState getInitialState(Robot[] robots, Color goalColor){
        RobotState[] initStates = new RobotState[3];

        int i = 0;
        //Create initial game state
        for(Robot robot : robots){
            if(goalColor == robot.color) {
                this.robotRow = robot.startField.row;
                this.robotCol = robot.startField.col;
                continue;
            }

            initStates[i] = new RobotState(robot.color, 0, robot.startField.row, robot.startField.col, Direction.NONE);
            i++;
        }

        return new GameState(initStates);
    }

    private GameResult getResult(GameState result, BfsNode bfsNode){
        int distance = result.moves + bfsNode.distance;

        ArrayList<WinningMove> moves = new ArrayList<WinningMove>();

        int counter = 0;
        while(bfsNode.distance > 0){
            moves.add(new WinningMove(bfsNode.direction, goal.color,bfsNode.vertex.row, bfsNode.vertex.col,distance - counter));
            counter++;
            bfsNode = bfsNode.parent;
        }

        while(result.moves > 0){
            RobotState robot = result.states.get(result.colorChanged);

            moves.add(new WinningMove(robot.dir, robot.color, robot.row, robot.col, distance - counter));

            counter++;
            result = result.prev;
        }

        Collections.reverse(moves);

        return new GameResult(moves, distance);
    }
}
