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
    private Player player;
    private Game game;

    @BeforeEach
    void setup() {
        player = new FakePlayer(SAMPLE_NAME);
        game = new Game(BOARD_SIZE, player, player);
        player.setCurrentGame(game);
        game.start();
    }

    @Test
    void dontMakeMoveIfGameNotSet() {
        // TODO
    }

    @Test
    void makeMoveIfValid() {
        // TODO
    }

    @Test
    void changeTurnAfterAMoveIsMade() {
        // TODO
    }

    @Test
    void registerMoveFromUserIfValidCoordinates() throws GameEndedException, CellOutOfBoardException, CellAlreadyOccupiedException, NoSuchFieldException, IllegalAccessException {
        player.setMoveToBeMade(SAMPLE_VALID_COORDINATES);
        Buffer<?> bufferOfPlayer = ((Buffer<?>) TestUtility.getFieldValue("nextMoveBuffer", player));
        assert bufferOfPlayer != null;
        assertEquals(SAMPLE_VALID_COORDINATES, bufferOfPlayer.getAndRemoveLastElement());
    }

    @Test
    void dontRegisterMoveFromUserIfGameNotSet() throws GameEndedException, CellOutOfBoardException, CellAlreadyOccupiedException {
        Player playerWithNoGameSet = new FakePlayer(SAMPLE_NAME);
        boolean catchedException = false;
        try {
            playerWithNoGameSet.setMoveToBeMade(SAMPLE_VALID_COORDINATES);
        } catch (IllegalStateException e) {
            catchedException = true;
        }
        assertTrue(catchedException);
    }

    @Test
    void dontRegisterMoveFromUserIfMoveIsNull() throws GameEndedException, CellOutOfBoardException, CellAlreadyOccupiedException {
        boolean catchedException = false;
        try {
            player.setMoveToBeMade(null);
        } catch (NullPointerException e) {
            catchedException = true;
        }
        assertTrue(catchedException);
    }

    @Test
    void dontRegisterMoveFromUserIfBoardCellIsAlreadyOccupied() throws BoardIsFullException, CellOutOfBoardException, CellAlreadyOccupiedException, GameEndedException {
        final Color SAMPLE_COLOR = Color.BLACK;
        game.getBoard().occupyPosition(SAMPLE_COLOR, SAMPLE_VALID_COORDINATES);
        Stone justPlacedStone = getCellAtCoordinates(SAMPLE_VALID_COORDINATES).getStone();
        assert justPlacedStone != null;
        assert justPlacedStone.getColor().equals(SAMPLE_COLOR);   // TODO : code smell message chain
        boolean catchedException = false;
        try {
            player.setMoveToBeMade(SAMPLE_VALID_COORDINATES);
        } catch (CellAlreadyOccupiedException e) {
            catchedException = true;
        }
        assertTrue(catchedException);
    }

    @NotNull
    private Cell getCellAtCoordinates(@NotNull final Coordinates coordinates) throws CellOutOfBoardException {
        return game.getBoard().getCellAtCoordinates(Objects.requireNonNull(coordinates));   // TODO: message chain code smell?
    }

    @ParameterizedTest
    @ValueSource(strings = {"Chan"})
    void createNewInstanceWithName(String playerName) {
        player = new FakePlayer(playerName);
        assertEquals(playerName, player.getName());
    }

    @Test
    void testCurrentGameSetter()
            throws NoSuchFieldException, IllegalAccessException {
        player.setCurrentGame(game);
        assertEquals(game, TestUtility.getFieldValue("currentGame", player));
    }


    @ParameterizedTest
    @ValueSource(strings = {"massimiliano", "matteo", "giovanni", "Travis Scott"})
    void testToString(String name) {
        Player player = new FakePlayer(name);
        assertEquals(player.toString(), name);
    }

    @Test
    void getCoordinatesRequiredToContinueProperty() {
        try {
            @SuppressWarnings("unchecked")
            ObservablePropertySettable<Boolean> coordinatesRequiredToContinueProperty = (ObservablePropertySettable<Boolean>)
                    TestUtility.getFieldValue("coordinatesRequiredToContinueProperty", player);
            assertEquals(coordinatesRequiredToContinueProperty, player.getCoordinatesRequiredToContinueProperty());
        } catch (NoSuchFieldException | IllegalAccessException e) {
            fail(e);
        }
    }

    @Test
    void checkCoordinatesRequiredAtStart() {
        assertEquals(Boolean.FALSE, player.getCoordinatesRequiredToContinueProperty().getPropertyValue());
    }

    @Test
    void testEquals() {
        Player player2 = new FakePlayer(SAMPLE_NAME);
        assertEquals(player, player2);
    }

    @Test
    void testEquality() {
        Player player1 = new FakePlayer("player");
        Player player2 = new FakePlayer("player");
        assertNotSame(player1, player2);
    }

    @Test
    void testHashCode() {
        assertEquals(SAMPLE_NAME.hashCode(), player.hashCode());
    }
}