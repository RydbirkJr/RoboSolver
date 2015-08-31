package RoboSolver;

import Core.Color;
import Core.Direction;
import Core.Field;

import java.util.*;

/**
 * Created by Anders on 27/08/15.
 */
public class FieldData {
    //private HashMap<Color, TreeSet<RobotStats>> robotStats;
    private ArrayList<RobotStats> finalStats;
    private ArrayList<RobotStats> intermediateStats;
    private HashSet<Color> finalIndicators;

    public RobotStats starter;
    public Field field;
    public int row;
    public int col;

    public FieldData(int row, int col, Field field){
        this.row = row;
        this.col = col;
        this.field = field;
        this.finalStats = new ArrayList<RobotStats>();
        this.intermediateStats = new ArrayList<RobotStats>();
        this.finalIndicators = new HashSet<Color>();
    }

    /**
     * Adds the stats to the different data structures.
     * In case there's already a final stat for the given color, returns false.
     * @param stats
     * @return False if color is already final on field.
     */

    public void setStarter(RobotStats stats){
        this.starter = stats;
        addRobotStats(stats);
    }

    public boolean addRobotStats(RobotStats stats){

        if(finalIndicators.contains(stats.color)){
            //if it's already visited by a final of the same color, it's not really interesting.. yet
            return false;
        }

        //Update if a final exists for the field
        if(stats.isFinal){
            finalIndicators.add(stats.color);
            finalStats.add(stats);
        } else {
            intermediateStats.add(stats);
        }

        return true;
    }



    public boolean hasOtherFinals(Color color){
        return finalIndicators != null && (finalIndicators.contains(color) ? -1 : 0) + finalIndicators.size() > 0;
    }

    public boolean hasBeenReached(Color color){
        return finalIndicators != null && finalIndicators.contains(color);
    }

    public RobotStats getResult(Color goal){
        for(RobotStats stats : finalStats){
            if(stats.color == goal) return stats;
        }

        return null;
    }

    public RobotStats getFinalFromOtherRobots(Color color){
        Collections.sort(finalStats, new RobotStatsComparator());
        Iterator<RobotStats> iterator = finalStats.iterator();

        while (iterator.hasNext()){
            RobotStats stat = iterator.next();
            if(stat.color != color){
                return stat;
            }
        }
        //Should not be hit
        return null;
    }

    /**
     *
     * @param robot
     * @param direction The direction the given robot has to travel to hit the final field
     */
    public boolean bubbleDownField(RobotStats robot, Direction direction){

        //In case no final stats exists: return
        if(intermediateStats == null) return false;

        //For each intermediate element in the field
        for(Iterator<RobotStats> it = intermediateStats.iterator(); it.hasNext();){
            RobotStats stats = it.next();

            if(stats.color != robot.color && stats.direction == direction){
                //Correct direction and the right color
                stats.moves += robot.moves;
                stats.isFinal = true;
                it.remove();
                finalIndicators.add(stats.color);
                finalStats.add(stats);
                //TODO: Should bubbled down fields also update adjacent fields?
            }
        }

        return true;
    }
}
