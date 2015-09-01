package basic;

import Core.Field;

/**
 * Created by Anders on 31/08/15.
 */
public class BasicField extends Field {
    public boolean hasRobot = false;

    public BasicField(Field field){
        super(field.row, field.col);
        this.canNorth = field.canNorth;
        this.canEast = field.canEast;
        this.canSouth = field.canSouth;
        this.canWest = field.canWest;
    }

}
