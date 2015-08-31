package GameHandler;

import Core.*;
import RoboSolver.RoboSolverHandler;
import org.springframework.util.StopWatch;

/**
 * Created by Anders on 07/08/15.
 */
public class GameHandler {

    /**
     * Initializing function, setups all the game bounds.
     */
    public void init(){
        //Setup the map
        MapHandler mapHandler = new MapHandler();
        GameBoard gameBoard = mapHandler.setupGameBoard();

        //Get robot positions
        Robot[] robots = mapHandler.getRobotPositions(gameBoard);

        //Print robot positions
        for(Robot robot : robots){
            String output = formatOutput(robot.color, robot.startField.row, robot.startField.col);
            System.out.println(output);
        }
        System.out.println();

        //For each goal, loop through solution
        StopWatch watch = new StopWatch();
        for(Goal goal : gameBoard.goals){
            String goalString = "Core.Goal: " + formatOutput(goal.color, goal.row, goal.col);
            watch.start(goalString);
            RoboSolverHandler solver = new RoboSolverHandler();
            GameResult roboResult = solver.solveGame(new Game(gameBoard.fields,robots,goal));
            watch.stop();
            System.out.println("Result: " + goalString);
            printResult(roboResult);
        }
        System.out.println(watch.prettyPrint());
        System.out.println("Terminated");
    }

    private String formatOutput(Color color, int row, int col){
        String formatColor = (color == Color.RED) ? "\t" : "" ;
        return color.name() +formatColor + "\t" + (row + 1) + "\t" + (col + 1);
    }

    private void printResult(GameResult result){
        for(WinningMove move : result.moves){
            String output = move.color.name().substring(0,1) + "\t";
            output += move.moveCount + "\t";
            output += "(" + (move.row + 1) + ":" + (move.col + 1) + ")\t";
            output += move.direction.name().substring(0,1);
            System.out.println(output);
        }
    }

//    private void printBoard(Game game){
//        for (int row = 0; row < 16; row++){
//            for(int col = 0; col < 16; col++){
//                Field field = game.gameBoard.fields[row][col];
//
//                String output = "";
//
//                if(!field.canWest) output += "|";
//                if(!field.canNorth) output += "^";
//                if(field.isGoalField) output += "G:" + field.goalColor.name();
//                //if(field.robotStats != null) output += "R:" + field.robotStats.entrySet().iterator().next().getValue().first().color.name();
//                output +=field.row + ":" + field.col;
//                if(!field.canSouth) output += "_";
//                if(!field.canEast) output += "|";
//
//                System.out.print(output + "\t\t<>");
//            }
//            System.out.println();
//        }
//
////        for(Core.Field field : game.robots){
////            System.out.println(field.robotStats.entrySet().iterator().next().getValue().first().color.name() + ", " + field.row + ":" + field.col);
////        }
//
//    }


}
