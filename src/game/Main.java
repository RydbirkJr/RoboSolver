package game;

import java.util.Scanner;

/**
 * Created by Anders on 05/08/15.
 */
public class Main {
    public static void main(String[] args){

        Scanner scan = new Scanner(System.in);

        int recurrences = 0;
        boolean showIndividualPerformance = false;
        boolean calculateAverages = false;
        try{
            System.out.println("Input the number of game boards to process:");
            recurrences = scan.nextInt();

            System.out.println("Input if the program should display individual solver's performance (1) or only the round time (0):");
            int temp = scan.nextInt();

            switch (temp){
                case 0:
                    showIndividualPerformance = false;
                    break;
                case 1:
                    showIndividualPerformance = true;
                    break;
                default: throw new Exception();
            }

            System.out.println("Input if the program should calculate averages on top of the generated data (1/0):");
            temp = scan.nextInt();

            switch (temp){
                case 0:
                    calculateAverages = false;
                    break;
                case 1:
                    calculateAverages = true;
                    break;
                default: throw new Exception();
            }

        }catch (Exception e){
            System.out.println("Invalid input.");
            System.exit(0);
        }

        GameHandler game = new GameHandler();
        game.processGame(recurrences,showIndividualPerformance);


        if(calculateAverages){
            String dir = game.getDirective();

            System.out.println("The data will be available in the directive " + dir);

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


}
