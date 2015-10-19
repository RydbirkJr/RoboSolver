package game;

/**
 * Created by Anders on 16/10/15.
 */
public class CsvData {
    public String gameID;
    public int moves;
    public int optimalMoves;
    public long time;
    public boolean isOptimal;
    public boolean isValid;

    public CsvData(String gameID, int moves, long time, int optimalMoves, boolean isOptimal, boolean isValid){
        this.gameID = gameID;
        this.moves = moves;
        this.time = time;
        this.optimalMoves = optimalMoves;
        this.isOptimal = isOptimal;
        this.isValid = isValid;
    }

    public int getMoves(){
        return moves;
    }
}
