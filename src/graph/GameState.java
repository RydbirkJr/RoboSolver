package graph;

import core.Color;

import java.util.HashMap;

/**
 * Created by Anders on 18/09/15.
 */
public class GameState {

    public int moves;
    public GameState prev;
    public HashMap<Color, RobotState> states;
    public Color colorChanged;

    public GameState(RobotState[] states){
        moves = 0;
        this.prev = null;
        this.states = new HashMap<>();
        for (RobotState state : states){
            this.states.put(state.color, state);
        }
    }

    public GameState(GameState prev, RobotState newState){
        this.prev = prev;
        this.states = (HashMap<Color, RobotState>) prev.states.clone();
        this.states.put(newState.color, newState);
        this.moves = prev.moves + 1;
        this.colorChanged = newState.color;
    }

    @Override
    public String toString() {
        return getString(Color.BLUE) + ";" + getString(Color.GREEN) + ";" + getString(Color.RED) + ";" + getString(Color.YELLOW);
    }

    private String getString(Color color){
        return this.states.containsKey(color) ? this.states.get(color).toString() : "";
    }
}
