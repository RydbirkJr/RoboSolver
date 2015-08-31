package Core;

import RoboSolver.RobotStats;

import java.util.ArrayList;

/**
 * Created by Anders on 14/08/15.
 */
public class GameResult {
    public ArrayList<WinningMove> moves;
    public int moveCount;

    public GameResult(ArrayList<WinningMove> moves, int count){
        this.moves = moves;
        this.moveCount = count;
    }
}
