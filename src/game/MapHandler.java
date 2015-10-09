package game;

import core.*;

import java.util.*;

/**
 * Created by Anders on 07/08/15.
 */
public class MapHandler {
    public GameBoard setupGameBoard(){
        return new GameBoard(
                getHorizontalObstacles(),
                getVerticalObstacles(),
                getGoals()
        );
    }

    public Robot[] getRobotPositions(GameBoard gameBoard){
        //To ensure no robots is placed on the same startField
        HashSet<Field> robotFields = new HashSet<Field>();

        //To hold the robots with the startField positions
        ArrayList<Robot> robotList = new ArrayList<Robot>();

        int min = 0;
        int max = 15;

        for(int i = 0; i < 4; i++){

            Field field;

            do{
                int row = getRandom(min, max);
                int col = getRandom(min, max);

                field = gameBoard.fields[row][col];

                //Runs until a legal startField is found
            }while(!validatePosition(robotFields, field));

            //Convert i into the Core.Color enum
            Color color = Color.values()[i];

            Robot robot = new Robot(color, field);
            robotList.add(robot);
        }

        return robotList.toArray(new Robot[robotList.size()]);
    }

    public boolean validatePosition(HashSet<Field> states, Field field){
        if( (field.row == 7 || field.row == 8) && (field.col ==7 || field.col == 8) ) return false;

        return !(states.contains(field) || field instanceof Goal);
    }

    private int getRandom(int min, int max){
        return min + (int)(Math.random() * ((max - min) + 1));
    }


    private ArrayList<Obstacle> getHorizontalObstacles(){
        ArrayList<Obstacle> horizontal = new ArrayList<Obstacle>();
        horizontal.add(new Obstacle(0,5));
        horizontal.add(new Obstacle(0,10));
        horizontal.add(new Obstacle(1,2));
        horizontal.add(new Obstacle(1,12));
        horizontal.add(new Obstacle(3,6));
        horizontal.add(new Obstacle(3,9));
        horizontal.add(new Obstacle(4,15));
        horizontal.add(new Obstacle(5,5));
        horizontal.add(new Obstacle(5,11));
        horizontal.add(new Obstacle(6,2));
        horizontal.add(new Obstacle(7,7));
        horizontal.add(new Obstacle(7,9));
        horizontal.add(new Obstacle(8,7));
        horizontal.add(new Obstacle(8,9));
        horizontal.add(new Obstacle(8,12));
        horizontal.add(new Obstacle(9,4));
        horizontal.add(new Obstacle(9,10));
        horizontal.add(new Obstacle(11,1));
        horizontal.add(new Obstacle(11,10));
        horizontal.add(new Obstacle(12,7));
        horizontal.add(new Obstacle(12,15));
        horizontal.add(new Obstacle(14,2));
        horizontal.add(new Obstacle(14,13));
        horizontal.add(new Obstacle(15,6));
        horizontal.add(new Obstacle(15,11));

        return horizontal;
    }

    private ArrayList<Obstacle> getVerticalObstacles(){
        ArrayList<Obstacle> vertical = new ArrayList<Obstacle>();
        vertical.add(new Obstacle(1,2));
        vertical.add(new Obstacle(1,12));
        vertical.add(new Obstacle(2,15));
        vertical.add(new Obstacle(4,6));
        vertical.add(new Obstacle(4,9));
        vertical.add(new Obstacle(5,0));
        vertical.add(new Obstacle(5,4));
        vertical.add(new Obstacle(5,10));
        vertical.add(new Obstacle(5,14));
        vertical.add(new Obstacle(7,1));
        vertical.add(new Obstacle(7,7));
        vertical.add(new Obstacle(7,8));
        vertical.add(new Obstacle(8,12));
        vertical.add(new Obstacle(9,3));
        vertical.add(new Obstacle(9,7));
        vertical.add(new Obstacle(9,8));
        vertical.add(new Obstacle(10,10));
        vertical.add(new Obstacle(11,15));
        vertical.add(new Obstacle(12,1));
        vertical.add(new Obstacle(12,9));
        vertical.add(new Obstacle(12,14));
        vertical.add(new Obstacle(13,6));
        vertical.add(new Obstacle(14,0));
        vertical.add(new Obstacle(14,2));
        vertical.add(new Obstacle(14,13));

        return vertical;
    }

    private Goal[] getGoals(){
        ArrayList<Goal> goals = new ArrayList<Goal>();
        goals.add(new Goal(1,2,Color.YELLOW));
        goals.add(new Goal(1,12,Color.RED));
        goals.add(new Goal(3,6,Color.BLUE));
        goals.add(new Goal(3,9,Color.BLUE));
        goals.add(new Goal(4,14,Color.GREEN));
        goals.add(new Goal(5,4,Color.RED));
        goals.add(new Goal(5,10,Color.YELLOW));
        goals.add(new Goal(6,1,Color.GREEN));
        goals.add(new Goal(9,3,Color.YELLOW));
        goals.add(new Goal(9,10,Color.BLUE));
        goals.add(new Goal(11,1,Color.RED));
        goals.add(new Goal(11,9, Color.YELLOW));
        goals.add(new Goal(12,6,Color.BLUE));
        goals.add(new Goal(12,14,Color.GREEN));
        goals.add(new Goal(14,2,Color.GREEN));
        goals.add(new Goal(14,13,Color.RED));

        return goals.toArray(new Goal[goals.size()]);
    }
}
