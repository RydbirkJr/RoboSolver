package game;

import core.Game;
import core.GameResult;
import core.IGameSolver;
import org.springframework.util.StopWatch;

/**
 * Created by Anders on 08/10/15.
 */
public class SolverWrapper implements Runnable {

    private IGameSolver _solver;
    private GameResult _result;
    private String _prefix, _gameID;
    private Game _game;
    private long _time;

    public SolverWrapper(IGameSolver solver, Game game, String prefix, String gameID){
        _solver = solver;
        _game = game;
        _prefix = prefix;
        _gameID = gameID;
    }

    @Override
    public void run() {
        Thread.currentThread().setName(_prefix + "_" + _gameID);

        StopWatch watch = new StopWatch();
        watch.start();
        _result = _solver.solveGame(_game);
        watch.stop();
        _time = watch.getLastTaskTimeMillis();
    }

    public GameResult getResult(){
        return _result;
    }

    public long getTime(){
        return _time;
    }
}
