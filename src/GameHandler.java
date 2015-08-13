/**
 * Created by Anders on 07/08/15.
 */
public class GameHandler {

    /**
     * Initializing function, setups all the game bounds.
     */
    public void init(){
        System.out.println("Loading map...");
        //Load map

        MapHandler mapHandler = new MapHandler();
        Game game = mapHandler.setupNewGame();



    }

    private void printBoard(Game game){
        for (int row = 0; row < 16; row++){
            for(int col = 0; col < 16; col++){
                Field field = game.gameBoard.fields[row][col];

                String output = "";

                if(!field.canWest) output += "|";
                if(!field.canNorth) output += "^";
                if(field.isGoalField) output += "G:" + field.goalColor.name();
                if(field.robotStats != null) output += "R:" + field.robotStats.entrySet().iterator().next().getValue().first().robot.name();
                output +=field.row + ":" + field.col;
                if(!field.canSouth) output += "_";
                if(!field.canEast) output += "|";

                System.out.print(output + "\t\t<>");
            }
            System.out.println();
        }

        for(Field field : game.robotFields){
            System.out.println(field.robotStats.entrySet().iterator().next().getValue().first().robot.name() + ", " + field.row + ":" + field.col);
        }

    }


}
