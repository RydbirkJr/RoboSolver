package graph;

import core.*;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * Created by Anders on 07/09/15.
 */
public class GraphSolver implements IGameSolver {
    private Vertex[][] graph;
    private Direction[] directions;
    private HashMap<Color, Vertex[][]> robotGraphs;
    private Field[][] fields;
    private Robot goalRobot;

    @Override
    public GameResult solveGame(Game game) {
        fields = game.fields;
        directions = new Direction[]{Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST};
        graph = buildGraph(game.fields);
        robotGraphs = new HashMap<Color, Vertex[][]>();

        //Init the individual graphs
        for(Robot robot : game.robots){
            robotGraphs.put(robot.color, copyGraph(graph));
        }

        //Update graphs with robots
        for(Map.Entry<Color, Vertex[][]> entry : robotGraphs.entrySet()){
            Color mapColor = entry.getKey();
            for(Robot robot : game.robots){
                if(robot.color != mapColor){
                    //Only enter this if the color does not match
                    updateGraph(entry.getValue(), robot.startField);

                    if(game.goal.color == robot.color){
                        goalRobot = robot;
                    }
                }
            }
        }

        BfsNode[][] result = applyBfsSearch(robotGraphs.get(goalRobot.color)[goalRobot.startField.row][goalRobot.startField.col]);

        if(result[game.goal.row][game.goal.col] != null){
            int dist = result[game.goal.row][game.goal.col].distance;

            System.out.println("Graph solver dist: " + dist);
        }

        return null;
    }

    private Vertex[][] buildGraph(Field[][] fields){
        Vertex[][] graph = new Vertex[16][16];

        Field field;
        Vertex vertex;
        Direction[] directions = new Direction[] {Direction.NORTH, Direction.WEST};

        //Instantiate the vertices
        for(int row = 0; row < fields.length; row++){
            for(int col = 0; col < fields[0].length; col++){
                field = fields[row][col];

                vertex = getEdges(new Vertex(row, col), field, directions);

                graph[row][col] = vertex;
            }
        }

        directions = new Direction[]{Direction.EAST, Direction.SOUTH};

        for(int row = 15; row > 0; row--){
            for(int col = 15; col > 0; col--){
                field = fields[row][col];
                vertex = graph[row][col];

                //Since it only gets a pointer, it's already applied on the object
                getEdges(vertex, field, directions);
            }
        }

        return graph;
    }

    private Vertex getEdges(Vertex vertex, Field field, Direction[] directions){
        for(Direction dir : directions){
            if(field.canMove(dir)){
                vertex.edges.add(new Edge(
                        vertex,
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
        switch (dir){
            case NORTH: row--;
                break;
            case EAST: col++;
                break;
            case SOUTH: row++;
                break;
            case WEST: col--;
        }

        Vertex nextVertex = graph[row][col];

        for (Edge e : nextVertex.edges) {
            if(e.direction == dir){
                return e.child;
            }
        }

        return nextVertex;
    }

    /**
     * Will return the same field if it's not validated that it can move in the given direction in the first place.
     * @param fields
     * @param direction
     * @param field
     * @return
     */
    private Field loopUntilObstacleHit(Field[][] fields, Direction direction, Field field){

        while (field.canMove(direction)){
            field = getNextField(direction, field, fields);
        }

        return field;
    }



    private void updateGraph(Vertex[][] graph, Field robotField){
        Vertex robotVertex = graph[robotField.row][robotField.col];


        for (Direction dir : directions){
            //Working fields
            Vertex curVertex = robotVertex;
            Field curField = robotField;

            //Next fields
            Field nextField;
            Vertex nextVertex;

            //The vertex to be used as a new vertex
            Vertex newEdge = null;

            //Used for special case in first iteration
            boolean isFirst = true;

            while (curField.canMove(dir) && curVertex.edges.containsKey(dir)){
                nextField = getNextField(dir, curField, fields);
                nextVertex = graph[nextField.row][nextField.col];

                //Opposite direction - used for applying new edges
                Direction oppositeDirection = getOppositeDirection(dir);

                if(isFirst){
                    nextVertex.edges.remove(oppositeDirection);
                    newEdge = nextVertex;
                    isFirst = false;
                } else {
                    nextVertex.edges.put(oppositeDirection,newEdge);
                }

                curField = nextField;
                curVertex = nextVertex;
            }
        }
    }

    private Direction getOppositeDirection(Direction dir){
        switch (dir){
            case NORTH: return Direction.SOUTH;
            case EAST: return Direction.WEST;
            case SOUTH: return Direction.NORTH;
            case WEST: return Direction.EAST;
            default: return Direction.NONE;
        }
    }

    private BfsNode[][] applyBfsSearch(Vertex root){
        BfsNode[][] result = new BfsNode[16][16];

        BfsNode rootNode = new BfsNode(0, null, root);

        result[root.row][root.col] = rootNode;

        LinkedList<BfsNode> queue = new LinkedList<BfsNode>();

        queue.push(rootNode);

        while (!queue.isEmpty()){
            BfsNode node = queue.poll();
            for(Vertex vertex : node.vertex.edges.values()){
                BfsNode temp = result[vertex.row][vertex.col];
                if(temp == null){
                    temp = new BfsNode(node.distance+1,node, vertex);
                    result[vertex.row][vertex.col] = temp;
                    queue.add(temp);
                }
            }
        }

        return result;
    }


    private Field getNextField(Direction dir, Field field, Field[][] fields){
        int row = field.row;
        int col = field.col;

        switch (dir){
            case NORTH:
                row--;
                break;
            case EAST:
                col++;
                break;
            case SOUTH:
                row++;
                break;
            case WEST:
                col--;
                break;
        }

        //Set next for looping
        return fields[row][col];
    }
}
