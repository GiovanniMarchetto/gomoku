package it.units.sdm.gomoku.model.entities.player;

import it.units.sdm.gomoku.model.custom_types.Buffer;
import it.units.sdm.gomoku.model.custom_types.Color;
import it.units.sdm.gomoku.model.custom_types.Coordinates;
import it.units.sdm.gomoku.model.entities.Cell;
import it.units.sdm.gomoku.model.entities.Game;
import it.units.sdm.gomoku.model.entities.Player;
import it.units.sdm.gomoku.model.entities.Stone;
import it.units.sdm.gomoku.model.exceptions.BoardIsFullException;
import it.units.sdm.gomoku.model.exceptions.CellAlreadyOccupiedException;
import it.units.sdm.gomoku.model.exceptions.CellOutOfBoardException;
import it.units.sdm.gomoku.model.exceptions.GameEndedException;
import it.units.sdm.gomoku.property_change_handlers.observable_properties.ObservablePropertySettable;
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
            @NotNull final Coordinates coordinates, @NotNull final Player playerWithNoGameSet)
            throws NoSuchFieldException, IllegalAccessException {
        Buffer<Coordinates> bufferWithMoveOfPlayer = getNextMoveBuffer(Objects.requireNonNull(playerWithNoGameSet));
        assert bufferWithMoveOfPlayer.isEmpty();
        bufferWithMoveOfPlayer.insert(Objects.requireNonNull(coordinates));
        assert bufferWithMoveOfPlayer.getNumberOfElements() == 1;
    }

    @BeforeEach
    void setup() {
        blackPlayer = new FakePlayer(SAMPLE_NAME);
        whitePlayer = new FakePlayer(SAMPLE_NAME);
        game = new Game(BOARD_SIZE, blackPlayer, whitePlayer);
        blackPlayer.setCurrentGame(game);
        game.start();
    }

    @Test
    void changeTurnAfterAMoveIsMade()
            throws BoardIsFullException, GameEndedException, CellOutOfBoardException,
            NoSuchFieldException, IllegalAccessException, CellAlreadyOccupiedException {
        makeMoveIfValid();
        assertEquals(game.getCurrentPlayerProperty().getPropertyValue(), whitePlayer);
    }

    @Test
    void makeMoveIfValid()
            throws NoSuchFieldException, IllegalAccessException, CellOutOfBoardException,
            BoardIsFullException, GameEndedException, CellAlreadyOccupiedException {
        insertCoordinatesInBufferOfPlayerIfBufferIsEmpty(SAMPLE_VALID_COORDINATES, blackPlayer);
        Color currentPlayerColor = game.getColorOfPlayer(blackPlayer);
        assert currentPlayerColor.equals(Color.BLACK);
        blackPlayer.makeMove();
        Stone justPlaced = getCellAtCoordinates(SAMPLE_VALID_COORDINATES).getStone();
        assert justPlaced != null;
        assertEquals(justPlaced.getColor(), currentPlayerColor);
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
    void registerMoveFromUserIfValidCoordinates()
            throws GameEndedException, CellOutOfBoardException, CellAlreadyOccupiedException,
            NoSuchFieldException, IllegalAccessException {
        blackPlayer.setMoveToBeMade(SAMPLE_VALID_COORDINATES);
        Buffer<?> bufferOfPlayer = getNextMoveBuffer(blackPlayer);
        assertEquals(SAMPLE_VALID_COORDINATES, bufferOfPlayer.getAndRemoveLastElement());
    }

    @Test
    void dontRegisterMoveFromUserIfGameNotSet()
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
    void dontRegisterMoveFromUserIfMoveIsNull()
            throws GameEndedException, CellOutOfBoardException, CellAlreadyOccupiedException {
        boolean caughtException = false;
        try {
            Coordinates invalidMove = null;
            //noinspection ConstantConditions   // test for invalid moves
            blackPlayer.setMoveToBeMade(invalidMove);
        } catch (NullPointerException e) {
            caughtException = true;
        }
        assertTrue(caughtException);
    }

    @Test
    void dontRegisterMoveFromUserIfBoardCellIsAlreadyOccupied()
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

    @NotNull
    private Cell getCellAtCoordinates(@NotNull final Coordinates coordinates) throws CellOutOfBoardException {
        return game.getBoard().getCellAtCoordinates(Objects.requireNonNull(coordinates));
    }

    @ParameterizedTest
    @ValueSource(strings = {"Chan"})
    void createNewInstanceWithName(String playerName) {
        blackPlayer = new FakePlayer(playerName);
        assertEquals(playerName, blackPlayer.getName());
    }

    @Test
    void testCurrentGameSetter()
            throws NoSuchFieldException, IllegalAccessException {
        blackPlayer.setCurrentGame(game);
        assertEquals(game, TestUtility.getFieldValue("currentGame", blackPlayer));
    }

    @ParameterizedTest
    @ValueSource(strings = {"massimiliano", "matteo", "giovanni", "Travis Scott"})
    void testToString(String name) {
        Player player = new FakePlayer(name);
        assertEquals(player.toString(), name);
    }

    @Test
    void testCoordinatesRequiredToContinuePropertyGetter() {
        try {
            @SuppressWarnings("unchecked")
            ObservablePropertySettable<Boolean> coordinatesRequiredToContinueProperty = (ObservablePropertySettable<Boolean>)
                    TestUtility.getFieldValue("coordinatesRequiredToContinueProperty", blackPlayer);
            assertEquals(coordinatesRequiredToContinueProperty, blackPlayer.getCoordinatesRequiredToContinueProperty());
        } catch (NoSuchFieldException | IllegalAccessException e) {
            fail(e);
        }
    }

    @Test
    void setCoordinatesRequiredToContinuePropertyValueToFalseInTheConstructor() {
        assertEquals(Boolean.FALSE, blackPlayer.getCoordinatesRequiredToContinueProperty().getPropertyValue());
    }

    @Test
    void testEquals() {
        assertEquals(blackPlayer, whitePlayer);
    }

    @Test
    void testEquality() {
        assertNotSame(blackPlayer, whitePlayer);
    }

    @Test
    void testHashCode() {
        assertEquals(SAMPLE_NAME.hashCode(), blackPlayer.hashCode());
    }
}