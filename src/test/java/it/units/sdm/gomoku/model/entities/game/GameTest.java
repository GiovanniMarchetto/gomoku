package it.units.sdm.gomoku.model.entities.game;

import it.units.sdm.gomoku.model.custom_types.Color;
import it.units.sdm.gomoku.model.custom_types.Coordinates;
import it.units.sdm.gomoku.model.custom_types.PositiveInteger;
import it.units.sdm.gomoku.model.entities.Board;
import it.units.sdm.gomoku.model.entities.CPUPlayer;
import it.units.sdm.gomoku.model.entities.Game;
import it.units.sdm.gomoku.model.entities.Player;
import it.units.sdm.gomoku.model.entities.player.FakePlayer;
import it.units.sdm.gomoku.model.exceptions.*;
import it.units.sdm.gomoku.property_change_handlers.PropertyObserver;
import it.units.sdm.gomoku.property_change_handlers.observable_properties.ObservablePropertySettable;
import it.units.sdm.gomoku.utils.TestUtility;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.lang.reflect.Field;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

import static it.units.sdm.gomoku.model.entities.game.GameTestUtility.*;
import static org.junit.jupiter.api.Assertions.*;

class GameTest {
    private static final PositiveInteger BOARD_SIZE = new PositiveInteger(5);
    private static final CPUPlayer blackPlayer = new CPUPlayer();
    private static final CPUPlayer whitePlayer = new CPUPlayer();
    private static final Coordinates firstMove = new Coordinates(0, 0);
    private static final Coordinates secondMove = new Coordinates(0, 1);
    private static final Coordinates coordinatesOutsideBoard = new Coordinates(BOARD_SIZE.incrementAndGet(), BOARD_SIZE.incrementAndGet());
    private Game game;

    @NotNull
    private static Game createNewGameWithDefaultParams() {
        return new Game(BOARD_SIZE, blackPlayer, whitePlayer);
    }

    @BeforeEach
    void setUp() {
        game = createNewGameWithDefaultParams();
        assert game.getGameStatusProperty().getPropertyValue() == null;
        assert game.getCurrentPlayerProperty().getPropertyValue() == null;
        game.start();
        Stream.of(blackPlayer, whitePlayer)
                .forEach(player -> player.setCurrentGame(game));
    }

    @Test
    void initializeAllNotNullFieldsWhenCreatingNewInstance() {
        List<String> namesOfNullableFieldsOfClass = List.of("winner");
        game = createNewGameWithDefaultParams();
        long numberOfNullFieldsAfterConstructionWhichShouldNotBeNull =
                Arrays.stream(game.getClass().getDeclaredFields())
                        .filter(field -> !namesOfNullableFieldsOfClass.contains(field.getName()))
                        .peek(field -> field.setAccessible(true))
                        .map(field -> {
                            try {
                                return field.get(game);
                            } catch (IllegalAccessException e) {
                                fail(e);
                                return null;
                            }
                        })
                        .filter(Objects::isNull)
                        .count();
        assertEquals(0, numberOfNullFieldsAfterConstructionWhichShouldNotBeNull);
    }

    @Test
    void createNewInstanceWithCorrectCreationTime() {
        final long EPSILON_NANOS = 1000;
        long currentTime = Instant.now().getNano();
        game = createNewGameWithDefaultParams();
        long creationTimeSetInGame = game.getCreationTime().getNano();
        assertTrue(Math.abs(currentTime - creationTimeSetInGame) < EPSILON_NANOS);
    }

    @Test
    void setGameStatusToStartedAfterGameStarted() {
        assertEquals(Game.Status.STARTED, game.getGameStatusProperty().getPropertyValue());
    }

    @Test
    void setGameStatusToEndedIfGameEndedDueToLastMove() {
        disputeGameAndDraw(game);
        assertEquals(Game.Status.ENDED, game.getGameStatusProperty().getPropertyValue());
    }

    @Test
    void notifyGameStatusOnStart() {
        game = createNewGameWithDefaultParams();
        AtomicReference<Boolean> gameHasNotifiedToBeStarted = new AtomicReference<>();
        boolean notificationSent = false;

        new PropertyObserver<>(
                game.getGameStatusProperty(),
                evt -> gameHasNotifiedToBeStarted.set(Game.Status.STARTED.equals(evt.getNewValue())));

        game.start();
        //noinspection StatementWithEmptyBody   // wait property change notification
        while (!gameHasNotifiedToBeStarted.get()) {
        }

        assertTrue(gameHasNotifiedToBeStarted.get());
    }

    //region Test Getters / Setters
    @Test
    void testCurrentPlayerPropertyGetter() throws NoSuchFieldException, IllegalAccessException {
        Field currentPlayerField =
                TestUtility.getFieldAlreadyMadeAccessible(game.getClass(), "currentPlayerProperty");
        @SuppressWarnings("unchecked")
        ObservablePropertySettable<Player> currentPlayerProperty =
                (ObservablePropertySettable<Player>) currentPlayerField.get(game);
        assertEquals(currentPlayerProperty, game.getCurrentPlayerProperty());
    }

    @Test
    void testGameStatusPropertyGetter() throws NoSuchFieldException, IllegalAccessException {
        Field gameStatusField =
                TestUtility.getFieldAlreadyMadeAccessible(game.getClass(), "gameStatusProperty");
        @SuppressWarnings("unchecked")
        ObservablePropertySettable<Game.Status> gameStatusProperty =
                (ObservablePropertySettable<Game.Status>) gameStatusField.get(game);
        assertEquals(gameStatusProperty, game.getGameStatusProperty());
    }

    @SuppressWarnings("unchecked")  // checked casting
    @ParameterizedTest
    @EnumSource(Game.Status.class)
    void testGameStatusPropertyValueGetter(Game.Status gameStatusToSet) throws NoSuchFieldException, IllegalAccessException {
        ((ObservablePropertySettable<Game.Status>)
                Objects.requireNonNull(TestUtility.getFieldValue("gameStatusProperty", game)))
                .setPropertyValue(gameStatusToSet);
        assertEquals(gameStatusToSet, game.getGameStatusProperty().getPropertyValue());
    }

    @Test
    void testBoardGetter() throws IllegalAccessException, NoSuchFieldException {
        Board board = (Board) TestUtility.getFieldAlreadyMadeAccessible(game.getClass(), "board").get(game);
        assertEquals(board, game.getBoard());
    }

    @Test
    void testBoardSizeGetter() {
        assertEquals(game.getBoard().getSize(), game.getBoardSize());
    }

    @Test
    void testCreationTimeGetter() throws NoSuchFieldException, IllegalAccessException {
        long expectedNanos = ((Instant)
                Objects.requireNonNull(TestUtility.getFieldValue("creationTime", game)))
                .getNano();
        assertEquals(expectedNanos, game.getCreationTime().getNano());

    }

    @ParameterizedTest
    @EnumSource(Color.class)
    void testColorOfPlayerGetter(Color color) {
        switch (color) {
            case BLACK -> assertEquals(color, game.getColorOfPlayer(blackPlayer));
            case WHITE -> assertEquals(color, game.getColorOfPlayer(whitePlayer));
            default -> fail(new IllegalArgumentException("Not a valid color"));
        }
    }

    @Test
    void throwExceptionIfGameNotEndedWhenGetWinnerInvoked() {
        assert !game.isEnded();
        try {
            game.getWinner();
            fail("Should have thrown an exception, but did not");
        } catch (GameNotEndedException ignored) {
        }
    }

    @ParameterizedTest
    @EnumSource(Color.class)
    void setPlayerAsWinnerIfWon(Color playerColor) throws GameNotEndedException {
        Player winnerPlayerToSet = null;
        switch (playerColor) {
            case BLACK -> winnerPlayerToSet = blackPlayer;
            case WHITE -> winnerPlayerToSet = whitePlayer;
            default -> fail(new IllegalArgumentException("Not a valid color"));
        }
        disputeGameAndMakeThePlayerToWin(game, winnerPlayerToSet);
        assertEquals(winnerPlayerToSet, game.getWinner());
    }

    @Test
    void dontSetWinnerIfGameEndedWithDraw() throws GameNotEndedException {
        disputeGameAndDraw(game);
        assertNull(game.getWinner());
    }

    @SuppressWarnings("unchecked")  // checked casting
    @ParameterizedTest
    @EnumSource(Game.Status.class)
    void testIsEnded(Game.Status gameStatusToSet) throws NoSuchFieldException, IllegalAccessException {
        ((ObservablePropertySettable<Game.Status>)
                Objects.requireNonNull(TestUtility.getFieldValue("gameStatusProperty", game)))
                .setPropertyValue(gameStatusToSet);
        assertEquals(gameStatusToSet == Game.Status.ENDED, game.isEnded());
    }

    //endregion Test Getters / Setters

    @Test
    void setBlackPlayerAsFirstPlayerWhenGameStarts() {
        assertEquals(blackPlayer, game.getCurrentPlayerProperty().getPropertyValue());
    }

    @Test
    void dontPlaceStoneIfGameNotStarted() throws CellOutOfBoardException {
        try {
            game = createNewGameWithDefaultParams();
            tryToPlaceStoneAndChangeTurn(firstMove, game);
        } catch (NullPointerException e) {
            assertTrue(isEmptyCell(firstMove));
        }
    }

    @Test
    void placeStoneAfterGameStarted() throws CellOutOfBoardException {
        tryToPlaceStoneAndChangeTurn(firstMove, game);
        assertFalse(isEmptyCell(firstMove));
    }

    @Test
    void changeTurnAfterAMoveIsMadeIfGameNotEnded() throws BoardIsFullException, GameEndedException, CellOutOfBoardException, CellAlreadyOccupiedException {
        final Player currentPlayerForTheMove = game.getCurrentPlayerProperty().getPropertyValue();
        game.placeStoneAndChangeTurn(firstMove);
        final Player currentPlayerAfterTheMoveIsDone = game.getCurrentPlayerProperty().getPropertyValue();
        assertNotEquals(currentPlayerForTheMove, currentPlayerAfterTheMoveIsDone);
    }

    @Test
    void dontPlaceStoneIfGameEndedOrBoardIsFull() throws CellOutOfBoardException, CellAlreadyOccupiedException {
        disputeGameAndDraw(game);
        try {
            game.placeStoneAndChangeTurn(firstMove);
            fail("Game should be ended, but the move was accepted");
        } catch (GameEndedException | BoardIsFullException e) {
            assertTrue(game.isEnded());
        }
    }

    @Test
    void dontPlaceStoneIfCellAlreadyOccupied() throws CellOutOfBoardException, BoardIsFullException, GameEndedException, CellAlreadyOccupiedException {
        assert isEmptyCell(firstMove);
        game.placeStoneAndChangeTurn(firstMove);
        assert !isEmptyCell(firstMove);
        try {
            game.placeStoneAndChangeTurn(firstMove);    // replace at same position
            fail("Cell already occupied and placing stone allowed but should not.");
        } catch (CellAlreadyOccupiedException e) {
            assertFalse(isEmptyCell(firstMove));
        }
    }

    @Test
    void dontPlaceStoneIfCellOutsideTheBoard() {
        try {
            game.placeStoneAndChangeTurn(coordinatesOutsideBoard);
            fail("Cell outside the board and placing stone has been allowed but should not be.");
        } catch (Exception e) {
            assertTrue(e instanceof CellOutOfBoardException);
        }
    }

    private boolean isEmptyCell(@NotNull final Coordinates coordinateOfCellOnBoard) throws CellOutOfBoardException {
        return game.getBoard()
                .getCellAtCoordinates(Objects.requireNonNull(coordinateOfCellOnBoard))
                .isEmpty();
    }

    @Test
    void changeTurnAfterFirstPlaceStone() {
        tryToPlaceStoneAndChangeTurn(firstMove, game);
        assertEquals(whitePlayer, game.getCurrentPlayerProperty().getPropertyValue());
    }

    @Test
    void changeTurnAfterSecondPlaceStone() {
        tryToPlaceStoneAndChangeTurn(firstMove, game);
        tryToPlaceStoneAndChangeTurn(secondMove, game);
        assertEquals(blackPlayer, game.getCurrentPlayerProperty().getPropertyValue());
    }

    @Test
    void dontChangeTurnIfInvalidMove() {
        final Player currentPlayer = game.getCurrentPlayerProperty().getPropertyValue();
        try {
            game.placeStoneAndChangeTurn(coordinatesOutsideBoard);
        } catch (Exception e) {
            assertEquals(game.getCurrentPlayerProperty().getPropertyValue(), currentPlayer);
        }
    }

    @Test
    void testIsEndedToReturnFalseIfGameNotEnded() {
        assertFalse(game.isEnded());    // game is just started
    }

    @ParameterizedTest
    @EnumSource(Color.class)
    void testIsEndedToReturnTrueIfAPlayerWon(Color playerColor) {
        Player winnerPlayerToSet = null;
        switch (playerColor) {
            case BLACK -> winnerPlayerToSet = blackPlayer;
            case WHITE -> winnerPlayerToSet = whitePlayer;
            default -> fail(new IllegalArgumentException("Not a valid color"));
        }
        disputeGameAndMakeThePlayerToWin(game, winnerPlayerToSet);
        assertTrue(game.isEnded());
    }

    @Test
    void testIsEndedToReturnTrueIfGameEndedWithADraw() { //i.e. board is full but no winner
        disputeGameAndDraw(game);
        assertTrue(game.isEnded());
    }

    @Test
    void testCompareTo() {
        try {
            Thread.sleep(0, 1000);//1 microsecond
        } catch (InterruptedException e) {
            fail(e);
        }
        Game gameNewer = createNewGameWithDefaultParams();
        assertTrue(game.compareTo(gameNewer) < 0);
    }

    @Test
    void testToString() {
        PositiveInteger boardSizeOfThree = new PositiveInteger(3);
        Player gianniPlayer = new FakePlayer("Gianni");
        Player beppePlayer = new FakePlayer("Beppe");

        game = new Game(boardSizeOfThree, gianniPlayer, beppePlayer);
        game.start();

        tryToPlaceStoneAndChangeTurn(new Coordinates(0, 0), game);
        tryToPlaceStoneAndChangeTurn(new Coordinates(1, 1), game);

        String lineSeparator = System.lineSeparator();
        String expected = "Game started at " + game.getCreationTime() +
                lineSeparator + "Gianni -> BLACK, Beppe -> WHITE" +
                lineSeparator + "Winner: null" +
                lineSeparator + "    0  1  2  " +
                lineSeparator + "0|  X       " +
                lineSeparator + "1|     O    " +
                lineSeparator + "2|          " +
                lineSeparator;
        assertEquals(expected, game.toString());
    }
}