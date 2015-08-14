/**
 * Created by Anders on 05/08/15.
 */
public class RobotStats {
    boolean isFinal = false;
    int moves = 0;
    Color color;
    Direction direction;

    public RobotStats(boolean isFinal, int moves, Color color, Direction direction){
        this.isFinal = isFinal;
        this.moves = moves;
        this.color = color;
        this.direction = direction;
    }
}
