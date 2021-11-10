package it.units.sdm.gomoku.model.entities.game;

import it.units.sdm.gomoku.model.custom_types.Coordinates;
import it.units.sdm.gomoku.model.entities.*;
import it.units.sdm.gomoku.property_change_handlers.ObservableProperty;
import it.units.sdm.gomoku.utils.TestUtility;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static it.units.sdm.gomoku.model.entities.game.GameTestUtility.*;
import static org.junit.jupiter.api.Assertions.*;

class GameTest {
    private final int BOARD_SIZE = 5;
    private final CPUPlayer cpuBlack = new CPUPlayer();
    private final CPUPlayer cpuWhite = new CPUPlayer();
    private final Coordinates firstCoordinates = new Coordinates(0, 0);
    private final Coordinates secondCoordinates = new Coordinates(0, 1);
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
        Assertions.assertEquals(Stone.Color.BLACK, game.getColorOfPlayer(cpuBlack));
    }

    @Test
    void getColorOfPlayerWhite() {
        assertEquals(Stone.Color.WHITE, game.getColorOfPlayer(cpuWhite));
    }

    @Test
    void getWinnerBeforeEndGame() {
        game.start();
        try {
            game.getWinner();
            fail("Game not ended!");
        } catch (Game.GameNotEndedException ignored) {
        }
    }

    @Test
    void getWinnerWithBlackPlayerWon() {
        game.start();
        try {
            disputeGameAndPlayerWin(game, cpuBlack);
            assertEquals(cpuBlack, game.getWinner());
        } catch (Game.GameNotEndedException e) {
            fail(e);
        }
    }

    @Test
    void getWinnerWithWhitePlayerWon() {
        game.start();
        try {
            disputeGameAndPlayerWin(game, cpuWhite);
            assertEquals(cpuWhite, game.getWinner());
        } catch (Game.GameNotEndedException e) {
            fail(e);
        }
    }

    @Test
    void getWinnerWithDraw() {
        game.start();
        try {
            disputeGameAndDraw(game, BOARD_SIZE);
            assertNull(game.getWinner());
        } catch (Game.GameNotEndedException e) {
            fail(e);
        }
    }

    @Test
    void placeStoneBeforeStart() {
        try {
            tryToPlaceStoneAndChangeTurn(firstCoordinates, game);
        } catch (NullPointerException ignored) {
            assertTrue(game.getBoard().getCellAtCoordinates(firstCoordinates).isEmpty());
        }
    }

    @Test
    void placeStoneAfterStart() {
        game.start();
        tryToPlaceStoneAndChangeTurn(firstCoordinates, game);
        assertFalse(game.getBoard().getCellAtCoordinates(firstCoordinates).isEmpty());
    }

    @Test
    void changeTurnAfterFirstPlaceStone() {
        game.start();
        tryToPlaceStoneAndChangeTurn(firstCoordinates, game);
        assertEquals(cpuWhite, game.getCurrentPlayer().getPropertyValue());
    }

    @Test
    void changeTurnAfterSecondPlaceStone() {
        game.start();
        tryToPlaceStoneAndChangeTurn(firstCoordinates, game);
        tryToPlaceStoneAndChangeTurn(secondCoordinates, game);
        assertEquals(cpuBlack, game.getCurrentPlayer().getPropertyValue());
    }

    @Test
    void setWinnerIfIsTheWinMoveBlack() {
        game.start();
        placeTwoChainOfFourIn0And1Columns(game);
        tryToPlaceStoneAndChangeTurn(new Coordinates(4, 0), game);
        try {
            assertEquals(cpuBlack, game.getWinner());
        } catch (Game.GameNotEndedException e) {
            fail(e);
        }
    }

    @Test
    void setWinnerIfIsTheWinMoveWhite() {
        game.start();
        placeTwoChainOfFourIn0And1Columns(game);
        tryToPlaceStoneAndChangeTurn(new Coordinates(2, 1), game);
        tryToPlaceStoneAndChangeTurn(new Coordinates(4, 1), game);
        try {
            assertEquals(cpuWhite, game.getWinner());
        } catch (Game.GameNotEndedException e) {
            fail(e);
        }
    }

}