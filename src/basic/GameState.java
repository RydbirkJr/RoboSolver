package basic;

import Core.Color;
import Core.WinningMove;

import java.util.HashMap;

/**
 * Created by Anders on 31/08/15.
 */
public class GameState {
    public HashMap<Color, RobotState> robots;
    public GameState prevState;
    public int moves;
    public Color lastMoved;

    /**
     * Init constructor, only for first round.
     * @param list
     * @param prevState
     * @param moves
     */
    public GameState(RobotState[] list, GameState prevState, int moves){
        robots = new HashMap<Color, RobotState>();
        for(RobotState robot : list){
            robots.put(robot.color, robot);
        }
        this.prevState = prevState;
        this.moves = moves;
    }

    /**
     * For when the game is running.
     * @param prevState
     * @param newState
     */
    public GameState(GameState prevState, RobotState newState){
        this.lastMoved = newState.color;
        this.moves = prevState.moves + 1;
        this.prevState = prevState;
        this.robots = new HashMap<Color, RobotState>(prevState.robots);
        this.robots.put(newState.color, newState);
    }

    public String toString(){
        RobotState red = robots.get(Color.RED);
        RobotState blue = robots.get(Color.BLUE);
        RobotState green = robots.get(Color.GREEN);
        RobotState yellow = robots.get(Color.YELLOW);

        return  "R" + red.row +":" + red.col
                + ";B" + blue.row + ":" + blue.col
                + ";G" + green.row + ":" + green.col
                + ";Y" + yellow.row + ":"+ yellow.col;

    }


}
