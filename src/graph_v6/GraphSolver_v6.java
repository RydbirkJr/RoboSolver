package graph_v6;

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
public class GraphSolver_v6 implements IGameSolver {
    private Goal goal;
    private int bestResult = Integer.MAX_VALUE;
    private HashSet<String> existingStates;
    private RobotState initState;

    public GraphSolver_v6(){
    }

    @Override
    public GameResult solveGame(Game game) {
        int searchStringHit = 0, continues = 0, hasBounced = 0;
        this.existingStates = new HashSet<>();
        this.goal = game.goal;

        Graph graph = new Graph(game.fields);

        Queue<GameState> queue = new LinkedList<>();

        int[][] minMoves = MinimumMoves.minimumMoves(game.fields, game.goal);

        GameState bestState = null;
        BfsNode bestBfs = null;

        GameState gameState = getInitialState(game.robots, goal.color);
        //InitState set by method

        existingStates.add(gameState.toString());

        queue.add(gameState);

        while(!queue.isEmpty()){
            //get element
            gameState = queue.poll();

            RobotState curSearch = gameState.states.get(goal.color);

            if( bestResult <= (gameState.moves + minMoves[curSearch.row][curSearch.col]) ){
                //Not feasible
                continues++;
                //System.out.println("BestResult: " + bestResult + "\t CurTotal: " + (gameState.moves + minMoves[curSearch.row][curSearch.col]) + "\t\tMoves: " + gameState.moves + "minMoves: " + minMoves[curSearch.row][curSearch.col]);
                continue;
            }

            for(RobotState robot : gameState.states.values()){
                if(robot.color == goal.color) continue;
                graph.placeRobot(robot.row, robot.col);
            }

            //Only apply BFS if the search state have not been searched before
            BfsNode result = applyBfsSearch(graph.getVertex(curSearch.row, curSearch.col), bestResult - gameState.moves, minMoves);

            if (result != null) {
                int res = result.distance + gameState.moves;
                if (res < bestResult) {
                    bestResult = res;
                    bestState = gameState;
                    bestBfs = result;
                }
            }

            //Add goal robot before making different moves.

            graph.placeRobot(curSearch.row, curSearch.col);

            //Remove robots from field
            //Move robots and create new game states now that we're at it.
            for(RobotState robot : gameState.states.values()){

                for(Edge edge : graph.getVertex(robot.row,robot.col).edges){
                    //If edge bounced off the goal robot, do something

                    GameState temp = new GameState(gameState, new RobotState(robot.color, robot.moves + 1, edge.child.row, edge.child.col, edge.direction), initState.color);

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
        }
        //System.out.println("Has bounced: " + hasBounced + "\tSearchStrHit: " + searchStringHit + "\tContinues: " + continues);
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

    private boolean isBetween(int start, int end, int inBetween){
        return (start < inBetween && inBetween <= end) || (end < inBetween && inBetween <= start);
    }

    private GameState getInitialState(Robot[] robots, Color goal){
        RobotState[] initStates = new RobotState[4];
        //Create initial game state
        int i = 0;
        for(Robot robot : robots){
            initStates[i] = new RobotState(robot.color, 0, robot.startField.row, robot.startField.col, Direction.NONE);

            if(robot.color == goal) this.initState = initStates[i];
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
