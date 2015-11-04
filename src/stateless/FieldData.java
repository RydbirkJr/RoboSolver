package stateless;

import core.Color;
import core.Direction;
import core.Field;

import java.util.*;

/**
 * Created by Anders on 27/08/15.
 */
public class FieldData {
    private HashMap<Color, RobotStats> finals;
    private HashMap<Direction, HashMap<Color, RobotStats>> intermediates;

    public RobotStats starter;
    public Field field;
    public int row;
    public int col;

    public FieldData(int row, int col, Field field){
        this.row = row;
        this.col = col;
        this.field = field;

        this.finals = new HashMap<Color, RobotStats>();
        this.intermediates = new HashMap<Direction, HashMap<Color, RobotStats>>();
        this.intermediates.put(Direction.NORTH, new HashMap<Color, RobotStats>());
        this.intermediates.put(Direction.EAST, new HashMap<Color, RobotStats>());
        this.intermediates.put(Direction.SOUTH, new HashMap<Color, RobotStats>());
        this.intermediates.put(Direction.WEST, new HashMap<Color, RobotStats>());
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

    /**
     * Adds or updates the final stats.
     * @param stats
     * @return Returns true if inserted, else false.
     */
    private boolean addFinalStats(RobotStats stats){
        RobotStats temp = finals.get(stats.color);

        if(temp != null){
            if(temp.moves > stats.moves){
                //Update the stored final if better
                temp.override(stats);
            }

            return false;

        } else {
            finals.put(stats.color, stats);
            return true;
        }
    }

    /**
     * Adds or updates the intermediate stats.
     * @param stats
     * @return Returns true if inserted, else false.
     */
    private boolean addIntermediateStats(RobotStats stats){
        RobotStats temp = intermediates.get(stats.direction).get(stats.color);

        if(temp != null){
            if(temp.moves > stats.moves){
                temp.override(stats);
            }
            return false;
        } else {
            intermediates.get(stats.direction).put(stats.color, stats);
            return true;
        }

    }

    public boolean addRobotStats(RobotStats stats){

        if(stats.isFinal){
            return addFinalStats(stats);
        } else {
            return addIntermediateStats(stats);
        }
    }

    public boolean hasOtherFinals(Color color){
        return (finals.size() + (finals.containsKey(color) ? -1 : 0) ) > 0;
    }

    public RobotStats getResult(Color goal){
        return finals.get(goal);
    }

    /**
     *
     * @param color
     * @return Returns lowest other final or null.
     */
    public RobotStats getLowestOtherFinal(Color color){

        RobotStats result = null;
        for(RobotStats stats : finals.values()){
            //Skip same color
            if(stats.color == color) continue;

            if(result == null) {
                result = stats;
            } else if(result.moves > stats.moves) {
                result = stats;
            }
        }

        return result;
    }

    public boolean hasStarter(Color color){
        return starter != null && starter.color != color;
    }

    /**
     *
     * @param robot
     * @param direction The direction the given robot has to travel to hit the final field.
     * @return A list of intermediate states that has been removed from the intermediates.
     * Should be inserted and eventually queued.
     */
    public List<RobotStats> bubbleDownField(RobotStats robot, Direction direction){
        List<RobotStats> result = new ArrayList<RobotStats>();
        HashMap<Color, RobotStats> temp = intermediates.get(direction);

        if(temp.isEmpty()) return result;

        for(Iterator<RobotStats> iterator = temp.values().iterator(); iterator.hasNext();){
            RobotStats next = iterator.next();

            if(next.color != robot.color){
                result.add(next);
                iterator.remove();
            }
        }

        return result;
    }
}


