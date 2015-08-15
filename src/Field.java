import java.util.*;

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
    boolean isStartField = false;
    boolean isVisited = false;
    private HashSet<Color> finalIndicators;
    Color goalColor;
    private HashMap<Color, TreeSet<RobotStats>> robotStats;
    private ArrayList<RobotStats> finalStats;
    private ArrayList<RobotStats> intermediateStats;

    public Field(int row, int col){
        this.row = row;
        this.col = col;
    }

    public boolean addRobotStats(RobotStats stats){

        //Check if robotStats is set
        if(robotStats == null){
            robotStats = new HashMap<Color, TreeSet<RobotStats>>();
            isVisited = true;
            finalIndicators = new HashSet<Color>();
            finalStats = new ArrayList<RobotStats>();
            intermediateStats = new ArrayList<RobotStats>();
        }

        if(finalIndicators.contains(stats.color)){
            return false;
        }

        //Update if a final exists for the field
        if(stats.isFinal){
            finalIndicators.add(stats.color);
            finalStats.add(stats);
        } else {
            intermediateStats.add(stats);
        }

        //Check if color stats already exists
        if(!robotStats.containsKey(stats.color)) {
            robotStats.put(stats.color, new TreeSet<RobotStats>(new RobotStatsComparator()));
        }

        robotStats.get(stats.color).add(stats);

        return true;
    }

    public boolean canMove(Direction direction){
        switch (direction){
            case NORTH: return canNorth;
            case EAST: return canEast;
            case SOUTH: return canSouth;
            case WEST: return canWest;
            default:
                System.out.println("UNKNOWN DIRECTION GIVEN IN 'Field.canMove(..)'");
                System.exit(1);
                return false;
        }
    }

    public boolean hasOtherFinals(Color color){
        return finalIndicators != null && (finalIndicators.contains(color) ? -1 : 0) + finalIndicators.size() > 0;
    }

    public boolean hasBeenReached(Color color){
        return finalIndicators != null && finalIndicators.contains(color);
    }

    public RobotStats getResult(){
        for(RobotStats stats : finalStats){
            if(stats.color == goalColor) return stats;
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
        for(RobotStats stats : intermediateStats){
            if(stats.color != robot.color && stats.direction == direction){
                //Correct direction and the right color
                stats.moves += robot.moves;
                stats.isFinal = true;
                intermediateStats.remove(stats);
                finalIndicators.add(stats.color);
                finalStats.add(stats);
                //TODO: Should bubbled down fields also update adjacent fields?
            }
        }

        return true;
    }
}
