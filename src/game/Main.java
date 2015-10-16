package game;

/**
 * Created by Anders on 05/08/15.
 */
public class Main {
    public static void main(String[] args){

//        GameHandler game = new GameHandler();
//        game.processGame(1000);

        String dir = "2015-10-16_131704";

        DataHandler data = new DataHandler(dir);

        data.generateDistributionData();
        data.generateCompletionData();
        data.generateAverageTimeData();
    }


}
