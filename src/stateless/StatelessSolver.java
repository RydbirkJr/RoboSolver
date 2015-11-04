package stateless;

import core.*;

import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

/**
 * Created by Anders on 07/08/15.
 */
public class StatelessSolver implements IGameSolver {
    private Game game;
    private FieldData goalFieldData;
    private DataWrapper wrapper;
    private Direction[] directions;

    public GameResult solveGame(Game game){
        this.game = game;
        this.directions = new Direction[] {Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};
        this.wrapper = new DataWrapper(game.fields);
        this.goalFieldData = wrapper.getFieldData(game.goal.row,game.goal.col);

        PriorityQueue<RobotStats> queue = new PriorityQueue<RobotStats>(new RobotStatsComparator());
        initRobots(game.robots, queue);

        //Start popping the robot queue
        while(!queue.isEmpty()){
            RobotStats stats = queue.poll();

            //Update to move in all directions
            moveAllDirections(stats, queue);
        }

        return wrapper.formatResult(goalFieldData.getResult(game.goal.color));
    }

    private void initRobots(Robot[] robots, Queue<RobotStats> queue){

        //Place robots on startField
        for(Robot robot : robots){
            FieldData fieldData = wrapper.getFieldData(robot.startField.row, robot.startField.col);
            RobotStats stats = new RobotStats(true, 0, robot.color, Direction.NONE,fieldData, null);
            fieldData.setStarter(stats);
            queue.add(stats);
        }


    }

    private void moveAllDirections(RobotStats robot, Queue<RobotStats> queue){
        int row = robot.field.row;
        int col = robot.field.col;

        Field field = game.fields[row][col];

        //Queue moves if it's possible to move in the given direction
        for (Direction dir : directions) {
            if (field.canMove(dir)) {
                moveDirection(new MoveStats(
                                robot,
                                getNextField(row, col, dir),
                                dir,
                                robot.moves + 1),
                        queue
                );
            }
        }
    }

    /**
     *  Possible outcomes of this method:
     *  * The robot can move to the next field - no finals on the next exists.
     *  * The robot can move to the next field - but finals on next do exist. + add final to current field
     *  * The robot can move to the next field, but requires a robot move.
     *  * The robot cannot move to the next field - add final
     *
     * @param move Contains the field to be processed.
     */
    private void moveDirection(MoveStats move, Queue<RobotStats> queue) {
        boolean canMove = move.fieldData.field.canMove(move.direction);
        int nextMoveCount = move.reqRobotMove ? move.moveCount + 1 : move.moveCount;

        if(canMove){
            FieldData next = getNextField(move.fieldData.row, move.fieldData.col, move.direction);
            boolean isFinal = next.hasOtherFinals(move.origin.color);
            boolean reqMove = next.hasStarter(move.origin.color);
            RobotStats dependUpon = isFinal ? next.getLowestOtherFinal(move.origin.color) : null;

            RobotStats newStats =  new RobotStats(isFinal, move.moveCount, move.origin.color, move.direction, move.fieldData, move.origin, dependUpon);
            MoveStats newMove = new MoveStats(move.origin, next,move.direction, nextMoveCount, reqMove);

            //Add new states before continuing moveDirection with the new move
            if(move.fieldData.addRobotStats(newStats) && isFinal){
                queue.add(newStats);
                updateAdjacentFields(newStats, newStats.direction, queue);
            }

            if(!reqMove){
                moveDirection(newMove, queue);
            }


        } else {
            //Cannot move
            //Final and no more moves
            //Add to queue if successfully inserted
            RobotStats newStats =  new RobotStats(true, move.moveCount, move.origin.color, move.direction, move.fieldData, move.origin);
            if(move.fieldData.addRobotStats(newStats)){
                queue.add(newStats);
                updateAdjacentFields(newStats, newStats.direction, queue);
                //TODO Update adjacent fields
            }
        }
    }


    /**
     *
     * @param robot the final robot stats for the current field.
     * @param direction
     */
    private void updateAdjacentFields(RobotStats robot, Direction direction, Queue<RobotStats> queue) {

        for(Direction dir : directions){
            if(robot.field.field.canMove(dir) && dir != direction){
                FieldData field = getNextField(robot.field.row, robot.field.col,dir);
                List<RobotStats> result = field
                        .bubbleDownField(robot, getOpposite(dir));

                for(RobotStats stat : result){
                    stat.dependUpon = robot;
                    stat.moves += robot.moves;
                    stat.isFinal = true;
                    if(field.addRobotStats(stat)){
                        queue.add(stat);
                    }
                }
            }
        }
    }

    private FieldData getNextField(int row, int col, Direction direction){
        switch (direction){
            case NORTH: row--;
                break;
            case EAST: col++;
                break;
            case SOUTH: row++;
                break;
            case WEST: col--;
                break;
            case NONE:
                break;
        }

        return wrapper.getFieldData(row,col);
    }

    private Direction getOpposite(Direction dir){
        switch (dir){
            case NORTH: return Direction.SOUTH;
            case EAST: return Direction.WEST;
            case SOUTH: return Direction.NORTH;
            case WEST: return Direction.EAST;
        }
        return Direction.NONE;
    }


}