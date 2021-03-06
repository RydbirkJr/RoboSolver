package core;

/**
 * Created by Anders on 05/08/15.
 */
public class Field {
    public boolean canNorth = true;
    public boolean canSouth = true;
    public boolean canEast = true;
    public boolean canWest = true;
    public int row;
    public int col;

    public Field(int row, int col){
        this.row = row;
        this.col = col;
    }

    public boolean canMove(Direction direction){
        switch (direction){
            case NORTH: return canNorth;
            case EAST: return canEast;
            case SOUTH: return canSouth;
            case WEST: return canWest;
            default:
                System.out.println("UNKNOWN DIRECTION GIVEN IN 'Core.Field.canMove(..)'");
                System.exit(1);
                return false;
        }
    }
}
