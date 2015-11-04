package stateless;

import java.util.Comparator;

/**
 * Created by Anders on 13/08/15.
 */
public class RobotStatsComparator implements Comparator<RobotStats> {

    @Override
    public int compare(RobotStats x, RobotStats y){
        return x.moves - y.moves;
    }
}
