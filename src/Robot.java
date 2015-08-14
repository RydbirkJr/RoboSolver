/**
 * Created by Anders on 13/08/15.
 */
public class Robot {
    public final Color color;
    public final Field startField;
    public final int moveCount;

    public Robot(Color color, Field startField, int moveCount){
        this.color = color;
        this.startField = startField;
        this.moveCount = moveCount;
    }
}
