package basic;

import Core.Color;

/**
 * Created by Anders on 31/08/15.
 */
public class RobotState {
    int row;
    int col;
    Color color;

    public RobotState(Color color, int row, int col){
        this.color = color;
        this.row = row;
        this.col = col;

    }
}
