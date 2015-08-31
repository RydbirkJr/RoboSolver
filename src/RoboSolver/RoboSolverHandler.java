package RoboSolver;

import Core.*;

import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;

/**
 * Created by Anders on 07/08/15.
 */
public class RoboSolverHandler implements IGameSolver {
    private Game game;
    private FieldData goalFieldData;
    private PriorityQueue<RobotStats> robotQueue;
    private Queue<MoveStats> moveQueue;
    private DataWrapper wrapper;
    private Direction[] directions;

    public GameResult solveGame(Game game){
        this.game = game;
        initDataStructures();

        //Start popping the robot queue
        while(!robotQueue.isEmpty()){
            RobotStats robot = robotQueue.poll();
            moveAllDirections(robot);

            while(!moveQueue.isEmpty()){
                moveDirection(moveQueue.remove());
            }

            if(goalFieldData.hasBeenReached(game.goal.color)){
                RobotStats finalStats = goalFieldData.getResult(game.goal.color);

                //If the result is above the current level, continue
                if(finalStats.moves <= (robot.moves +1)){
                    return wrapper.formatResult(finalStats);
                }
            }
        }

        return null;
    }

    private void moveAllDirections(RobotStats robot){
        int row = robot.field.row;
        int col = robot.field.col;

        Field field = game.fields[row][col];

        //Queue moves if it's possible to move in the given direction
        for (Direction dir : directions){
            if(field.canMove(dir)){
                moveQueue.add(
                        new MoveStats(
                            robot,
                            getNextField(row, col, dir),
                            dir,
                            robot.moves + 1)

                        //TODO: Should I add a starter-check here?
                );
            }
        }
    }

    /**
     *  Possible outcomes of this method:
     *  * The robot can move to the next field - no finals on the next exists.
     *  * The robot can move to the next field, but requires a robot move.
     *  * The robot can move to the next field - but finals on next do exist.
     *  * The robot cannot move to the next field - add final
     *
     * @param move Contains the field to be processed.
     */
    private void moveDirection(MoveStats move) {
        boolean canMove = move.fieldData.field.canMove(move.direction);
        //Increment move counts
        int moveCount = move.moveCount + (move.reqRobotMove ? 1 : 0);
        boolean reqRobotMove = false;
        boolean isFinal = true; //depends upon the next field
        RobotStats dependUpon = null;

        if (canMove) {
            FieldData nextField = getNextField(move.fieldData.row, move.fieldData.col, move.direction);

            if (nextField.starter != null) {
                //Require robot move
                reqRobotMove = true;
                isFinal = true;
            } else if (nextField.hasOtherFinals(move.origin.color)) {
                //this field has to be final too - depend upon
                isFinal = true;
                dependUpon = nextField.getFinalFromOtherRobots(move.origin.color);
            } else {
                //No robot interaction
                isFinal = false;
            }

            //Add next move because it can move.
            moveQueue.add(
                    new MoveStats(
                            move.origin,
                            nextField,
                            move.direction,
                            moveCount,
                            reqRobotMove));

            /**
             * Tjekker om der kan rykkes i den givne retning
             * Hvis ja:
             * - Hent næste felt
             * - Hvis næste felt er et starter-felt - increment move count med reqRobotMove
             *      - Indsæt final robotstats
             *      - Kør videre med moveCount +1
             *
             * - Hvis næste felt har final
             *      - Increment med lowest final not same color
             *      - tilføj final robotstats
             *      - Enqueue final robot stats
             *      - kør videre med dependUpon
             *
             * - Hvis næste felt ikke har nogle af delene:
             *      - Is final false
             *      - dependUpon null
             *      - Hvis den kan tilføjes til feltet:
             *          - add new move
             *
             * - Hvis ikke kan move:
             *      - new final
             *      - add
             */

        }

        //Add the robot stats to the field.
        RobotStats robotStats = new RobotStats(isFinal, moveCount, move.origin.color, move.direction, move.fieldData, move.origin, dependUpon);

        if (move.fieldData.addRobotStats(robotStats) && isFinal) {
            //Update adjacent fields:
            updateAdjacentFields(robotStats, move.direction);

            //Add the new final to the robot queue for further processing.
            robotQueue.add(robotStats);
        }
    }


    /**
     *
     * @param robot the final robot stats for the current field.
     * @param direction
     */
    private void updateAdjacentFields(RobotStats robot, Direction direction) {
        if (robot.field.field.canNorth && direction != Direction.NORTH) {
            getNextField(robot.field.row, robot.field.col, Direction.NORTH).bubbleDownField(robot, Direction.SOUTH);
        }
        if (robot.field.field.canEast && direction != Direction.EAST) {
            getNextField(robot.field.row, robot.field.col, Direction.EAST).bubbleDownField(robot, Direction.WEST);
        }
        if (robot.field.field.canSouth && direction != Direction.SOUTH) {
            getNextField(robot.field.row, robot.field.col, Direction.SOUTH).bubbleDownField(robot, Direction.NORTH);
        }
        if (robot.field.field.canWest && direction != Direction.WEST) {
            getNextField(robot.field.row, robot.field.col, Direction.WEST).bubbleDownField(robot, Direction.EAST);
        }
    }

//    private void moveDirection(RobotStats robot, Direction direction, Field curField, boolean requireRobotMove){
//        //Apply robot stats
//        boolean canMove = curField.canMove(direction);
//
//        //Set the number of moves to be used
//        int moves = robot.moves + 1 + (requireRobotMove ? 1 : 0);
//
//        if(canMove){
//            Field nextField = getNextField(curField.row, curField.col, direction); //DONE
//            if(nextField.hasOtherFinals(robot.color)){ //DONE
//                RobotStats stats = nextField.getFinalFromOtherRobots(robot.color); //DONE
//                RobotStats newRobot = new RobotStats(true, moves + stats.moves,robot.color, direction, curField,robot, stats); //DONE
//                if(curField.addRobotStats(newRobot)) {
//                    //Create new robot and update fields
//                    robotQueue.add(newRobot);
//                    updateAdjacentFields(newRobot,direction);
//                }
//
//            }
//
//            moveDirection(robot,direction,nextField,nextField.isStartField);
//
//        } else {
//            //Cannot move - is final
//            RobotStats newRobot = new RobotStats(true, moves, robot.color, direction, curField, robot);
//            if(curField.addRobotStats(newRobot)){
//                robotQueue.add(newRobot);
//                updateAdjacentFields(newRobot, direction);
//            }
//        }
//    }



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

    private void initDataStructures(){
        //Init the robot queue
        robotQueue = new PriorityQueue<RobotStats>(new RobotStatsComparator());
        moveQueue = new LinkedList<MoveStats>();

        wrapper = new DataWrapper(game.fields);
        goalFieldData = wrapper.getFieldData(game.goal.row,game.goal.col);
        directions = new Direction[] {Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};

        //Place robots on startField
        for(Robot robot : game.robots){
            FieldData fieldData = wrapper.getFieldData(robot.startField.row, robot.startField.col);

            RobotStats stats = new RobotStats(true, 0, robot.color, Direction.NONE,fieldData, null);

            fieldData.setStarter(stats);
            robotQueue.add(stats);
        }


    }
}