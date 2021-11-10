package it.units.sdm.gomoku.model.entities;

import it.units.sdm.gomoku.model.custom_types.Coordinates;
import it.units.sdm.gomoku.model.custom_types.PositiveInteger;
import it.units.sdm.gomoku.property_change_handlers.ObservableProperty;
import it.units.sdm.gomoku.utils.TestUtility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

class GameTest {
    private final PositiveInteger BOARD_SIZE = new PositiveInteger(5);
    private final CPUPlayer cpuBlack = new CPUPlayer();
    private final CPUPlayer cpuWhite = new CPUPlayer();
    private Game game;

    @BeforeEach
    void setUp() {
        game = new Game(BOARD_SIZE, cpuBlack, cpuWhite);
    }

    @Test
    void getGameStatus() {
        try {
            Field gameStatusField = TestUtility.getFieldAlreadyMadeAccessible(Game.class, "gameStatus");
            @SuppressWarnings("unchecked")
            ObservableProperty<Game.Status> gameStatusProperty = (ObservableProperty<Game.Status>) gameStatusField.get(game);
            assertEquals(gameStatusProperty, game.getGameStatus());
        } catch (NoSuchFieldException | IllegalAccessException e) {
            fail(e);
        }
    }

    @Test
    void checkGameStatusBeforeStart() {
        assertNull(game.getGameStatus().getPropertyValue());
    }

    @Test
    void checkGameStatusAfterStart() {
        game.start();
        assertEquals(Game.Status.STARTED, game.getGameStatus().getPropertyValue());
    }

    @Test
    void getCurrentPlayer() {
        try {
            Field currentPlayerField = TestUtility.getFieldAlreadyMadeAccessible(Game.class, "currentPlayer");
            @SuppressWarnings("unchecked")
            ObservableProperty<Player> currentPlayerProperty = (ObservableProperty<Player>) currentPlayerField.get(game);
            assertEquals(currentPlayerProperty, game.getCurrentPlayer());
        } catch (NoSuchFieldException | IllegalAccessException e) {
            fail(e);
        }
    }

    @Test
    void checkCurrentPlayerBeforeStart() {
        assertNull(game.getCurrentPlayer().getPropertyValue());
    }

    @Test
    void checkCurrentPlayerAfterStart() {
        game.start();
        assertEquals(cpuBlack, game.getCurrentPlayer().getPropertyValue());
    }

    @Test
    void getBoard() {
        try {
            Field boardField = TestUtility.getFieldAlreadyMadeAccessible(Game.class, "board");
            Board board = (Board) boardField.get(game);
            assertEquals(board, game.getBoard());
        } catch (NoSuchFieldException | IllegalAccessException e) {
            fail(e);
        }
    }

    @Test
    void getColorOfPlayerBlack() {
        assertEquals(Stone.Color.BLACK, game.getColorOfPlayer(cpuBlack));
    }

    @Test
    void getColorOfPlayerWhite() {
        assertEquals(Stone.Color.WHITE, game.getColorOfPlayer(cpuWhite));
    }

    @Test
    void placeStoneBeforeStart() {
        try {
            game.placeStoneAndChangeTurn(new Coordinates(0, 0));
            fail();
        } catch (Board.BoardIsFullException | Game.GameEndedException | Board.CellAlreadyOccupiedException e) {
            fail(e);
        } catch (NullPointerException ignored) {
        }
    }

    //region Support methods
    public static void disputeGameWithSmartAlgorithm(Game game) {
        CPUPlayer cpuPlayer = new CPUPlayer();
        while (!game.isEnded()) {
            try {
                game.placeStoneAndChangeTurn(cpuPlayer.chooseSmartEmptyCoordinates(game.getBoard()));
            } catch (Board.CellAlreadyOccupiedException | Board.BoardIsFullException | Game.GameEndedException e) {
                fail(e);
            }
        }
    }
    //endregion
}