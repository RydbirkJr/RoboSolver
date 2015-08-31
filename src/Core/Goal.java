package Core;

import Core.Field;

/**
 * Created by Anders on 07/08/15.
 */

public class Goal extends Field {
    public Color color;

    public Goal(int row, int col, Color color){
        super(row, col);
        this.color = color;
    }

}