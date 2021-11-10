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

    //region Private Support methods
    private static void tryToPlaceStoneAndChangeTurn(Coordinates coordinates, Game game) {
        try {
            game.placeStoneAndChangeTurn(coordinates);
        } catch (Board.BoardIsFullException | Game.GameEndedException | Board.CellAlreadyOccupiedException e) {
            fail(e);
        }
    }

    //region Public Support methods
    public static void disputeGameWithSmartAlgorithm(Game game) {
        CPUPlayer cpuPlayer = new CPUPlayer();
        while (!game.isEnded()) {
            try {
                tryToPlaceStoneAndChangeTurn(cpuPlayer.chooseSmartEmptyCoordinates(game.getBoard()), game);
            } catch (Board.BoardIsFullException e) {
                fail(e);
            }
        }
    }

    public static void disputeGameAndDraw(Game game, int boardSize) {
        CPUPlayer cpuPlayer = new CPUPlayer();

        for (int x = 0; x < boardSize; x++) {
            for (int y = 0; y < boardSize; y++) {
                if (x % 3 == 0 && y == 0) {
                    tryToPlaceStoneAndChangeTurn(new Coordinates(x, y), game);
                }
            }
        }

        while (!game.isEnded()) {
            try {
                tryToPlaceStoneAndChangeTurn(cpuPlayer.chooseNextEmptyCoordinates(game.getBoard()), game);
            } catch (Board.BoardIsFullException e) {
                fail(e);
            }
        }

        try {
            if (game.getWinner() != null) {
                fail("It's not a draw");
            }
        } catch (Game.GameNotEndedException e) {
            fail(e);
        }
    }
    //endregion

    @Test
    void placeStoneBeforeStart() {
        final Coordinates coordinates = new Coordinates(0, 0);
        try {
            tryToPlaceStoneAndChangeTurn(coordinates, game);
        } catch (NullPointerException ignored) {
            assertTrue(game.getBoard().getCellAtCoordinates(coordinates).isEmpty());
        }
    }

    public static void disputeGameAndPlayerWin(Game game, Player player) {
        try {
            for (int i = 0; i < 4; i++) {
                game.placeStoneAndChangeTurn(new Coordinates(i, 0));
                game.placeStoneAndChangeTurn(new Coordinates(i, 1));
            }

            if (player == game.getCurrentPlayer().getPropertyValue()) {
                game.placeStoneAndChangeTurn(new Coordinates(4, 0));
            } else {
                game.placeStoneAndChangeTurn(new Coordinates(0, 2));
                game.placeStoneAndChangeTurn(new Coordinates(4, 1));
            }

            if (game.getWinner() != player) {
                fail("The winner is not the correct player");
            }
        } catch (Board.BoardIsFullException | Board.CellAlreadyOccupiedException | Game.GameNotEndedException | Game.GameEndedException e) {
            fail(e);
        }
    }

    @Test
    void placeStoneAfterStart() {
        game.start();
        final Coordinates coordinates = new Coordinates(0, 0);
        tryToPlaceStoneAndChangeTurn(coordinates, game);
        assertFalse(game.getBoard().getCellAtCoordinates(coordinates).isEmpty());
    }
    //endregion
}