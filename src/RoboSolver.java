import java.util.PriorityQueue;

/**
 * Created by Anders on 07/08/15.
 */
public class RoboSolver {
    private Game game;
    private PriorityQueue<RobotStats> robotQueue;
    private Goal goal;
    private Field goalField;
    private RobotStats result;

    public RoboSolver(Game game, Goal goal){
        this.game = game;
        this.goal = goal;
        robotQueue = new PriorityQueue<RobotStats>(new RobotStatsComparator());
        this.goalField = game.gameBoard.fields[goal.row][goal.col];

        initGame();
    }

    public RobotStats solveGame(){
        //Start popping the robot queue
        while(!robotQueue.isEmpty()){
            RobotStats robot = robotQueue.poll();
            moveAllDirections(robot);


            if(goalField.isVisited && goalField.hasBeenReached(goal.color)){
                result = goalField.getResult();

                //If the result is above the current level, continue
                if(result.moves <= (robot.moves +1)){
                    return result;
                }
            }
        }

        return null;
    }

    private void moveAllDirections(RobotStats robot){
        if(robot.field.canNorth){
            moveDirection(robot, Direction.NORTH);
        }
        if(robot.field.canEast){
            moveDirection(robot, Direction.EAST);
        }
        if(robot.field.canSouth){
            moveDirection(robot, Direction.SOUTH);
        }
        if (robot.field.canWest){
            moveDirection(robot, Direction.WEST);
        }
    }

    private void moveDirection(RobotStats robot, Direction direction){
        Field nextField = game.gameBoard.getField(robot.field, direction);
        moveDirection(robot, direction, nextField, nextField.isStartField);
    }

    private void moveDirection(RobotStats robot, Direction direction, Field curField, boolean requireRobotMove){
        //Apply robot stats
        boolean canMove = curField.canMove(direction);

        //Set the number of moves to be used
        int moves = robot.moves + 1 + (requireRobotMove ? 1 : 0);

        if(canMove){
            Field nextField = game.gameBoard.getField(curField, direction);
            if(nextField.hasOtherFinals(robot.color)){
                RobotStats stats = nextField.getFinalFromOtherRobots(robot.color);
                RobotStats newRobot = new RobotStats(true, moves + stats.moves,robot.color, direction, curField,robot, stats);
                if(curField.addRobotStats(newRobot)) {
                    //Create new robot and update fields
                    robotQueue.add(newRobot);
                    updateAdjacentFields(newRobot,direction);
                }

            }

            moveDirection(robot,direction,nextField,nextField.isStartField);

        } else {
            //Cannot move - is final
            RobotStats newRobot = new RobotStats(true, moves, robot.color, direction, curField, robot);
            if(curField.addRobotStats(newRobot)){
                robotQueue.add(newRobot);
                updateAdjacentFields(newRobot, direction);
            }
        }
    }

    private void updateAdjacentFields(RobotStats robot, Direction direction){
        if(robot.field.canNorth && direction != Direction.NORTH){
            game.gameBoard.getField(robot.field,Direction.NORTH).bubbleDownField(robot, Direction.SOUTH);
        }
        if(robot.field.canEast && direction != Direction.EAST){
            game.gameBoard.getField(robot.field,Direction.EAST).bubbleDownField(robot, Direction.WEST);
        }
        if(robot.field.canSouth && direction != Direction.SOUTH){
            game.gameBoard.getField(robot.field,Direction.SOUTH).bubbleDownField(robot, Direction.NORTH);
        }
        if(robot.field.canWest && direction != Direction.WEST){
            game.gameBoard.getField(robot.field,Direction.WEST).bubbleDownField(robot, Direction.EAST);
        }
    }



    private void initGame(){
        //Place robots on startField
        for(Robot robot : game.robots){
            RobotStats newRobot = new RobotStats(true, robot.moveCount, robot.color, Direction.NONE, robot.startField, null);
            robot.startField.addRobotStats(newRobot);
            robotQueue.add(newRobot);
        }
    }
}