package it.units.sdm.gomoku.model.entities.player;

import it.units.sdm.gomoku.model.custom_types.Coordinates;
import it.units.sdm.gomoku.model.entities.Game;
import it.units.sdm.gomoku.model.entities.Player;
import it.units.sdm.gomoku.model.exceptions.CellAlreadyOccupiedException;
import it.units.sdm.gomoku.model.exceptions.CellOutOfBoardException;
import it.units.sdm.gomoku.model.exceptions.GameEndedException;
import it.units.sdm.gomoku.property_change_handlers.observable_properties.ObservablePropertySettable;
import it.units.sdm.gomoku.utils.TestUtility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

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

//    @Test
//    void registerMoveFromUserIfValidCoordinates() {
//
//    }

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