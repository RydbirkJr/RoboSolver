package basic_iddfs;

import core.*;
import minimum_moves.MinimumMoves;

import java.util.*;

/**
 * Created by Anders on 31/08/15.
 */
public class IddfsSolver implements IGameSolver {

    private int nodes = 0, hits = 0, inners = 0;

    public GameResult solveGame(Game game){
        Direction[] directions = new Direction[] {Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};
        HashMap<String, Integer> states = new HashMap<String, Integer>();
        boolean[][] hasRobot = new boolean[16][16];

        RobotState[] robots = putGoalRobotFirst(game.robots, game.goal.color);

        boolean found = false;
        GameState state = new GameState(robots, null, 0);
        int i = 0;

        int[][] minMoves = MinimumMoves.minimumMoves(game.fields, game.goal);

        for(RobotState robot : robots){
            hasRobot[robot.row][robot.col] = true;
        }

        while(!found && !Thread.currentThread().isInterrupted()){
            i++;

            GameState result = search(state, game.fields, directions, states, hasRobot,game.goal, i, 0, minMoves);

            //System.out.println("Nodes: " + nodes + " Hits: " + hits + " inners: " + inners);

            if(result != null){
                states = null;
                return new GameResult(null, result.moves);
            }
        }
        return null;
    }

    private GameState search(GameState state,
                             Field[][] fields,
                             Direction[] directions,
                             HashMap<String, Integer> states,
                             boolean[][] hasRobot,
                             Goal goal,
                             int maxDepth,
                             int depth,
                             int[][] minMoves
    ){


        nodes++;
        int height = maxDepth - depth;
        RobotState goalRobot = state.robots.get(goal.color);

        if(goalRobot.row == goal.row && goalRobot.col == goal.col) {
            return state;
        }

        if(depth == maxDepth) return null;

        if(minMoves[goalRobot.row][goalRobot.col] > height) return null;

        if(height != 1 && !addKey(states, state.toString(), height)){
            hits++;
            return null;
        }

        inners++;
        for(RobotState robot : state.robots.values()){
            if(robot.color != goal.color && minMoves[goalRobot.row][goalRobot.col] == height) continue;

            for(Direction d : directions){
                GameState newState = moveDirection(d, state, robot, fields, hasRobot);

                if(newState != null){
                    RobotState moved = newState.robots.get(robot.color);
                    hasRobot[robot.row][robot.col] = false;
                    hasRobot[moved.row][moved.col] = true;

                    newState = search(newState, fields, directions, states, hasRobot, goal, maxDepth, depth +1, minMoves);

                    hasRobot[robot.row][robot.col] = true;
                    hasRobot[moved.row][moved.col] = false;

                    if(newState != null){
                        return newState;
                    }
                }
            }
        }
        return null;
    }

    private boolean addKey(HashMap<String, Integer> states, String key, int value){
        if(states.containsKey(key)){
            int old = states.get(key);
            if(old < value){
                states.put(key, value);
                return true;
            }
            return false;

        } else {
            states.put(key, value);
            return true;
        }
    }

    private GameState moveDirection(Direction direction, GameState oldState, RobotState robotState, Field[][] fields, boolean[][] hasRobot){
        int row = robotState.row;
        int col = robotState.col;

        Field field;
        Field nextField = fields[row][col];

        do{
            field = nextField;

            //Must be applied before assigning next field in case it's off the board. Throws exception
            if (!field.canMove(direction)) {
                break;
            }

            switch (direction){
                case NORTH:
                    row--;
                    break;
                case EAST:
                    col++;
                    break;
                case SOUTH:
                    row++;
                    break;
                case WEST:
                    col--;
                    break;
            }

            //Set next for looping
            nextField = fields[row][col];

        } while(!hasRobot[row][col]);

        if(field.row == robotState.row && field.col == robotState.col) return null;

        GameState result = new GameState(oldState, new RobotState(robotState.color, field.row, field.col));

        return result;
    }

    private RobotState[] putGoalRobotFirst(Robot[] list, Color goalColor){
        RobotState[] robots = new RobotState[4];

        int i = 1;
        for(Robot robot : list){
            if(robot.color == goalColor) {
                robots[0] = new RobotState(robot.color, robot.startField.row, robot.startField.col);
            } else {
                robots[i] = new RobotState(robot.color, robot.startField.row, robot.startField.col);
                i++;
            }
        }
        return  robots;
    }
}
