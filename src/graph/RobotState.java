package graph;

import core.Color;

/**
 * Created by Anders on 18/09/15.
 */
public class RobotState {
    final Color color;
    final int moves;
    final int row;
    final int col;

    public RobotState(Color color, int moves, int row, int col){
        this.color = color;
        this.moves = moves;
        this.row = row;
        this.col = col;
    }

    public String toString(){
        return row + ":" + col;
    }
}
