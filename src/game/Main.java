package game;

/**
 * Created by Anders on 05/08/15.
 */
public class Main {
    public static void main(String[] args){

        GameHandler game = new GameHandler();
        game.processGame(1000);

        String dir = game.getDirective();

        DataHandler data = new DataHandler(dir);

        data.generateDistributionData();
        data.generateCompletionData();
        data.generateAverageTimeDataBySolution();
        data.generateAverageTimeDataByOptimal();
    }   


}
