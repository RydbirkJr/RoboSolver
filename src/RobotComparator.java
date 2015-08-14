import java.util.Comparator;

/**
 * Created by Anders on 13/08/15.
 */
public class RobotComparator implements Comparator<Robot> {

    @Override
    public int compare(Robot x, Robot y){
        return x.moveCount - y.moveCount;
    }
}
