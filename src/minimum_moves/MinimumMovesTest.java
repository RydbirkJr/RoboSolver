package minimum_moves;

import core.GameBoard;
import game.MapHandler;
import org.junit.Test;
import org.springframework.util.Assert;

import static org.junit.Assert.*;

public class MinimumMovesTest {

    @Test
    public void testMinimumMoves() throws Exception {
        GameBoard board = new MapHandler().setupGameBoard();

        int[][] moves1 = MinimumMoves.minimumMoves(board.fields, board.goals[0]);
        int[][] moves2 = MinimumMoves.minimumMoves(board.fields, board.goals[0]);

        for(int i = 0; i < 16; i++){
            for(int j = 0; j<16; j++){
                Assert.isTrue(moves1[i][j] == moves2[i][j]);
            }
        }


    }
}