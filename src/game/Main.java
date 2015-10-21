package game;

/**
 * Created by Anders on 05/08/15.
 */
public class Main {
    public static void main(String[] args){

        GameHandler game = new GameHandler();
        game.processGame(10000);

        String dir = game.getDirective();

        DataHandler data = new DataHandler(dir);

        System.out.println("Generate Distribution Data");
        data.generateDistributionData();
        System.out.println("Generate Completion Data");
        data.generateCompletionData();
        System.out.println("Generate Average Time By Solution");
        data.generateAverageTimeDataBySolution();
        System.out.println("Generate Average Time By Optimal");
        data.generateAverageTimeDataByOptimal();
        System.out.println("Done");
    }


}
