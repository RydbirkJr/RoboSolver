package graph_v6;

import core.*;
import game.MapHandler;
import graph_v3.GraphSolver_v3;
import org.junit.Test;

/**
 * Created by Anders on 28/10/15.
 */
public class GraphSolver_v6Test {

    @Test
    public void testSolveGame() throws Exception {
        MapHandler handler = new MapHandler();
        GameBoard board = handler.setupGameBoard();

        GraphSolver_v6 solver5 = new GraphSolver_v6();
        GraphSolver_v3 solver4 = new GraphSolver_v3();

        Goal goal = null;

        for(Goal g : board.goals){
            if(g.row == 3 && g.col == 6) {
                goal = g;
                break;
            }
        }

        Robot[] robots = new Robot[]
                {
                        new Robot(Color.BLUE, board.fields[5][6]),
                        new Robot(Color.RED, board.fields[14][14]),
                        new Robot(Color.GREEN, board.fields[2][11]),
                        new Robot(Color.YELLOW, board.fields[7][15])
                };

        Game game = new Game(board.fields, robots,goal);

        GameResult res = solver5.solveGame(game);
        System.out.println(res.moveCount);
        printResult(res);

        res = solver4.solveGame(game);
        System.out.println(res.moveCount);
        printResult(res);
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
}