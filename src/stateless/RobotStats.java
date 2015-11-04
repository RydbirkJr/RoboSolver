package stateless;
import core.*;

/**
 * Created by Anders on 05/08/15.
 */
public class RobotStats {
    boolean isFinal = false;
    int moves = 0;
    Color color;
    Direction direction;
    FieldData field;
    RobotStats prevRobot;
    RobotStats dependUpon;

    public RobotStats(boolean isFinal, int moves, Color color, Direction direction, FieldData field, RobotStats prevRobot){
        this(isFinal, moves, color, direction, field, prevRobot, null);
    }

    public RobotStats(boolean isFinal, int moves, Color color, Direction direction, FieldData field, RobotStats prevRobot, RobotStats dependUpon){
        this.isFinal = isFinal;
        this.moves = moves + (dependUpon != null ? dependUpon.moves : 0);
        this.color = color;
        this.direction = direction;
        this.field = field;
        this.prevRobot = prevRobot;
        this.dependUpon = dependUpon;
    }

    /**
     * Will override only moving data, but never field, final state or color.
     * @param stats
     */
    public void override(RobotStats stats){
        this.moves = stats.moves;
        this.direction = stats.direction;
        this.prevRobot = stats.prevRobot;
        this.dependUpon = stats.dependUpon;
    }
}
