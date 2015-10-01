package graph_v2;

import core.*;
import game.MapHandler;
import org.junit.Test;
import org.springframework.util.Assert;

import java.util.List;

public class GraphSolverTest {

    @Test
    public void testBuildGraph() throws Exception {
        MapHandler mapHandler = new MapHandler();
        GameBoard board = mapHandler.setupGameBoard();

        Vertex[][] graph = new GraphSolver_v2().buildGraph(board.fields);

        Assert.isTrue(graph[0][0].edges.size() == 2, "(0,0) has 2 edges.");

        Vertex vertex = graph[7][3];

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

        GraphSolver_v2 solver = new GraphSolver_v2();

        Vertex[][] graph = solver.buildGraph(board.fields);

        //Before applying the robot
        //Testing that if there's only a single field between
        //the robot and an obstacle, the robot can move there,
        //but it cannot bounce back from that position

        Assert.isTrue(graph[9][4].edges.size() == 3);

        //After robot applied
        solver.placeRobot(graph, board.fields, 9,5);
        Assert.isTrue(graph[9][4].edges.size() == 2);
        Assert.isTrue(graph[9][5].edges.size() == 4);

        graph = solver.buildGraph(board.fields);

        //Tests that the edge is removed from the nearby field.
        solver.placeRobot(graph, board.fields, 9,6);
        Assert.isTrue(getEdge(graph[9][5].edges, Direction.EAST) == null);

        //Tests that the edge on step further away still remains and has been updated.
        Edge edge = getEdge(graph[9][4].edges, Direction.EAST);
        Assert.isTrue(edge.child.col == 5);

        solver.placeRobot(graph, board.fields, 9, 4);

        graph = solver.buildGraph(board.fields);

        solver.placeRobot(graph,board.fields, 15,5);
        solver.placeRobot(graph,board.fields, 9,5);

        edge = getEdge(graph[15][5].edges,Direction.NORTH);
        Assert.isTrue(edge.child.row == 10);

        graph = solver.buildGraph(board.fields);

        solver.placeRobot(graph, board.fields,7,5);
        solver.placeRobot(graph, board.fields,8,5);

        edge = getEdge(graph[7][5].edges,Direction.SOUTH);

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

        GraphSolver_v2 solver = new GraphSolver_v2();

        Vertex[][] graph = solver.buildGraph(board.fields);

        solver.placeRobot(graph, board.fields, 15,5);
        solver.placeRobot(graph,board.fields, 9,5);

        //No edge should be present for (9,4) after insertion
        Edge e = getEdge(graph[9][4].edges,Direction.EAST);
        Assert.isTrue(e == null);

        //Test removal
        solver.removeRobot(graph, board.fields, 9,5);
        e = getEdge(graph[9][4].edges,Direction.EAST);

        Assert.isTrue(e.child.col == 9);

        e = getEdge(graph[0][5].edges, Direction.SOUTH);

        Assert.isTrue(e.child.row == 14);
        Assert.isTrue(e.child.col == 5);

        e = getEdge(graph[15][5].edges, Direction.NORTH);

        Assert.isTrue(e.child.row == 0);

        graph = solver.buildGraph(board.fields);

        solver.placeRobot(graph, board.fields,5,1);
        solver.placeRobot(graph, board.fields,6,0);
        solver.placeRobot(graph, board.fields,5,0);

        Vertex v = graph[5][0];

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
            System.out.print("Goal: " + goal.color.name() + " " + (goal.row + 1) + ":" + (goal.col + 1));
            Game game = new Game(board.fields, robots, goal);
            GameResult result = new GraphSolver_v2().solveGame(game);
            System.out.println("\t" + result.moveCount);
        }

    }
}