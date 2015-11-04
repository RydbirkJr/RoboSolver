package graph_v3;

import core.*;
import graph.*;
import minimum_moves.MinimumMoves;

import java.util.*;

/**
 * Created by Anders on 07/09/15.
 */
public class GraphSolver_v3 implements IGameSolver {
    private Goal goal;
    private int bestResult = Integer.MAX_VALUE;
    private HashSet<String> existingStates;

    public GraphSolver_v3(){
    }

    @Override
    public GameResult solveGame(Game game) {
        //this.vertices = buildGraph(game.fields);
        this.existingStates = new HashSet<>();
        this.goal = game.goal;

        Graph graph = new Graph(game.fields);

        Queue<GameState> queue = new LinkedList<>();

        int[][] minMoves = MinimumMoves.minimumMoves(game.fields, game.goal);

        GameState bestState = null;
        BfsNode bestBfs = null;

        GameState gameState = getInitialState(game.robots);
        existingStates.add(gameState.toString());

        queue.add(gameState);

        while(!queue.isEmpty()){
            //get element
            gameState = queue.poll();

            RobotState GR = gameState.states.get(goal.color);

            if(bestResult < (gameState.moves + 2)) break;

//            if( bestResult <= (gameState.moves + minMoves[GR.row][GR.col]) ){
//                //satisfied the search
//                continue;
//            }

            for(RobotState robot : gameState.states.values()){
                if(robot.color == goal.color) continue;
                graph.placeRobot(robot.row, robot.col);
            }

            //Only apply BFS if it's not the goal robot that moved last
            if(gameState.colorChanged != goal.color){
                BfsNode result = applyBfsSearch(graph.getVertex(GR.row,GR.col), bestResult - gameState.moves, minMoves);

                if(result != null){
                    int res = result.distance + gameState.moves;
                    if(res < bestResult) {
                        bestResult = res;
                        bestState = gameState;
                        bestBfs = result;
                    }
                }
            }

            //In case the next level search is worse than the current best result, skip the movement phase
            if( !((gameState.moves + 1) >= bestResult) ){
                //Add goal robot before making different moves.
                graph.placeRobot(GR.row, GR.col);

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

                graph.removeRobot(GR.row, GR.col);
            }

            for(RobotState robot : gameState.states.values()) {
                if(robot.color == GR.color) continue;
                graph.removeRobot(robot.row, robot.col);
            }
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

    private GameState getInitialState(Robot[] robots){
        RobotState[] initStates = new RobotState[4];
        //Create initial game state
        int i = 0;
        for(Robot robot : robots){

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
