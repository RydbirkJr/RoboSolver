import java.util.PriorityQueue;

/**
 * Created by Anders on 07/08/15.
 */
public class RoboSolver {
    private Game game;
    private PriorityQueue<Robot> robotQueue;
    private Goal goal;
    private Field goalField;

    public RoboSolver(Game game, Goal goal){
        this.game = game;
        this.goal = goal;
        robotQueue = new PriorityQueue<Robot>(new RobotComparator());
        this.goalField = game.gameBoard.fields[goal.row][goal.col];

        initGame();
    }

    public GameResult solveGame(){
        //Start popping the robot queue
        while(!robotQueue.isEmpty()){
            Robot robot = robotQueue.poll();
            moveAllDirections(robot);

            if(goalField.isVisited && goalField.hasBeenReached(goal.color)){
                return new GameResult(goalField.getResult());
            }
        }

        return null;
    }

    private void moveAllDirections(Robot robot){
        if(robot.startField.canNorth){
            moveDirection(robot, Direction.NORTH);
        }
        if(robot.startField.canEast){
            moveDirection(robot, Direction.EAST);
        }
        if(robot.startField.canSouth){
            moveDirection(robot, Direction.SOUTH);
        }
        if (robot.startField.canWest){
            moveDirection(robot, Direction.WEST);
        }
    }

    private void moveDirection(Robot robot, Direction direction){
        Field nextField = game.gameBoard.getField(robot.startField, direction);
        moveDirection(robot, direction, nextField, nextField.isStartField);
    }

    private void moveDirection(Robot robot, Direction direction, Field curField, boolean requireRobotMove){
        //Apply robot stats
        boolean canMove = curField.canMove(direction);

        //Set the number of moves to be used
        int moves = robot.moveCount + 1 + (requireRobotMove ? 1 : 0);

        if(canMove){
            Field nextField = game.gameBoard.getField(curField, direction);
            if(nextField.hasOtherFinals(robot.color)){
                RobotStats stats = nextField.getFinalFromOtherRobots(robot.color);
                curField.addRobotStats(new RobotStats(true, moves + stats.moves,robot.color, direction));

                //Create new robot and update fields
                Robot newRobot = new Robot(robot.color,curField,moves + stats.moves);
                robotQueue.add(newRobot);
                updateAdjacentFields(newRobot,direction);
            }

            moveDirection(robot,direction,nextField,nextField.isStartField);

        } else {
            //Cannot move - is final
            curField.addRobotStats(new RobotStats(true, moves, robot.color, direction));
            Robot newRobot = new Robot(robot.color,curField,moves);
            robotQueue.add(newRobot);
            updateAdjacentFields(newRobot, direction);
        }
    }

    private void updateAdjacentFields(Robot robot, Direction direction){

        if(robot.startField.canNorth && direction != Direction.NORTH){
            game.gameBoard.getField(robot.startField,Direction.NORTH).bubbleDownField(robot, Direction.SOUTH);
        }
        if(robot.startField.canEast && direction != Direction.EAST){
            game.gameBoard.getField(robot.startField,Direction.EAST).bubbleDownField(robot, Direction.WEST);
        }
        if(robot.startField.canSouth && direction != Direction.SOUTH){
            game.gameBoard.getField(robot.startField,Direction.SOUTH).bubbleDownField(robot, Direction.NORTH);
        }
        if(robot.startField.canWest && direction != Direction.WEST){
            game.gameBoard.getField(robot.startField,Direction.WEST).bubbleDownField(robot, Direction.EAST);
        }
    }



    private void initGame(){
        //Place robots on startField
        for(Robot robot : game.robots){
            robot.startField.addRobotStats(new RobotStats(true, robot.moveCount, robot.color, Direction.NONE));
            robotQueue.add(robot);
        }
    }
}