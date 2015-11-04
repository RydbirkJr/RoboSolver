package stateless;

import core.Direction;

/**
 * Created by Anders on 27/08/15.
 */
public class MoveStats {
    public RobotStats origin;
    public FieldData fieldData;
    public Direction direction;
    public boolean reqRobotMove;
    public int moveCount;

    public MoveStats(RobotStats stats, FieldData field, Direction direction, int moveCount){
        this(stats, field, direction, moveCount, false);
    }

    public MoveStats(RobotStats stats, FieldData field, Direction direction,int moveCount, boolean reqRobotMove){
        origin = stats;
        fieldData = field;
        this.direction = direction;
        this.moveCount = moveCount;
        this.reqRobotMove = reqRobotMove;
    }
}
