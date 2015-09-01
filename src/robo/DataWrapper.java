package robo;

import Core.Direction;
import Core.Field;
import Core.GameResult;
import Core.WinningMove;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Anders on 27/08/15.
 */
public class DataWrapper {
    private FieldData[][] data;
    private Field[][] fields;

    public DataWrapper(Field[][] fields){

        data = new FieldData[16][16];
        this.fields = fields;
    }

    public FieldData getFieldData(int row, int col){
        FieldData field = data[row][col];

        //Init field data object if null
        if(field == null){
            field = new FieldData(row, col, fields[row][col]);
            data[row][col] = field;
        }

        return field;
    }

    public GameResult formatResult(RobotStats finalStats){
        ArrayList<WinningMove> moves = getMoves(finalStats);
        Collections.reverse(moves);
        return new GameResult(moves,finalStats.moves);
    }

    private ArrayList<WinningMove> getMoves(RobotStats stats) {
        ArrayList<WinningMove> moves = new ArrayList<WinningMove>();

        while (stats.direction != Direction.NONE) {
            //Print: moves, field, direction
            moves.add(new WinningMove(stats.direction, stats.color, stats.field.row, stats.field.col, stats.moves));

            if (stats.dependUpon != null) {
                moves.addAll(getMoves(stats.dependUpon));
            }
            stats = stats.prevRobot;
        }
        return moves;
    }
}
