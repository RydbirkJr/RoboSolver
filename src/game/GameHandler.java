package game;

import basic.BasicSolver;
import core.*;
import graph.GraphSolver;
import org.springframework.util.StopWatch;
import robo.RoboSolver;

/**
 * Created by Anders on 07/08/15.
 */
public class GameHandler {

    private GameBoard gameBoard;
    private MapHandler mapHandler;
    private Robot[] robots;

    public GameHandler(){
        //Setup the map
        mapHandler = new MapHandler();
        gameBoard = mapHandler.setupGameBoard();
        robots = mapHandler.getRobotPositions(gameBoard);
    }

    /**
     * Initializing function, setups all the game bounds.
     */
    public void init(){

        printRobots();

        //Start monitoring stop watch
        StopWatch watch = new StopWatch();

        //For each goal, loop through solution
        for(Goal goal : gameBoard.goals){

            String goalString = "Goal: " + formatOutput(goal.color, goal.row, goal.col);
            System.out.println(goalString);
            Game game = new Game(gameBoard.fields,robots,goal);

            {
                watch.start(goalString);
                runGame(game, new RoboSolver(), "Robo");
                watch.stop();

            }

            {
                watch.start(goalString);
                runGame(game, new BasicSolver(), "Basic");
                watch.stop();
            }
            try {
                watch.start(goalString);
                runGame(game, new GraphSolver(), "Graph");
                watch.stop();
            } catch (Exception e) {
                //e.printStackTrace();
                watch.stop();
            }

            System.out.println("----");


        }
        System.out.println(watch.prettyPrint());
        System.out.println("Terminated");
    }

    private void runGame(Game game, IGameSolver solver, String prefix){
        GameResult result = solver.solveGame(game);

        System.out.println("Result: "+ prefix + " " + result.moveCount);
        printResult(result);
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

    private void printRobots(){
        //Print robot positions
        for(Robot robot : robots){
            String output = formatOutput(robot.color, robot.startField.row, robot.startField.col);
            System.out.println(output);
        }
        System.out.println();
    }
}
