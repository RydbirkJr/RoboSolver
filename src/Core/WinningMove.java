package Core;

/**
 * Created by Anders on 31/08/15.
 */
public class WinningMove {
    public Direction direction;
    public Color color;
    public int row;
    public int col;
    public int moveCount;

    public WinningMove(Direction direction, Color color, int row, int col, int moveCount){
        this.direction = direction;
        this.color = color;
        this.row = row;
        this.col = col;
        this.moveCount = moveCount;
    }
}
