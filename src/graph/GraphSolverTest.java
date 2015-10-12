package graph;

import core.*;
import game.MapHandler;
import org.junit.Test;
import org.springframework.util.Assert;

import java.util.List;

public class GraphSolverTest {

    @org.junit.Test
    public void testBuildGraph() throws Exception {
        MapHandler mapHandler = new MapHandler();
        GameBoard board = mapHandler.setupGameBoard();

        Graph graph = new Graph(board.fields);

        Assert.isTrue(graph.getVertex(0,0).edges.size() == 2, "(0,0) has 2 edges.");

        Vertex vertex = graph.getVertex(7,3);

        Assert.isTrue(vertex.edges.size() == 4, "(8,4) has 4 edges.");

        for(Edge edge : vertex.edges){
            switch (edge.direction){
                case NORTH:
                    Assert.isTrue(edge.child.row == 0, "(8,4) moved to correct row north.");
                    Assert.isTrue(edge.child.col == 3);
                    break;
                case EAST:
                    Assert.isTrue(edge.child.row == 7);
                    Assert.isTrue(edge.child.col == 6, "(8,4) moved to correct col east.");
                    break;
                case SOUTH:
                    Assert.isTrue(edge.child.row == 8, "(8,4) moved to correct row south.");
                    Assert.isTrue(edge.child.col == 3);
                    break;
                case WEST:
                    Assert.isTrue(edge.child.row == 7);
                    Assert.isTrue(edge.child.col == 0, "(8,4) moved to correct col west.");
                    break;
            }
        }
    }

    @Test
    public void testPlaceRobot() throws Exception {
        MapHandler mapHandler = new MapHandler();
        GameBoard board = mapHandler.setupGameBoard();

        Graph graph = new Graph(board.fields);

        //Before applying the robot
        //Testing that if there's only a single field between
        //the robot and an obstacle, the robot can move there,
        //but it cannot bounce back from that position

        Assert.isTrue(graph.getVertex(9,4).edges.size() == 3);

        //After robot applied
        graph.placeRobot(9, 5);
        Assert.isTrue(graph.getVertex(9,4).edges.size() == 2);
        Assert.isTrue(graph.getVertex(9,5).edges.size() == 4);

        graph = new Graph(board.fields);

        //Tests that the edge is removed from the nearby field.
        graph.placeRobot(9, 6);
        Assert.isTrue(getEdge(graph.getVertex(9,5).edges, Direction.EAST) == null);

        //Tests that the edge on step further away still remains and has been updated.
        Edge edge = getEdge(graph.getVertex(9,4).edges, Direction.EAST);
        Assert.isTrue(edge.child.col == 5);

        graph.placeRobot(9, 4);

        graph = new Graph(board.fields);

        graph.placeRobot(15,5);
        graph.placeRobot(9,5);

        edge = getEdge(graph.getVertex(15,15).edges,Direction.NORTH);
        Assert.isTrue(edge.child.row == 10);

        graph = new Graph(board.fields);

        graph.placeRobot(7,5);
        graph.placeRobot(8,5);

        edge = getEdge(graph.getVertex(7,5).edges,Direction.SOUTH);

        Assert.isTrue(edge == null);
    }

    private Edge getEdge(List<Edge> edges, Direction direction){
        for(Edge edge : edges){
            if(edge.direction == direction){
                return edge;
            }
        }
        return null;
    }

    @Test
    public void testRemoveRobot() throws Exception {
        MapHandler mapHandler = new MapHandler();
        GameBoard board = mapHandler.setupGameBoard();

        Graph graph = new Graph(board.fields);

        graph.placeRobot(15,5);
        graph.placeRobot(9,5);

        //No edge should be present for (9,4) after insertion
        Edge e = getEdge(graph.getVertex(9,4).edges,Direction.EAST);
        Assert.isTrue(e == null);

        //Test removal
        graph.removeRobot(9,5);
        e = getEdge(graph.getVertex(9,4).edges,Direction.EAST);

        Assert.isTrue(e.child.col == 9);

        e = getEdge(graph.getVertex(0,5).edges, Direction.SOUTH);

        Assert.isTrue(e.child.row == 14);
        Assert.isTrue(e.child.col == 5);

        e = getEdge(graph.getVertex(15,15).edges, Direction.NORTH);

        Assert.isTrue(e.child.row == 0);

        graph = new Graph(board.fields);

        graph.placeRobot(5,1);
        graph.placeRobot(6,0);
        graph.placeRobot(5,0);

        Vertex v = graph.getVertex(5,0);

        Assert.isTrue(v.edges.size() == 0);
    }

    @Test
    public void testSolveGame() throws Exception {
        MapHandler mapHandler = new MapHandler();
        GameBoard board = mapHandler.setupGameBoard();
        Robot[] robots = mapHandler.getRobotPositions(board);

        Field[][] fields = board.fields;
//
//        Robot[] robots = new Robot[] {
//            new Robot(Color.BLUE, fields[11][12]),
//            new Robot(Color.RED, fields[5][15]),
//            new Robot(Color.GREEN, fields[8][5]),
//            new Robot(Color.YELLOW, fields[3][8])
//        };
//new Goal[] {new Goal(14,2, Color.GREEN)
        for(Goal goal : board.goals){
            System.out.println("Goal: " + goal.color.name() + " " + (goal.row + 1) + ":" + (goal.col + 1));
            Game game = new Game(board.fields, robots, goal);
            new GraphSolver().solveGame(game);
        }

    }
}