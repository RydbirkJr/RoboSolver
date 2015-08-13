import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeSet;

/**
 * Created by Anders on 05/08/15.
 */
public class Field {
    int row;
    int col;
    boolean canNorth = true;
    boolean canSouth = true;
    boolean canEast = true;
    boolean canWest = true;
    boolean isGoalField = false;
    boolean hasFinal = false;
    boolean isVisited = false;
    Color goalColor;
    public HashMap<Color, TreeSet<RobotStats>> robotStats;

    public Field(int row, int col){
        this.row = row;
        this.col = col;
    }
}
