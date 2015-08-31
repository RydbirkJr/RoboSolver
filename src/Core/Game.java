package Core;

/**
 * Created by Anders on 07/08/15.
 */
public class Game {
    public final Robot[] robots;
    public final Field[][] fields;
    public final Goal goal;

    public Game(Field[][] fields, Robot[] robots, Goal goal){
        this.robots = robots;
        this.fields = fields;
        this.goal = goal;
    }
}
