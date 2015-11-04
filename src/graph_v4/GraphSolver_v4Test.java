package graph_v4;

import core.*;
import game.MapHandler;
import org.junit.Test;
import stateless.StatelessSolver;

/**
 * Created by Anders on 28/10/15.
 */
public class GraphSolver_v4Test {

    @Test
    public void testSolveGame() throws Exception {
        MapHandler handler = new MapHandler();
        GameBoard board = handler.setupGameBoard();

        GraphSolver_v4 solver4 = new GraphSolver_v4();
        StatelessSolver solver = new StatelessSolver();

        Goal goal = null;

        for(Goal g : board.goals){
            if(g.row == 6 && g.col == 1) {
                goal = g;
                break;
            }
        }

        Robot[] robots = new Robot[]
                {
                        new Robot(Color.BLUE, board.fields[13][14]),
                        new Robot(Color.RED, board.fields[2][11]),
                        new Robot(Color.GREEN, board.fields[12][0]),
                        new Robot(Color.YELLOW, board.fields[13][2])
                };

        Game game = new Game(board.fields, robots,goal);

        GameResult res = solver4.solveGame(game);
        System.out.println(res.moveCount);
        printResult(res);

        res = solver.solveGame(game);
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