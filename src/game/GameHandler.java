package game;

import basic.BasicSolver;
import basic_iddfs.IddfsSolver;
import core.*;
import graph.GraphSolver;
import graph_v2.GraphSolver_v2;
import org.springframework.util.StopWatch;
import robo.RoboSolver;

import java.io.FileWriter;
import java.util.concurrent.*;

/**
 * Created by Anders on 07/08/15.
 */
public class GameHandler {

    private GameBoard _gameBoard;
    private MapHandler _mapHandler;
    private int _minRounds = 0;
    private boolean test = false;

    public GameHandler(){
        //Setup the map
        _mapHandler = new MapHandler();
        _gameBoard = _mapHandler.setupGameBoard();
    }

    /**
     * Initializing function, setups all the game bounds.
     */
    public void init(){

        //printRobots();

        //Start monitoring stop watch


        for(int i = 0; i < 100; i++){
            StopWatch roundWatch = new StopWatch();
            roundWatch.start();
            Robot[] robots = _mapHandler.getRobotPositions(_gameBoard);

                //Generate a robot string representing the game
                String robotPositions = concatRobots(robots);
                //For each goal, loop through solution
                for(Goal goal : _gameBoard.goals){

                    String gameID = robotPositions + "GOAL="  + goal.color.name().charAt(0) + goal.row + ":" + goal.col;
                    Game game = new Game(_gameBoard.fields,robots,goal);
                    //System.out.println(gameID);
                    {
                        runGame(game, new BasicSolver(), "basic",gameID, true);
                    }
                    {
                        runGame(game, new IddfsSolver(), "iddfs", gameID, false);
                    }
                    {
                        runGame(game, new RoboSolver(), "robo", gameID, false);
                    }
                    {
                        runGame(game, new GraphSolver(), "graph", gameID, false);
                    }
                    {
                        runGame(game, new GraphSolver_v2(), "graph-Optimized", gameID, false);
                    }
                }

                roundWatch.stop();
                System.out.println("Round: " + i + "\tTime: " + roundWatch.getLastTaskTimeMillis() + " ms");
        }
        System.out.println("Terminated");

    }

    private void runGame(Game game, IGameSolver solver, String prefix, String gameID, boolean isBasic){

        ExecutorService executor = Executors.newSingleThreadExecutor();
        try{
            SolverWrapper wrapper = new SolverWrapper(solver, game, prefix, gameID);
            executor.submit(wrapper).get(40, TimeUnit.SECONDS);


            GameResult res = wrapper.getResult();
            long timeSpend = wrapper.getTime();

            if(isBasic){
                _minRounds = res.moveCount;
            }

            saveToFile(prefix, gameID, res.moveCount, timeSpend);
            //System.out.println(prefix + "\nTime: " + timeSpend + "ms\tMoves: " + res.moveCount);

        }catch(TimeoutException e){
            System.out.println("Timeout for " + prefix);
            executor.shutdownNow();
        } catch(InterruptedException e){
            System.out.println("Interrupted for " + prefix);
            executor.shutdownNow();
        } catch (ExecutionException e){
            System.out.println("Execution error for " + prefix);
            executor.shutdownNow();
        } catch(NullPointerException e){
            System.out.printf("Nullpointer returned for " + prefix);

        }
    }

    private void saveToFile(String prefix, String gameID, int moves, long time){

        FileWriter out;
        try{
            out =  new FileWriter(prefix + ".csv",true);
            out.append(gameID + ";");
            out.append(moves + ";");
            out.append((_minRounds == moves ? "1" : "0") + ";");
            out.append(time + "\n");
            out.close();
        }catch(Exception e){
            e.printStackTrace();
        }
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

    private String concatRobots(Robot[] robots){
        String result = "";
        for(int i = 0; i < robots.length; i++){
            Robot r = robots[i];
            result += r.color.name().substring(0, 1) + r.startField.row + ":" + r.startField.col;
            if(i != robots.length){
                result += ";";
            }
        }
        return result;
    }
}
