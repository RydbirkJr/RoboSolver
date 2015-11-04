package graph_v4;

import core.*;
import graph.*;
import minimum_moves.MinimumMoves;

import java.util.*;

/**
 * Created by Anders on 07/09/15.
 */
public class GraphSolver_v4 implements IGameSolver {
    private Goal goal;
    private int bestResult = Integer.MAX_VALUE;
    private HashSet<String> existingStates;
    private HashSet<String> searchStates;
    private RobotState initState;

    public GraphSolver_v4(){
    }

    @Override
    public GameResult solveGame(Game game) {
        this.existingStates = new HashSet<>();
        this.searchStates = new HashSet<>();
        this.goal = game.goal;

        Graph graph = new Graph(game.fields);
        Queue<GameState> queue = new LinkedList<>();
        int[][] minMoves = MinimumMoves.minimumMoves(game.fields, game.goal);

        GameState bestState = null;
        BfsNode bestBfs = null;

        GameState gameState = getInitialState(game.robots, goal.color);

        initState = gameState.searchState;

        existingStates.add(gameState.toString());

        queue.add(gameState);

        while(!queue.isEmpty()){
            //get element
            gameState = queue.poll();

            RobotState search = gameState.searchState;
            RobotState curSearch = gameState.states.get(goal.color);

            //Termination condition
            if(bestResult < (gameState.moves + 2)) break;

            for(RobotState robot : gameState.states.values()){
                if(robot.color == goal.color) continue;
                graph.placeRobot(robot.row, robot.col);
            }

            //Only apply BFS if the search state have not been searched before
            String searchString = gameState.toSearchString();
            if(!searchStates.contains(searchString)){
                searchStates.add(searchString);
                BfsNode result = applyBfsSearch(graph.getVertex(search.row,search.col), bestResult - gameState.moves + gameState.goalMoves, minMoves);

                if(result != null){
                    int res = result.distance + gameState.moves;
                    if(res < bestResult) {
                        bestResult = res;
                        bestState = gameState;
                        bestBfs = result;
                    }
                }
            }

            if( !((gameState.moves + 1) >= bestResult) ){
                //Add goal robot before making different moves.
                graph.placeRobot(curSearch.row, curSearch.col);

                //Remove robots from field
                //Move robots and create new game states now that we're at it.
                for(RobotState robot : gameState.states.values()){

                    for(Edge edge : graph.getVertex(robot.row,robot.col).edges){
                        //If edge bounced off the goal robot, do something

                        GameState temp;

                        boolean isGoalRobot = robot.color == goal.color;

                        if(hasBouncedOnRobot(game.fields, edge, gameState, isGoalRobot) || passedGoalStart(robot.row, robot.col, edge)){
                            //Set new search origin
                            temp = new GameState(gameState, new RobotState(robot.color, robot.moves + 1, edge.child.row, edge.child.col, edge.direction), curSearch, isGoalRobot);
                        } else {
                            //Use old search origin
                            temp = new GameState(gameState, new RobotState(robot.color, robot.moves + 1, edge.child.row, edge.child.col, edge.direction));
                        }


                        //If state already exists, continue
                        String lookup = temp.toString();

                        if(!existingStates.contains(lookup)){
                            //else, create new state and add to queue
                            existingStates.add(lookup);
                            queue.add(temp);
                        }
                    }
                }
                graph.removeRobot(curSearch.row, curSearch.col);
            }

            for(RobotState robot : gameState.states.values()) {
                if(robot.color == initState.color) continue;
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

    private boolean hasBouncedOnRobot(Field[][] fields, Edge e, GameState state, boolean isGoalRobot){
        int row = e.child.row;
        int col = e.child.col;

        if(!fields[row][col].canMove(e.direction)) return false;

        switch (e.direction){
            case NORTH: row--;
                break;
            case EAST: col++;
                break;
            case SOUTH: row++;
                break;
            case WEST: col--;
                break;
        }

        for(RobotState robot : state.states.values()){
            if(robot.row == row && robot.col == col){
                //This robot stands on the next field in the given direction
                if(isGoalRobot){
                    //return true if robot found and isGoalRobot == true
                    return true;
                } else {
                    //Return true if robot is the goal robot and isGoalRobot = false
                    return robot.color == state.searchState.color;
                }
            }
        }

        return false;
    }

    public boolean passedGoalStart(int row, int col, Edge edge){
        //Horizontal movement
        if(row == edge.child.row && row == initState.row){
            return isBetween(col, edge.child.col, initState.col);
        } else {
            //Vertical movement
            return  isBetween(row, edge.child.row, initState.row);
        }
    }

    private boolean isBetween(int start, int end, int inBetween){
        return (start < inBetween && inBetween <= end) || (end < inBetween && inBetween <= start);
    }

    private GameState getInitialState(Robot[] robots, Color goal){
        RobotState[] initStates = new RobotState[4];
        //Create initial game state
        int i = 0;
        RobotState searchState = null;
        for(Robot robot : robots){
            initStates[i] = new RobotState(robot.color, 0, robot.startField.row, robot.startField.col, Direction.NONE);

            if(robot.color == goal) searchState = initStates[i];
            i++;
        }

        return new GameState(initStates, searchState);
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
