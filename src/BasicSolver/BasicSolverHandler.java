package BasicSolver;

import Core.*;

import java.util.HashSet;
import java.util.LinkedList;

/**
 * Created by Anders on 31/08/15.
 */
public class BasicSolverHandler implements IGameSolver {
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
                //DONE
                System.out.println(result.moves);
                return null;
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

        BasicField field = null;
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

            if (!field.canMove(direction)) {
                break;
            }

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
}
