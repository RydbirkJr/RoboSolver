package graph_v2;

import core.*;
import graph.BfsNode;
import graph.Edge;
import graph.Graph;
import graph.Vertex;
import minimum_moves.MinimumMoves;

import java.util.*;

/**
 * Created by Anders on 07/09/15.
 */
public class GraphSolver_v2 implements IGameSolver {
    private Goal goal;
    private int bestResult = Integer.MAX_VALUE;
    private HashSet<String> existingStates;

    public GraphSolver_v2(){
    }

    @Override
    public GameResult solveGame(Game game) {
        //this.vertices = buildGraph(game.fields);
        this.existingStates = new HashSet<String>();
        this.goal = game.goal;

        Graph graph = new Graph(game.fields);

        Queue<GameState> queue = new LinkedList<GameState>();

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

        int[][] minMoves = MinimumMoves.minimumMoves(game.fields, game.goal);

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
                graph.placeRobot(robot.row, robot.col);
            }

            BfsNode result = applyBfsSearch(graph.getVertex(GR.startField.row,GR.startField.col), bestResult - gameState.moves, minMoves);

            if(result != null){
                int res = result.distance + gameState.moves;
                if(res < bestResult) {
                    bestResult = res;
                    bestState = gameState;
                    bestBfs = result;
                }
            }

            //Add goal robot before making different moves.
            graph.placeRobot(GR.startField.row, GR.startField.col);

            //Remove robots from field
            //Move robots and create new game states now that we're at it.
            for(RobotState robot : gameState.states.values()){

                for(Edge edge : graph.getVertex(robot.row,robot.col).edges){
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
                graph.removeRobot(robot.row, robot.col);
            }
            graph.removeRobot(GR.startField.row, GR.startField.col);
        }
        return getResult(bestState, bestBfs);
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
