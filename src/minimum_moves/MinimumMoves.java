package minimum_moves;

import core.Direction;
import core.Field;
import core.GameBoard;
import core.Goal;
import game.MapHandler;
import org.springframework.util.StopWatch;

import java.util.*;

/**
 * Created by Anders on 30/09/15.
 */
public class MinimumMoves {

    public static int[][] minimumMoves(Field[][] fields, Goal goal){
        Direction[] directions = new Direction[] {Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST};

        int[][] minMoves = new int[16][16];

        for(int i = 0; i < 16; i++){
            for(int j = 0; j < 16; j++){
                minMoves[i][j] = Integer.MAX_VALUE;
            }
        }

        Queue<Field> queue = new LinkedList<Field>();

        queue.add(fields[goal.row][goal.col]);
        minMoves[goal.row][goal.col] = 0;

        while (! queue.isEmpty()){
            Field f = queue.poll();
            int depth = minMoves[f.row][f.col] + 1;
            for(Direction d : directions){

                Field temp = f;

                while (temp.canMove(d)){
                    temp = getField(fields, temp.row, temp.col, d);

                    // if minMoves < depth then break
                    if( !(minMoves[temp.row][temp.col] >= depth)) break;

                    minMoves[temp.row][temp.col] = depth;
                    queue.add(temp);
                }

            }
        }

        minMoves[7][7] = 0;
        minMoves[7][8] = 0;
        minMoves[8][7] = 0;
        minMoves[8][8] = 0;

        return minMoves;
    }

    private static Field getField(Field[][] fields, int row, int col, Direction d){
        switch (d){
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

        return fields[row][col];
    }
}
