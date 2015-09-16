package core;

import java.util.ArrayList;

/**
 * Created by Anders on 07/08/15.
 */
public class GameBoard {
    public final Field[][] fields;
    public final Goal[] goals;

    public GameBoard(ArrayList<Obstacle> horizontal, ArrayList<Obstacle> vertical, Goal[] goals){
        fields = new Field[16][16];
        this.goals = goals;

        //Init goals - before other fields are initiated
        for(Goal goal : goals){
            fields[goal.row][goal.col] = goal;
        }

        initOuterBoundaries();
        initObstacles(horizontal, vertical);


    }

    private  void initObstacles(ArrayList<Obstacle> horizontal, ArrayList<Obstacle> vertical){
        //Init horizontal obstacles
        for(Obstacle obs : horizontal){
            fields[obs.row][obs.col].canWest = false;
            fields[obs.row][obs.col - 1].canEast = false;
        }

        //Init vertical obstacles
        for(Obstacle obs : vertical){
            fields[obs.row][obs.col].canNorth = false;
            fields[obs.row - 1][obs.col].canSouth = false;
        }
    }

    private  void initOuterBoundaries(){

        for(int row = 0; row < 16; row++){
            for(int col = 0; col < 16; col++){
                //Init temporary startField unless already set
                Field tempField = fields[row][col] == null ? new Field(row, col) : fields[row][col];

                //Set standard limits - outer bounds
                switch (row){
                    case 0: tempField.canNorth = false;
                        break;
                    case 15: tempField.canSouth = false;
                        break;
                }

                switch (col){
                    case 0: tempField.canWest = false;
                        break;
                    case 15: tempField.canEast = false;
                        break;
                }

                fields[row][col] = tempField;
            }

        }
    }


}
