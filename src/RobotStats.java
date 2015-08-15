/**
 * Created by Anders on 05/08/15.
 */
public class RobotStats {
    boolean isFinal = false;
    int moves = 0;
    Color color;
    Direction direction;
    Field field;
    RobotStats prevRobot;
    RobotStats dependUpon;

    public RobotStats(boolean isFinal, int moves, Color color, Direction direction, Field field, RobotStats prevRobot){
        this(isFinal, moves, color, direction, field, prevRobot, null);
    }

    public RobotStats(boolean isFinal, int moves, Color color, Direction direction, Field field, RobotStats prevRobot, RobotStats dependUpon){
        this.isFinal = isFinal;
        this.moves = moves;
        this.color = color;
        this.direction = direction;
        this.field = field;
        this.prevRobot = prevRobot;
        this.dependUpon = dependUpon;

    }
}
