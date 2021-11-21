package it.units.sdm.gomoku.model.entities.game;

import it.units.sdm.gomoku.model.custom_types.Color;
import it.units.sdm.gomoku.model.custom_types.Coordinates;
import it.units.sdm.gomoku.model.custom_types.PositiveInteger;
import it.units.sdm.gomoku.model.entities.Board;
import it.units.sdm.gomoku.model.entities.CPUPlayer;
import it.units.sdm.gomoku.model.entities.Game;
import it.units.sdm.gomoku.model.entities.Player;
import it.units.sdm.gomoku.model.exceptions.CellOutOfBoardException;
import it.units.sdm.gomoku.model.exceptions.GameNotEndedException;
import it.units.sdm.gomoku.property_change_handlers.observable_properties.ObservablePropertySettable;
import it.units.sdm.gomoku.utils.TestUtility;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Objects;

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
        game = new Game(new PositiveInteger(BOARD_SIZE), cpuBlack, cpuWhite);
    }

    @Test
    void getGameStatus() {
        try {
            Field gameStatusField =
                    TestUtility.getFieldAlreadyMadeAccessible(Game.class, "gameStatusProperty");
            @SuppressWarnings("unchecked")
            ObservablePropertySettable<Game.Status> gameStatusProperty =
                    (ObservablePropertySettable<Game.Status>) gameStatusField.get(game);
            assertEquals(gameStatusProperty, game.getGameStatusProperty());
        } catch (NoSuchFieldException | IllegalAccessException e) {
            fail(e);
        }
    }

    @Test
    void checkGameStatusBeforeStart() {
        assertNull(game.getGameStatusProperty().getPropertyValue());
    }

    @Test
    void checkGameStatusAfterStart() {
        game.start();
        assertEquals(Game.Status.STARTED, game.getGameStatusProperty().getPropertyValue());
    }

    @Test
    void getCurrentPlayer() {
        try {
            Field currentPlayerField =
                    TestUtility.getFieldAlreadyMadeAccessible(Game.class, "currentPlayerProperty");
            @SuppressWarnings("unchecked")
            ObservablePropertySettable<Player> currentPlayerProperty =
                    (ObservablePropertySettable<Player>) currentPlayerField.get(game);
            assertEquals(currentPlayerProperty, game.getCurrentPlayerProperty());
        } catch (NoSuchFieldException | IllegalAccessException e) {
            fail(e);
        }
    }

    @Test
    void checkCurrentPlayerBeforeStart() {
        assertNull(game.getCurrentPlayerProperty().getPropertyValue());
    }

    @Test
    void checkCurrentPlayerAfterStart() {
        game.start();
        assertEquals(cpuBlack, game.getCurrentPlayerProperty().getPropertyValue());
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
        Assertions.assertEquals(Color.BLACK, game.getColorOfPlayer(cpuBlack));
    }

    @Test
    void getColorOfPlayerWhite() {
        assertEquals(Color.WHITE, game.getColorOfPlayer(cpuWhite));
    }

    @Test
    void getWinnerBeforeEndGame() {
        game.start();
        try {
            game.getWinner();
            fail("Game not ended!");
        } catch (GameNotEndedException ignored) {
        }
    }

    @Test
    void getWinnerWithBlackPlayerWon() {
        game.start();
        try {
            disputeGameAndPlayerWin(game, cpuBlack);
            assertEquals(cpuBlack, game.getWinner());
        } catch (GameNotEndedException e) {
            fail(e);
        }
    }

    @Test
    void getWinnerWithWhitePlayerWon() {
        game.start();
        try {
            disputeGameAndPlayerWin(game, cpuWhite);
            assertEquals(cpuWhite, game.getWinner());
        } catch (GameNotEndedException e) {
            fail(e);
        }
    }

    @Test
    void getWinnerWithDraw() {
        game.start();
        try {
            disputeGameAndDraw(game);
            assertNull(game.getWinner());
        } catch (GameNotEndedException e) {
            fail(e);
        }
    }

    @Test
    void placeStoneBeforeStart() throws CellOutOfBoardException {
        try {
            tryToPlaceStoneAndChangeTurn(firstCoordinates, game);
        } catch (NullPointerException ignored) {
            assertTrue(game.getBoard().getCellAtCoordinates(firstCoordinates).isEmpty());
        }
    }

    @Test
    void placeStoneAfterStart() throws CellOutOfBoardException {
        game.start();
        tryToPlaceStoneAndChangeTurn(firstCoordinates, game);
        assertFalse(game.getBoard().getCellAtCoordinates(firstCoordinates).isEmpty());
    }

    @Test
    void changeTurnAfterFirstPlaceStone() {
        game.start();
        tryToPlaceStoneAndChangeTurn(firstCoordinates, game);
        assertEquals(cpuWhite, game.getCurrentPlayerProperty().getPropertyValue());
    }

    @Test
    void changeTurnAfterSecondPlaceStone() {
        game.start();
        tryToPlaceStoneAndChangeTurn(firstCoordinates, game);
        tryToPlaceStoneAndChangeTurn(secondCoordinates, game);
        assertEquals(cpuBlack, game.getCurrentPlayerProperty().getPropertyValue());
    }

    @Test
    void setWinnerIfIsTheWinMoveBlack() {
        game.start();
        disputeGameAndPlayerWin(game, cpuBlack);
        try {
            assertEquals(cpuBlack, game.getWinner());
        } catch (GameNotEndedException e) {
            fail(e);
        }
    }

    @Test
    void setWinnerIfIsTheWinMoveWhite() {
        game.start();
        disputeGameAndPlayerWin(game, cpuWhite);
        try {
            assertEquals(cpuWhite, game.getWinner());
        } catch (GameNotEndedException e) {
            fail(e);
        }
    }

    @Test
    void setGameStatusIfGameEndedWhenPlaceStone() {
        game.start();
        disputeGameAndPlayerWin(game, cpuBlack);
        assertEquals(Game.Status.ENDED, game.getGameStatusProperty().getPropertyValue());
    }

    @Test
    void setGameStatusIfGameNotEnded() {
        game.start();
        placeTwoChainOfFourIn0And1Rows(game);
        assertNotEquals(Game.Status.ENDED, game.getGameStatusProperty().getPropertyValue());
    }

    @Test
    void checkIsEndedInNormalExecution() {
        game.start();
        assertFalse(game.isEnded());
    }

    @Test
    void checkIsEndedWithWinner() {
        game.start();
        disputeGameAndPlayerWin(game, cpuBlack);
        assertTrue(game.isEnded());
    }

    @Test
    void checkIsEndedWithDraw() { //i.e. board is full
        game.start();
        disputeGameAndDraw(game);
        assertTrue(game.isEnded());
    }

    @Test
    void getStart() {
        try {
            ZonedDateTime expected = ((Instant)
                    Objects.requireNonNull(TestUtility.getFieldValue("creationTime", game)))
                    .atZone(ZoneId.systemDefault());
            assertEquals(expected, game.getCreationTime());
        } catch (NoSuchFieldException | IllegalAccessException e) {
            fail(e);
        }
    }

    @Test
    void compareTo() {
        try {
            Thread.sleep(0, 1000);//1 microsecond
        } catch (InterruptedException e) {
            fail(e);
        }
        Game gameNewer = new Game(new PositiveInteger(BOARD_SIZE), cpuBlack, cpuWhite);
        assertTrue(game.compareTo(gameNewer) < 0);
    }

    @Test
    void testToString() {
        game.start();
        disputeGameWithSmartAlgorithm(game);
        String expected = "";
        try {
            expected = "Game started at " + game.getCreationTime() + "\n" +
                    cpuBlack + " -> BLACK, " +
                    cpuWhite + " -> WHITE" + "\n" +
                    "Winner: " + game.getWinner() + "\n" +
                    game.getBoard();
        } catch (GameNotEndedException e) {
            fail(e);
        }
        assertEquals(expected, game.toString());
    }
}