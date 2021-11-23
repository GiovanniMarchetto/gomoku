package it.units.sdm.gomoku.model.entities.player;

import it.units.sdm.gomoku.model.custom_types.Buffer;
import it.units.sdm.gomoku.model.custom_types.Color;
import it.units.sdm.gomoku.model.custom_types.Coordinates;
import it.units.sdm.gomoku.model.custom_types.PositiveInteger;
import it.units.sdm.gomoku.model.entities.Cell;
import it.units.sdm.gomoku.model.entities.Game;
import it.units.sdm.gomoku.model.entities.Player;
import it.units.sdm.gomoku.model.entities.Stone;
import it.units.sdm.gomoku.model.exceptions.*;
import it.units.sdm.gomoku.property_change_handlers.observable_properties.ObservableProperty;
import it.units.sdm.gomoku.utils.TestUtility;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {
    private final static int BOARD_SIZE = 5;
    private final static String SAMPLE_NAME = "player";
    private final static Coordinates SAMPLE_VALID_COORDINATES = new Coordinates(0, 0);
    private Player blackPlayer;
    private Player whitePlayer;
    private Game game;

    @SuppressWarnings("unchecked") // buffer for moves contains coordinates
    @NotNull
    private static Buffer<Coordinates> getNextMoveBuffer(@NotNull final Player player)
            throws NoSuchFieldException, IllegalAccessException {
        return (Buffer<Coordinates>) Objects.requireNonNull(
                TestUtility.getFieldValue("nextMoveBuffer", Objects.requireNonNull(player)));
    }

    private static void insertCoordinatesInBufferOfPlayerIfBufferIsEmpty(
            @SuppressWarnings("SameParameterValue") @NotNull final Coordinates coordinates,
            @NotNull final Player playerWithNoGameSet)
            throws NoSuchFieldException, IllegalAccessException {

        Buffer<Coordinates> bufferWithMoveOfPlayer = getNextMoveBuffer(Objects.requireNonNull(playerWithNoGameSet));
        assert bufferWithMoveOfPlayer.isEmpty();
        bufferWithMoveOfPlayer.insert(Objects.requireNonNull(coordinates));
        assert bufferWithMoveOfPlayer.getNumberOfElements() == 1;
    }

    @NotNull
    private Cell getCellAtCoordinates(
            @SuppressWarnings("SameParameterValue") @NotNull final Coordinates coordinates)
            throws CellOutOfBoardException {
        return game.getBoard().getCellAtCoordinates(Objects.requireNonNull(coordinates));
    }

    @BeforeEach
    void setup() throws GameAlreadyStartedException {
        blackPlayer = new FakePlayer(SAMPLE_NAME);
        whitePlayer = new FakePlayer(SAMPLE_NAME);
        game = new Game(new PositiveInteger(BOARD_SIZE), blackPlayer, whitePlayer);
        blackPlayer.setCurrentGame(game);
        whitePlayer.setCurrentGame(game);
        game.start();
    }

    @Test
    void dontMakeMoveIfGameNotSet()
            throws NoSuchFieldException, IllegalAccessException, BoardIsFullException,
            GameEndedException, CellOutOfBoardException, CellAlreadyOccupiedException {

        Player playerWithNoGameSet = new FakePlayer(SAMPLE_NAME);
        insertCoordinatesInBufferOfPlayerIfBufferIsEmpty(SAMPLE_VALID_COORDINATES, playerWithNoGameSet);
        boolean caughtException = false;
        try {
            playerWithNoGameSet.makeMove();
        } catch (IllegalStateException e) {
            caughtException = true;
        }
        assertTrue(caughtException);
    }

    @Test
    void makeMoveIfValid()
            throws NoSuchFieldException, IllegalAccessException, CellOutOfBoardException,
            BoardIsFullException, GameEndedException, CellAlreadyOccupiedException {

        insertCoordinatesInBufferOfPlayerIfBufferIsEmpty(SAMPLE_VALID_COORDINATES, blackPlayer);
        Color currentPlayerColor = game.getColorOfPlayer(blackPlayer);
        assert getCellAtCoordinates(SAMPLE_VALID_COORDINATES).isEmpty();

        blackPlayer.makeMove();

        Stone justPlaced = getCellAtCoordinates(SAMPLE_VALID_COORDINATES).getStone();
        assert justPlaced != null;

        assertEquals(justPlaced.getColor(), currentPlayerColor);
    }

    @Test
    void dontRegisterMoveIfGameNotSet()
            throws GameEndedException, CellOutOfBoardException, CellAlreadyOccupiedException {

        Player playerWithNoGameSet = new FakePlayer(SAMPLE_NAME);
        boolean caughtException = false;
        try {
            playerWithNoGameSet.setMoveToBeMade(SAMPLE_VALID_COORDINATES);
        } catch (IllegalStateException e) {
            caughtException = true;
        }
        assertTrue(caughtException);
    }

    @Test
    void dontRegisterMoveIfMoveIsNull()
            throws GameEndedException, CellOutOfBoardException, CellAlreadyOccupiedException {

        boolean caughtException = false;
        try {
            Coordinates invalidMove = null;
            //noinspection ConstantConditions
            blackPlayer.setMoveToBeMade(invalidMove);
        } catch (NullPointerException e) {
            caughtException = true;
        }
        assertTrue(caughtException);
    }

    @Test
    void dontRegisterMoveIfBoardCellIsAlreadyOccupied()
            throws BoardIsFullException, CellOutOfBoardException, CellAlreadyOccupiedException, GameEndedException {

        final Color SAMPLE_COLOR = Color.BLACK;
        game.getBoard().occupyPosition(SAMPLE_COLOR, SAMPLE_VALID_COORDINATES);
        Stone justPlacedStone = getCellAtCoordinates(SAMPLE_VALID_COORDINATES).getStone();
        assert justPlacedStone != null;
        assert justPlacedStone.getColor().equals(SAMPLE_COLOR);

        boolean caughtException = false;
        try {
            blackPlayer.setMoveToBeMade(SAMPLE_VALID_COORDINATES);
        } catch (CellAlreadyOccupiedException e) {
            caughtException = true;
        }
        assertTrue(caughtException);
    }

    @Test
    void registerMoveIfValidCoordinates()
            throws GameEndedException, CellOutOfBoardException, CellAlreadyOccupiedException,
            NoSuchFieldException, IllegalAccessException {

        blackPlayer.setMoveToBeMade(SAMPLE_VALID_COORDINATES);
        Buffer<?> bufferOfPlayer = getNextMoveBuffer(blackPlayer);
        assertEquals(SAMPLE_VALID_COORDINATES, bufferOfPlayer.getAndRemoveLastElement());
    }

    @Test
    void createNewInstanceWithName() {
        blackPlayer = new FakePlayer(SAMPLE_NAME);
        assertEquals(SAMPLE_NAME, blackPlayer.getName());
    }

    @Test
    void testCurrentGameSetter()
            throws NoSuchFieldException, IllegalAccessException {

        blackPlayer.setCurrentGame(game);
        assertEquals(game, TestUtility.getFieldValue("currentGame", blackPlayer));
    }

    @ParameterizedTest
    @ValueSource(strings = {"Massimiliano", "Matteo", "Giovanni", "Travis Scott"})
    void testToString(String name) {
        Player player = new FakePlayer(name);
        assertEquals(player.toString(), name);
    }

    @Test
    void testCoordinatesRequiredToContinuePropertyGetter() {
        try {
            @SuppressWarnings("unchecked")
            ObservableProperty<Boolean> coordinatesRequiredToContinueProperty = (ObservableProperty<Boolean>)
                    TestUtility.getFieldValue("coordinatesRequiredToContinueProperty", blackPlayer);
            assertEquals(
                    coordinatesRequiredToContinueProperty,
                    blackPlayer.getCoordinatesRequiredToContinueProperty());
        } catch (NoSuchFieldException | IllegalAccessException e) {
            fail(e);
        }
    }

    @Test
    void setCoordinatesRequiredToContinuePropertyValueToFalseInTheConstructor() {
        assertEquals(Boolean.FALSE, blackPlayer.getCoordinatesRequiredToContinueProperty().getPropertyValue());
    }

    @Test
    void dontRequireMoveToFirstPlayerAfterHasJustDoneTheFirstMove()
            throws NoSuchFieldException, IllegalAccessException,
            GameEndedException, CellOutOfBoardException, CellAlreadyOccupiedException, BoardIsFullException {

        makeMoveIfValid();

        @SuppressWarnings("unchecked")
        ObservableProperty<Boolean> coordinatesRequiredToContinueProperty =
                (ObservableProperty<Boolean>) TestUtility.getFieldValue(
                        "coordinatesRequiredToContinueProperty", blackPlayer);

        //noinspection ConstantConditions
        assertEquals(Boolean.FALSE, coordinatesRequiredToContinueProperty.getPropertyValue());
    }

    @Test
    void testEqualIfSamePlayer() {
        assertEquals(blackPlayer, blackPlayer);
    }

    @Test
    void testNotEqualIfTheSecondIsNull() {
        assertNotEquals(blackPlayer, null);
    }

    @Test
    void testNotEqualIfNotInstanceOf() {
        assertNotEquals(blackPlayer, "");
    }

    @Test
    void testEqual() {
        Player player1 = new FakePlayer(SAMPLE_NAME);
        Player player2 = new FakePlayer(SAMPLE_NAME);
        assertEquals(player1, player2);
    }

    @Test
    void testNotEqualIfDifferentName() {
        Player player1 = new FakePlayer(SAMPLE_NAME);
        Player player2 = new FakePlayer(SAMPLE_NAME + "2");
        assertNotEquals(player1, player2);
    }

    @Test
    void testHashCode() {
        assertEquals(SAMPLE_NAME.hashCode(), blackPlayer.hashCode());
    }
}