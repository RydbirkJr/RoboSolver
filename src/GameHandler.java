import org.springframework.util.StopWatch;

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

        for(Robot robot : game.robots){
            String output = formatOutput(robot.color, robot.startField.row, robot.startField.col);
            System.out.println(output);
        }
        System.out.println();

        StopWatch watch = new StopWatch();
        for(Goal goal : game.gameBoard.goals){

            watch.start("Goal: " + formatOutput(goal.color, goal.row, goal.col));
            RoboSolver solver = new RoboSolver(game, goal);
            GameResult result = solver.solveGame();
            watch.stop();
            System.out.println("Result: " + result.moves);
        }
        System.out.println(watch.prettyPrint());
        System.out.println("Terminated");
    }

    private String formatOutput(Color color, int row, int col){
        String formatColor = (color == Color.RED || color == Color.BLUE) ? "\t" : "" ;
        return color.name() +formatColor + "\t" + (row + 1) + "\t" + (col + 1);
    }

    private void printBoard(Game game){
        for (int row = 0; row < 16; row++){
            for(int col = 0; col < 16; col++){
                Field field = game.gameBoard.fields[row][col];

                String output = "";

                if(!field.canWest) output += "|";
                if(!field.canNorth) output += "^";
                if(field.isGoalField) output += "G:" + field.goalColor.name();
                //if(field.robotStats != null) output += "R:" + field.robotStats.entrySet().iterator().next().getValue().first().color.name();
                output +=field.row + ":" + field.col;
                if(!field.canSouth) output += "_";
                if(!field.canEast) output += "|";

                System.out.print(output + "\t\t<>");
            }
            System.out.println();
        }

//        for(Field field : game.robots){
//            System.out.println(field.robotStats.entrySet().iterator().next().getValue().first().color.name() + ", " + field.row + ":" + field.col);
//        }

    }


}
