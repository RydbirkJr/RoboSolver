package game;

import basic.BasicSolver;
import basic_iddfs.IddfsSolver;
import core.*;
import graph.GraphSolver;
import graph_v2.GraphSolver_v2;
import javafx.scene.paint.Stop;
import org.springframework.util.StopWatch;
import robo.RoboSolver;

import java.io.FileWriter;
import java.io.PrintWriter;

/**
 * Created by Anders on 07/08/15.
 */
public class GameHandler {

    private GameBoard gameBoard;
    private MapHandler mapHandler;
    private Robot[] robots;
    private int roundMax = 0;

    public GameHandler(){
        //Setup the map
        mapHandler = new MapHandler();
        gameBoard = mapHandler.setupGameBoard();
    }

    /**
     * Initializing function, setups all the game bounds.
     */
    public void init(){

        //printRobots();

        //Start monitoring stop watch
        StopWatch watch = new StopWatch();
        StopWatch roundWatch = new StopWatch();

        for(int i = 0; i < 100; i++){
            roundWatch.start();
            robots = mapHandler.getRobotPositions(gameBoard);
            //For each goal, loop through solution

            //printRobots(robots);
            for(Goal goal : gameBoard.goals){

                String goalString = "Goal: " + formatOutput(goal.color, goal.row, goal.col);
                //System.out.println(goalString);
                Game game = new Game(gameBoard.fields,robots,goal);

                runGame(game, new BasicSolver(), "basic", watch,goalString, true);
                runGame(game, new RoboSolver(), "robo",watch,goalString, false);
                runGame(game, new GraphSolver(), "graph",watch,goalString, false);
                runGame(game, new GraphSolver_v2(), "graph-Optimized",watch,goalString, false);
                runGame(game, new IddfsSolver(), "iddfs",watch,goalString, false);
                //System.out.println("----");
                }

            roundWatch.stop();
            System.out.println("Round: " + i + "\tTime: " + roundWatch.getLastTaskTimeMillis() + " ms");

            //System.out.println(watch.prettyPrint());


            }
        System.out.println("Terminated");

        }

    private void runGame(Game game, IGameSolver solver, String prefix, StopWatch watch, String goalString, boolean isBasic){

        GameResult result = null;
        try{
            watch.start(goalString + " " + prefix);
            result = solver.solveGame(game);
            if(isBasic){
                roundMax = result.moveCount;
            }
            //System.out.println("Result: "+ prefix + " " + result.moveCount);
            watch.stop();
        } catch (Exception e){
            watch.stop();
        }

        FileWriter out = null;
        try{
            out =  new FileWriter(prefix + ".csv",true);
            out.append(result.moveCount + ";");
            out.append((roundMax == result.moveCount ? "1" : "0") + ";");
            out.append(watch.getLastTaskTimeMillis() + "\n");
            out.close();
        }catch(Exception e){
            e.printStackTrace();
        }

        //printResult(result);
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

    private void printRobots(Robot[] robots){
        //Print robot positions
        for(Robot robot : robots){
            String output = formatOutput(robot.color, robot.startField.row, robot.startField.col);
            System.out.println(output);
        }
        System.out.println();
    }
}
