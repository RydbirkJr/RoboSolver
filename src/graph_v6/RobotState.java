package graph_v6;

import core.Color;
import core.Direction;

/**
 * Created by Anders on 18/09/15.
 */
public class RobotState {
    public final Color color;
    public final int moves;
    public final int row;
    public final int col;
    public final Direction dir;

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
