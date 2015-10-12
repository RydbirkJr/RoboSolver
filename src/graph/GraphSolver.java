package graph;

import core.*;

import java.util.*;

/**
 * Created by Anders on 07/09/15.
 */
public class GraphSolver implements IGameSolver {
    private Graph graph;
    private int robotRow, robotCol; //Goal robot robotRow robotCol
    private Goal goal;
    private Queue<GameState> queue;
    private int bestResult = Integer.MAX_VALUE;
    private HashSet<String> existingStates;

    public GraphSolver(){
    }

    @Override
    public GameResult solveGame(Game game) {
        this.graph = new Graph(game.fields);
        this.queue = new LinkedList<GameState>();
        this.existingStates = new HashSet<String>();
        this.goal = game.goal;

        GameState bestState = null;
        BfsNode bestBfs = null;

        GameState gameState = getInitialState(game.robots, game.goal.color);
        existingStates.add(gameState.toString());

        queue.add(gameState);

        while(!queue.isEmpty()){
            //get element
            gameState = queue.poll();

            if( bestResult < (gameState.moves + 2) ){
                //satisfied the search
                break;
            }

            for(RobotState robot : gameState.states.values()){
                graph.placeRobot(robot.row, robot.col);
            }

            BfsNode result = applyBfsSearch(graph.getVertex(robotRow, robotCol));

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
            graph.placeRobot(robotRow, robotCol);

            //Remove robots from field
            //Move robots and create new game states now that we're at it.
            for(RobotState robot : gameState.states.values()){

                for(Edge edge : graph.getVertex(robot.row, robot.col).edges){
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
            graph.removeRobot(robotRow, robotCol);
        }
        return getResult(bestState, bestBfs);
    }


    private BfsNode applyBfsSearch(Vertex root){
        BfsNode[][] result = new BfsNode[16][16];
        BfsNode rootNode = new BfsNode(0, null, root, Direction.NONE);

        result[root.row][root.col] = rootNode;

        Queue<BfsNode> queue = new LinkedList<BfsNode>();

        queue.add(rootNode);

        while (!queue.isEmpty()){
            BfsNode node = queue.poll();
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
