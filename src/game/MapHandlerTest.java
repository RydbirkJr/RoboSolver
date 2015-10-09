package game;

import core.Field;
import core.GameBoard;
import org.junit.Test;
import org.springframework.util.Assert;

import java.util.HashSet;

import static org.junit.Assert.*;

public class MapHandlerTest {

    @Test
    public void testValidatePosition() throws Exception {
        MapHandler handler = new MapHandler();

        GameBoard board = handler.setupGameBoard();
        HashSet<Field> states = new HashSet<Field>();

        Field field = board.fields[0][0];

        Assert.isTrue(handler.validatePosition(states, field));

        states.add(field);

        Assert.isTrue(!handler.validatePosition(states, field));

        Field goalField = board.fields[1][2];

        Assert.isTrue(!handler.validatePosition(states, goalField));
    }
}