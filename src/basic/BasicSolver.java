package basic;

import Core.*;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;

/**
 * Created by Anders on 31/08/15.
 */
public class BasicSolver implements IGameSolver {
    private HashSet<String> states;
    private LinkedList<GameState> gameQueue;
    private BasicField[][] fields;
    private Game game;
    private Direction[] directions;
    private boolean goalReached = false;
    private GameState result;

    public GameResult solveGame(Game game){
        initGame(game);

        while (!gameQueue.isEmpty()){
            //get state
            GameState state = gameQueue.pop();

            //Place robots on board
            updateRobotSate(state, true);

            //Move in all directions
            moveInAllDirections(state);

            if(goalReached){
                return getResult();
            }

            //Remove robots from board
            updateRobotSate(state, false);
        }
        return null;
    }

    private void moveInAllDirections(GameState oldState){
        for(RobotState robot : oldState.robots.values()){
            for(Direction dir : directions){
                GameState newState = moveDirection(dir, oldState, robot);
                String stateShort = newState.toString();
                if(!states.contains(stateShort)){
                    states.add(stateShort);
                    gameQueue.add(newState);
                }
            }
        }
    }

    private GameState moveDirection(Direction direction, GameState oldState, RobotState robotState){
        int row = robotState.row;
        int col = robotState.col;

        BasicField field;
        BasicField nextField = fields[row][col];

        do{
            field = nextField;

            switch (direction){
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

            //Must be applied before assigning next field in case it's off the board. Throws exception
            if (!field.canMove(direction)) {
                break;
            }

            //Set next for looping
            nextField = fields[row][col];

        } while(!nextField.hasRobot);

        GameState result = new GameState(oldState, new RobotState(robotState.color, field.row, field.col));

        //Check winning condition
        if(game.goal.color == robotState.color
            && game.goal.row == field.row
            && game.goal.col == field.col){

            this.goalReached = true;
            this.result = result;
        }

        return result;
    }

    private void updateRobotSate(GameState state, boolean value){
        for(RobotState robot : state.robots.values()){
            fields[robot.row][robot.col].hasRobot = value;
        }
    }

    //Converts the gameboard fields into solution specific fields
    private BasicField[][] initFields(Field[][] fields){
        BasicField[][] temp = new BasicField[16][16];
        for (int row = 0; row<16 ; row++){
            for(int col = 0; col < 16; col++){
                temp[row][col] = new BasicField(fields[row][col]);
            }
        }
        return temp;
    }

    private GameState initRobots(Robot[] robots){
        RobotState[] states = new RobotState[4];

        for(int i = 0; i < robots.length; i++){
            states[i] = new RobotState(robots[i].color,robots[i].startField.row, robots[i].startField.col);
            }
        return new GameState(states,null,0);
    }

    private void initGame(Game game){
        this.game = game;
        this.states = new HashSet<String>();
        this.gameQueue = new LinkedList<GameState>();
        this.fields = initFields(game.fields);
        this.directions =  new Direction[] {Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};

        GameState initState = initRobots(game.robots);

        states.add(initState.toString());
        gameQueue.add(initState);
    }

    private GameResult getResult(){
        //Use the global variable result

        GameState current = result;
        GameState prev;

        RobotState curRobot;
        RobotState prevRobot;

        ArrayList<WinningMove> moves = new ArrayList<WinningMove>();

        do{
            //Get previous state
            prev = current.prevState;

            //Get robots
            curRobot = current.robots.get(current.lastMoved);
            prevRobot = prev.robots.get(current.lastMoved);

            //Get diff between robots
            moves.add(getWinningMove(curRobot, prevRobot, current.moves));

            //Set current to prev
            current = prev;

        } while (current.moves != 0);

        Collections.reverse(moves);

        return new GameResult(moves, result.moves);
    }

    private WinningMove getWinningMove(RobotState cur, RobotState prev, int moves){

        Direction dir = null;

        //NOT PRETTY...
        if(cur.row < prev.row){
            dir = Direction.SOUTH;
        } else if(cur.row > prev.row){
            dir = Direction.NORTH;
        } else {
            if(cur.col < prev.col){
                dir = Direction.WEST;
            } else {
                dir = Direction.EAST;
            }
        }

        return new WinningMove(dir, cur.color, cur.row, cur.col, moves);
    }
}
