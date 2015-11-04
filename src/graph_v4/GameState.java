package graph_v4;

import core.Color;

import java.util.HashMap;

/**
 * Created by Anders on 18/09/15.
 */
public class GameState {

    public int moves;
    public int goalMoves;
    public GameState prev;
    public HashMap<Color, RobotState> states;
    public Color colorChanged;
    public RobotState searchState;

    public GameState(RobotState[] states, RobotState searchState){
        moves = 0;
        goalMoves = 0;
        this.prev = null;
        this.states = new HashMap<>();
        for (RobotState state : states){
            this.states.put(state.color, state);
        }

        this.searchState = searchState;
    }

    public GameState(GameState prev, RobotState newState){
        this(prev, newState, null, false);
    }

    public GameState(GameState prev, RobotState newState, RobotState newSearchState, boolean useNewStateAsSearchState){
        this.prev = prev;
        this.states = (HashMap<Color, RobotState>) prev.states.clone();
        this.states.put(newState.color, newState);
        this.moves = prev.moves + 1;
        this.searchState = newSearchState != null ? newSearchState : prev.searchState;
        if(useNewStateAsSearchState) this.searchState = newState;
        if(newState.color == this.searchState.color) goalMoves++;
        this.colorChanged = newState.color;
    }

    @Override
    public String toString() {
        return getString(Color.BLUE) + ";" + getString(Color.GREEN) + ";" + getString(Color.RED) + ";" + getString(Color.YELLOW);
    }

    public String toSearchString() {
        return getSearchString(Color.BLUE) + ";" + getSearchString(Color.GREEN) + ";" + getSearchString(Color.RED) + ";" + getSearchString(Color.YELLOW);
    }

    private String getString(Color color){
        return this.states.containsKey(color) ? this.states.get(color).toString() : "";
    }

    private String getSearchString(Color color){
        return this.searchState.color == color ? searchState.toString() : this.states.get(color).toString();
    }
}
