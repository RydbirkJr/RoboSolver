package graph_v2;

import core.Color;
import core.Direction;

/**
 * Created by Anders on 18/09/15.
 */
public class RobotState {
    final Color color;
    final int moves;
    final int row;
    final int col;
    final Direction dir;

    public RobotState(Color color, int moves, int row, int col, Direction dir){
        this.color = color;
        this.moves = moves;
        this.row = row;
        this.col = col;
        this.dir = dir;
    }

    public String toString(){
        return row + ":" + col;
    }
}
