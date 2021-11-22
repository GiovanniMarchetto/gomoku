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
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

import static it.units.sdm.gomoku.model.entities.game.GameTestUtility.*;
import static org.junit.jupiter.api.Assertions.*;

class GameTest {
    private static final PositiveInteger BOARD_SIZE = new PositiveInteger(5);
    private static final CPUPlayer blackPlayer = new CPUPlayer();
    private static final CPUPlayer whitePlayer = new CPUPlayer();
    private static final Coordinates firstMove = new Coordinates(0, 0);
    private static final Coordinates secondMove = new Coordinates(0, 1);
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
    void setGameStatusIsStartedAfterGameStarted() {
        assertEquals(Game.Status.STARTED, game.getGameStatusProperty().getPropertyValue());
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

    //region Test Getters
    @Test
    void testGameStatusGetter() throws NoSuchFieldException, IllegalAccessException {
        Field gameStatusField =
                TestUtility.getFieldAlreadyMadeAccessible(Game.class, "gameStatusProperty");
        @SuppressWarnings("unchecked")
        ObservablePropertySettable<Game.Status> gameStatusProperty =
                (ObservablePropertySettable<Game.Status>) gameStatusField.get(game);
        assertEquals(gameStatusProperty, game.getGameStatusProperty());
    }

    @Test
    void testCurrentPlayerPropertyGetter() throws NoSuchFieldException, IllegalAccessException {
        Field currentPlayerField =
                TestUtility.getFieldAlreadyMadeAccessible(Game.class, "currentPlayerProperty");
        @SuppressWarnings("unchecked")
        ObservablePropertySettable<Player> currentPlayerProperty =
                (ObservablePropertySettable<Player>) currentPlayerField.get(game);
        assertEquals(currentPlayerProperty, game.getCurrentPlayerProperty());
    }

    @Test
    void testBoardGetter() throws IllegalAccessException, NoSuchFieldException {
        Board board = (Board) TestUtility.getFieldAlreadyMadeAccessible(Game.class, "board").get(game);
        assertEquals(board, game.getBoard());
    }

    @Test
    void testBoardSizeGetter() {
        assertEquals(game.getBoard().getSize(), game.getBoardSize());
    }

    @Test
    void testCreationTimeGetter() throws NoSuchFieldException, IllegalAccessException {
        ZonedDateTime expected = ((Instant)
                Objects.requireNonNull(TestUtility.getFieldValue("creationTime", game)))
                .atZone(ZoneId.systemDefault());
        assertEquals(expected, game.getCreationTime());

    }

    @ParameterizedTest
    @EnumSource(Color.class)
    void testColorOfPlayerGetter(Color color) {
        switch (color) {
            case BLACK -> assertEquals(color, game.getColorOfPlayer(blackPlayer));
            case WHITE -> assertEquals(color, game.getColorOfPlayer(whitePlayer));
            default -> fail("No color test");
        }
    }

    @Test
    void testWinnerGetterIfGameNotEnded() {
        try {
            game.getWinner();
            fail("Game not ended!");
        } catch (GameNotEndedException ignored) {
        }
    }

    @Test
    void testWinnerGetterIfBlackPlayerWon() throws GameNotEndedException {
        disputeGameAndPlayerWin(game, blackPlayer);
        assertEquals(blackPlayer, game.getWinner());
    }

    @Test
    void testWinnerGetterIfWhitePlayerWon() throws GameNotEndedException {
        disputeGameAndPlayerWin(game, whitePlayer);
        assertEquals(whitePlayer, game.getWinner());
    }

    @Test
    void testWinnerGetterIfGameEndedWithDraw() throws GameNotEndedException {
        disputeGameAndDraw(game);
        assertNull(game.getWinner());
    }
    //endregion Test Getters

    @Test
    void checkCurrentPlayerAfterStart() {
        assertEquals(blackPlayer, game.getCurrentPlayerProperty().getPropertyValue());
    }

    @Test
    void dontPlaceStoneIfGameNotStarted() throws CellOutOfBoardException {
        try {
            game = createNewGameWithDefaultParams();
            tryToPlaceStoneAndChangeTurn(firstMove, game);
        } catch (NullPointerException e) {
            assertTrue(game.getBoard().getCellAtCoordinates(firstMove).isEmpty());
        }
    }

    @Test
    void placeStoneAfterStart() throws CellOutOfBoardException {
        tryToPlaceStoneAndChangeTurn(firstMove, game);
        assertFalse(game.getBoard().getCellAtCoordinates(firstMove).isEmpty());
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
    void setWinnerIfIsTheWinMoveBlack() throws GameNotEndedException {
        disputeGameAndPlayerWin(game, blackPlayer);
        assertEquals(blackPlayer, game.getWinner());
    }

    @Test
    void setWinnerIfIsTheWinMoveWhite() throws GameNotEndedException {
        disputeGameAndPlayerWin(game, whitePlayer);
        assertEquals(whitePlayer, game.getWinner());
    }

    @Test
    void setGameStatusIfGameEndedWhenPlaceStone() {
        disputeGameAndPlayerWin(game, blackPlayer);
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
        disputeGameAndPlayerWin(game, blackPlayer);
        assertTrue(game.isEnded());
    }

    @Test
    void checkIsEndedIfDraw() { //i.e. board is full but no winner
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
        disputeGameWithSmartAlgorithm(game);
        String expected = "";
        try {
            expected = "Game started at " + game.getCreationTime() + "\n" +
                    blackPlayer + " -> BLACK, " +
                    whitePlayer + " -> WHITE" + "\n" +
                    "Winner: " + game.getWinner() + "\n" +
                    game.getBoard();
        } catch (GameNotEndedException e) {
            fail(e);
        }
        assertEquals(expected, game.toString());
    }
}