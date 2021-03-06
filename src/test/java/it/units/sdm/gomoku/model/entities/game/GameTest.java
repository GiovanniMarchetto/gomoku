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
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.lang.reflect.Field;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static it.units.sdm.gomoku.model.entities.game.GameTestUtility.disputeGameAndDraw;
import static it.units.sdm.gomoku.model.entities.game.GameTestUtility.disputeGameAndMakeThePlayerToWin;
import static org.junit.jupiter.api.Assertions.*;

class GameTest {
    static final PositiveInteger BOARD_SIZE = new PositiveInteger(5);
    static final CPUPlayer blackPlayer = new CPUPlayer();
    static final CPUPlayer whitePlayer = new CPUPlayer();
    private static final Coordinates coordinatesForFirstMove = new Coordinates(0, 0);
    private static final Coordinates coordinatesForSecondMove = new Coordinates(0, 1);
    private static final Coordinates coordinatesOutsideBoard = new Coordinates(BOARD_SIZE.incrementAndGet(), BOARD_SIZE.incrementAndGet());
    private final Coordinates[] coordinatesForCheckHeadChains = {
            new Coordinates(0, 0), new Coordinates(0, 3),
            new Coordinates(0, 1), new Coordinates(2, 3),
            new Coordinates(1, 1)};
    private final Coordinates[] coordinatesThatAreAHeadChainsOfThree = {
            new Coordinates(0, 2), new Coordinates(2, 1)};
    private Game game;

    @NotNull
    private static Stream<Arguments> provideCoupleOfNonNegativeIntegersInsideBoard() {
        return TestUtility.provideCoupleOfNonNegativeIntegersTillNExcluded(BOARD_SIZE.intValue());
    }

    private boolean isEmptyCellAtCoordinatesForFirstMove() throws CellOutOfBoardException {
        return game.getBoard()
                .getCellAtCoordinates(Objects.requireNonNull(GameTest.coordinatesForFirstMove))
                .isEmpty();
    }

    @BeforeEach
    void setUp() throws GameAlreadyStartedException {
        game = GameTestUtility.createNewGameWithDefaultParams();
        assert game.getCurrentPlayerProperty().getPropertyValue() == null;
        game.start();
        Stream.of(blackPlayer, whitePlayer)
                .forEach(player -> player.setCurrentGame(game));
    }

    @Test
    void initializeAllNotNullFieldsWhenCreatingNewInstance() {
        List<String> namesOfNullableFieldsOfClass = List.of("winner");
        game = GameTestUtility.createNewGameWithDefaultParams();
        long numberOfNullFieldsAfterConstructionWhichShouldNotBeNull =
                TestUtility.getNumberOfNullFieldsOfObjectWhichNameIsNotInList(namesOfNullableFieldsOfClass, game);
        assertEquals(0, numberOfNullFieldsAfterConstructionWhichShouldNotBeNull);
    }

    @Test
    void createNewInstanceWithCorrectCreationTime() {
        final long EPSILON_NANOS = 1000;
        long currentTime = Instant.now().getNano();
        game = GameTestUtility.createNewGameWithDefaultParams();
        long creationTimeSetInGame = game.getCreationTime().getNano();
        assertTrue(Math.abs(currentTime - creationTimeSetInGame) < EPSILON_NANOS);
    }

    @Test
    void assertGameNotStartedWhenJustCreated() {
        game = GameTestUtility.createNewGameWithDefaultParams();
        assertEquals(Game.Status.NOT_STARTED, game.getGameStatusProperty().getPropertyValue());
    }

    @ParameterizedTest
    @EnumSource(Game.Status.class)
    void setGameStatus(Game.Status gameStatusToTest) throws GameAlreadyStartedException {
        game = GameTestUtility.createNewGameWithDefaultParams();
        if (gameStatusToTest != Game.Status.NOT_STARTED) {
            game.start();
        }
        if (gameStatusToTest == Game.Status.ENDED) {
            disputeGameAndDraw(game);
        }
        assertEquals(gameStatusToTest, game.getGameStatusProperty().getPropertyValue());
    }

    @ParameterizedTest
    @EnumSource(Game.Status.class)
    void notifyGameStatus(Game.Status gameStatusToTest) throws GameAlreadyStartedException {

        game = GameTestUtility.createNewGameWithDefaultParams();

        AtomicReference<Boolean> gameHasNotified = new AtomicReference<>();
        new PropertyObserver<>(
                game.getGameStatusProperty(),
                evt -> gameHasNotified.set(gameStatusToTest.equals(evt.getNewValue())));

        switch (gameStatusToTest) {
            case NOT_STARTED -> {
                return;/*nothing to notify, this is the initial state*/
            }
            case STARTED -> game.start();
            case ENDED -> {
                game.start();
                disputeGameAndDraw(game);
            }
            default -> throw new IllegalArgumentException("Unexpected value for game status");
        }

        //noinspection StatementWithEmptyBody   // wait property change notification
        while (!gameHasNotified.get()) {
        }

        assertTrue(gameHasNotified.get());
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
    void testGameStatusPropertyValueGetter(Game.Status gameStatusToSet)
            throws NoSuchFieldException, IllegalAccessException {

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
    //endregion Test Getters / Setters

    @SuppressWarnings("unchecked")  // checked casting
    @ParameterizedTest
    @EnumSource(Game.Status.class)
    void testIsNotStarted(Game.Status gameStatusToSet) throws NoSuchFieldException, IllegalAccessException {
        ((ObservablePropertySettable<Game.Status>)
                Objects.requireNonNull(TestUtility.getFieldValue("gameStatusProperty", game)))
                .setPropertyValue(gameStatusToSet);
        assertEquals(gameStatusToSet == Game.Status.NOT_STARTED, game.isNotStarted());
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

    @Test
    void setBlackPlayerAsFirstPlayerWhenGameStarts() {
        assertEquals(blackPlayer, game.getCurrentPlayerProperty().getPropertyValue());
    }

    @Test
    void placeStoneAfterGameStarted()
            throws CellOutOfBoardException, GameEndedException, CellAlreadyOccupiedException, GameNotStartedException {

        game.placeStoneAndChangeTurn(coordinatesForFirstMove);
        assertFalse(isEmptyCellAtCoordinatesForFirstMove());
    }

    @Test
    void changeTurnAfterAMoveIsMadeIfGameNotEnded() throws GameEndedException, CellOutOfBoardException, CellAlreadyOccupiedException, GameNotStartedException {
        final Player currentPlayerForTheMove = game.getCurrentPlayerProperty().getPropertyValue();
        game.placeStoneAndChangeTurn(coordinatesForFirstMove);
        final Player currentPlayerAfterTheMoveIsDone = game.getCurrentPlayerProperty().getPropertyValue();
        assertNotEquals(currentPlayerForTheMove, currentPlayerAfterTheMoveIsDone);
    }

    @Test
    void dontStartGameIfAlreadyStarted() {
        game = GameTestUtility.createNewGameWithDefaultParams();
        try {
            game.start();
        } catch (Exception e) {
            fail("Unexpected exception: a new game must be started");
        }
        try {
            game.start();
        } catch (Exception e) {
            assertTrue(e instanceof GameAlreadyStartedException);/*game can be started only once*/
        }
    }

    @Test
    void dontPlaceStoneIfGameNotStarted()
            throws CellOutOfBoardException, GameEndedException, CellAlreadyOccupiedException {

        try {
            game = GameTestUtility.createNewGameWithDefaultParams();
            game.placeStoneAndChangeTurn(coordinatesForFirstMove);
        } catch (GameNotStartedException e) {
            assertTrue(isEmptyCellAtCoordinatesForFirstMove());
        }
    }

    @Test
    void dontPlaceStoneIfGameEndedOrBoardIsFull() throws CellOutOfBoardException, CellAlreadyOccupiedException, GameNotStartedException {
        disputeGameAndDraw(game);
        try {
            game.placeStoneAndChangeTurn(coordinatesForFirstMove);
            fail("Game should be ended, but the move was accepted");
        } catch (GameEndedException e) {
            assertTrue(game.isEnded());
        }
    }

    @Test
    void dontPlaceStoneIfCellAlreadyOccupied()
            throws CellOutOfBoardException, GameEndedException, CellAlreadyOccupiedException, GameNotStartedException {

        assert isEmptyCellAtCoordinatesForFirstMove();
        game.placeStoneAndChangeTurn(coordinatesForFirstMove);
        assert !isEmptyCellAtCoordinatesForFirstMove();
        try {
            game.placeStoneAndChangeTurn(coordinatesForFirstMove);    // replace at same position
            fail("Cell already occupied and placing stone allowed but should not.");
        } catch (CellAlreadyOccupiedException e) {
            assertFalse(isEmptyCellAtCoordinatesForFirstMove());
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

    @Test
    void changeTurnAfterFirstPlaceStone()
            throws GameEndedException, CellOutOfBoardException, CellAlreadyOccupiedException, GameNotStartedException {

        game.placeStoneAndChangeTurn(coordinatesForFirstMove);
        assertEquals(whitePlayer, game.getCurrentPlayerProperty().getPropertyValue());
    }

    @Test
    void changeTurnAfterSecondPlaceStone()
            throws GameEndedException, CellOutOfBoardException, CellAlreadyOccupiedException, GameNotStartedException {

        game.placeStoneAndChangeTurn(coordinatesForFirstMove);
        game.placeStoneAndChangeTurn(coordinatesForSecondMove);
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
    void testIsEndedToReturnTrueIfGameEndedWithADraw() {
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
        Game gameNewer = GameTestUtility.createNewGameWithDefaultParams();
        assertTrue(game.compareTo(gameNewer) < 0);
    }

    @Test
    void testToString()
            throws GameEndedException, CellOutOfBoardException, CellAlreadyOccupiedException, GameNotStartedException, GameAlreadyStartedException {

        PositiveInteger boardSizeOfThree = new PositiveInteger(3);
        Player gianniPlayer = new FakePlayer("Gianni");
        Player beppePlayer = new FakePlayer("Beppe");

        game = new Game(boardSizeOfThree, gianniPlayer, beppePlayer);
        game.start();

        game.placeStoneAndChangeTurn(new Coordinates(0, 0));
        game.placeStoneAndChangeTurn(new Coordinates(1, 1));

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

    @ParameterizedTest
    @MethodSource("provideCoupleOfNonNegativeIntegersInsideBoard")
    void occupyFourPositionAndCheckIfIsValidMove(int x, int y)
            throws GameEndedException, CellOutOfBoardException {
        final int endOfIndexToOccupyExclusive = 2;
        IntStream.range(0, endOfIndexToOccupyExclusive).forEach(row ->
                IntStream.range(0, endOfIndexToOccupyExclusive).forEach(col -> {
                    try {
                        game.placeStoneAndChangeTurn(new Coordinates(row, col));
                    } catch (CellAlreadyOccupiedException
                            | GameEndedException | CellOutOfBoardException | GameNotStartedException e) {
                        fail(e);
                    }
                }));

        boolean expected = !(x < endOfIndexToOccupyExclusive && y < endOfIndexToOccupyExclusive);
        assertEquals(expected, game.isValidMove(new Coordinates(x, y)));
    }

    @Test
    void disputeGameAndValidateAnotherMoveAfterTheEnd() throws CellOutOfBoardException {
        disputeGameAndDraw(game);
        try {
            game.isValidMove(new Coordinates(BOARD_SIZE, BOARD_SIZE));
            fail("Game already ended!");
        } catch (GameEndedException ignored) {
        }
    }

    @ParameterizedTest
    @MethodSource("provideCoupleOfNonNegativeIntegersInsideBoard")
    void placeSomeStonesAndCheckIfIsHeadOfAChainOfThree(int x, int y) {
        IntStream.range(0, coordinatesForCheckHeadChains.length).forEach(i -> {
            try {
                game.placeStoneAndChangeTurn(coordinatesForCheckHeadChains[i]);
            } catch (CellAlreadyOccupiedException
                    | GameEndedException | CellOutOfBoardException | GameNotStartedException e) {
                fail(e);
            }
        });
        Coordinates coordinates = new Coordinates(x, y);
        boolean isAHeadOfAChainOfThree = Arrays.stream(coordinatesThatAreAHeadChainsOfThree).anyMatch(c -> c == coordinates);
        assertEquals(isAHeadOfAChainOfThree, game.isHeadOfAChainOfStones(coordinates, new PositiveInteger(3)));
    }
}