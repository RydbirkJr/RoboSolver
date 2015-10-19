package game;

import basic.BasicSolver;
import basic_iddfs.IddfsSolver;
import core.*;
import graph.GraphSolver;
import graph_v2.GraphSolver_v2;
import org.springframework.util.StopWatch;
import robo.StatelessSolver;

import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.*;

/**
 * Created by Anders on 07/08/15.
 */
public class GameHandler {

    private GameBoard _gameBoard;
    private MapHandler _mapHandler;
    private int _minRounds = 0;
    private String _directive;

    public GameHandler(){
        //Setup the map
        _mapHandler = new MapHandler();
        _gameBoard = _mapHandler.setupGameBoard();
    }

    /**
     * Initializing function, setups all the game bounds.
     */
    public void processGame(int iterations){

        //printRobots();

        _directive = createNewDataFolder();


        for(int i = 0; i < iterations; i++){
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
                    runGame(game, new IddfsSolver(), Solver.IDDFS, gameID, true);
                    runGame(game, new BasicSolver(), Solver.NAIVE,gameID, false);
                    runGame(game, new StatelessSolver(), Solver.STATELESS, gameID, false);
                    runGame(game, new GraphSolver(), Solver.GRAPHv1, gameID, false);
                    runGame(game, new GraphSolver_v2(), Solver.GRAPHv2, gameID, false);
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

            saveToFile(prefix, gameID, res.moveCount, timeSpend, (isBasic || _minRounds <= res.moveCount));
            //System.out.println(prefix + "\nTime: " + timeSpend + "ms\tMoves: " + res.moveCount);
            return;


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
            System.out.printf("Null pointer returned for " + prefix);
        }
        saveToFile(prefix, gameID, 0, 0, false);
    }

    private void saveToFile(String prefix, String gameID, int moves, long time, boolean isValid){

        FileWriter out;
        try{
            out =  new FileWriter(_directive + "/" + prefix + ".csv",true);
            out.append(gameID + ";"); //ID
            out.append(moves + ";"); //# moves for solution
            out.append(time + ";"); //Time spend
            out.append(_minRounds + ";"); //# moves optimal
            out.append((_minRounds == moves ? "true" : "false") + ";"); //Is optimal
            out.append((isValid ? "true" : "false") + "\n"); //Is valid
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

    private String createNewDataFolder(){
        Date date = new Date();
        String folderName = new SimpleDateFormat("yyyy-MM-dd_HHmmss").format(date);
        File dir = new File(folderName);
        if(!dir.exists()){
            dir.mkdir();
        }
        return folderName;
    }

    public String getDirective(){
        return _directive;
    }
}
