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
    private final PositiveInteger BOARD_SIZE = new PositiveInteger(5);
    private final CPUPlayer cpuBlack = new CPUPlayer();
    private final CPUPlayer cpuWhite = new CPUPlayer();
    private final Coordinates firstCoordinates = new Coordinates(0, 0);
    private final Coordinates secondCoordinates = new Coordinates(0, 1);
    private Game game;

    @BeforeEach
    void setUp() {
        game = new Game(BOARD_SIZE, cpuBlack, cpuWhite);
        assert game.getGameStatusProperty().getPropertyValue() == null;
        assert game.getCurrentPlayerProperty().getPropertyValue() == null;
        game.start();
    }

    @Test
    void getGameStatus() throws NoSuchFieldException, IllegalAccessException {
        Field gameStatusField =
                TestUtility.getFieldAlreadyMadeAccessible(Game.class, "gameStatusProperty");
        @SuppressWarnings("unchecked")
        ObservablePropertySettable<Game.Status> gameStatusProperty =
                (ObservablePropertySettable<Game.Status>) gameStatusField.get(game);
        assertEquals(gameStatusProperty, game.getGameStatusProperty());
    }

    @Test
    void getCurrentPlayer() throws NoSuchFieldException, IllegalAccessException {
        Field currentPlayerField =
                TestUtility.getFieldAlreadyMadeAccessible(Game.class, "currentPlayerProperty");
        @SuppressWarnings("unchecked")
        ObservablePropertySettable<Player> currentPlayerProperty =
                (ObservablePropertySettable<Player>) currentPlayerField.get(game);
        assertEquals(currentPlayerProperty, game.getCurrentPlayerProperty());
    }

    @Test
    void checkGameStatusAfterStart() {
        assertEquals(Game.Status.STARTED, game.getGameStatusProperty().getPropertyValue());
    }

    @Test
    void checkCurrentPlayerAfterStart() {
        assertEquals(cpuBlack, game.getCurrentPlayerProperty().getPropertyValue());
    }

    @Test
    void getBoard() throws IllegalAccessException, NoSuchFieldException {
        Board board = (Board) TestUtility.getFieldAlreadyMadeAccessible(Game.class, "board").get(game);
        assertEquals(board, game.getBoard());
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
    void getWinnerIfGameNotEnded() {
        try {
            game.getWinner();
            fail("Game not ended!");
        } catch (GameNotEndedException ignored) {
        }
    }

    @Test
    void getWinnerIfBlackPlayerWon() throws GameNotEndedException {
        disputeGameAndPlayerWin(game, cpuBlack);
        assertEquals(cpuBlack, game.getWinner());
    }

    @Test
    void getWinnerIfWhitePlayerWon() throws GameNotEndedException {
        disputeGameAndPlayerWin(game, cpuWhite);
        assertEquals(cpuWhite, game.getWinner());
    }

    @Test
    void getWinnerIfDraw() throws GameNotEndedException {
        disputeGameAndDraw(game);
        assertNull(game.getWinner());
    }

    @Test
    void dontPlaceStoneIfGameNotStarted() throws CellOutOfBoardException {
        try {
            game = new Game(BOARD_SIZE, cpuBlack, cpuWhite);
            tryToPlaceStoneAndChangeTurn(firstCoordinates, game);
        } catch (NullPointerException e) {
            assertTrue(game.getBoard().getCellAtCoordinates(firstCoordinates).isEmpty());
        }
    }

    @Test
    void placeStoneAfterStart() throws CellOutOfBoardException {
        tryToPlaceStoneAndChangeTurn(firstCoordinates, game);
        assertFalse(game.getBoard().getCellAtCoordinates(firstCoordinates).isEmpty());
    }

    @Test
    void changeTurnAfterFirstPlaceStone() {
        tryToPlaceStoneAndChangeTurn(firstCoordinates, game);
        assertEquals(cpuWhite, game.getCurrentPlayerProperty().getPropertyValue());
    }

    @Test
    void changeTurnAfterSecondPlaceStone() {
        tryToPlaceStoneAndChangeTurn(firstCoordinates, game);
        tryToPlaceStoneAndChangeTurn(secondCoordinates, game);
        assertEquals(cpuBlack, game.getCurrentPlayerProperty().getPropertyValue());
    }

    @Test
    void setWinnerIfIsTheWinMoveBlack() throws GameNotEndedException {
        disputeGameAndPlayerWin(game, cpuBlack);
        assertEquals(cpuBlack, game.getWinner());
    }

    @Test
    void setWinnerIfIsTheWinMoveWhite() throws GameNotEndedException {
        disputeGameAndPlayerWin(game, cpuWhite);
        assertEquals(cpuWhite, game.getWinner());
    }

    @Test
    void setGameStatusIfGameEndedWhenPlaceStone() {
        disputeGameAndPlayerWin(game, cpuBlack);
        assertEquals(Game.Status.ENDED, game.getGameStatusProperty().getPropertyValue());
    }

    @Test
    void setGameStatusIfGameNotEnded() {
        placeTwoChainOfFourIn0And1Rows(game);
        assertNotEquals(Game.Status.ENDED, game.getGameStatusProperty().getPropertyValue());
    }

    @Test
    void checkIsEndedInNormalExecution() {
        assertFalse(game.isEnded());
    }

    @Test
    void checkIsEndedIfBlackWinner() {
        disputeGameAndPlayerWin(game, cpuBlack);
        assertTrue(game.isEnded());
    }

    @Test
    void checkIsEndedIfDraw() { //i.e. board is full but no winner
        disputeGameAndDraw(game);
        assertTrue(game.isEnded());
    }

    @Test
    void testCreationTimeGetter() throws NoSuchFieldException, IllegalAccessException {
        ZonedDateTime expected = ((Instant)
                Objects.requireNonNull(TestUtility.getFieldValue("creationTime", game)))
                .atZone(ZoneId.systemDefault());
        assertEquals(expected, game.getCreationTime());

    }

    @Test
    void testCompareTo() {
        try {
            Thread.sleep(0, 1000);//1 microsecond
        } catch (InterruptedException e) {
            fail(e);
        }
        Game gameNewer = new Game(BOARD_SIZE, cpuBlack, cpuWhite);
        assertTrue(game.compareTo(gameNewer) < 0);
    }

    @Test
    void testToString() {
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