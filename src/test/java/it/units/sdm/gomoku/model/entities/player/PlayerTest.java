package it.units.sdm.gomoku.model.entities.player;

import it.units.sdm.gomoku.model.entities.CPUPlayer;
import it.units.sdm.gomoku.model.entities.Game;
import it.units.sdm.gomoku.model.entities.HumanPlayer;
import it.units.sdm.gomoku.model.entities.Player;
import it.units.sdm.gomoku.property_change_handlers.observable_properties.ObservablePropertySettable;
import it.units.sdm.gomoku.utils.TestUtility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {
    private final int BOARD_SIZE = 5;
    private final String name = "player";
    private final Player cpuPlayer = new CPUPlayer();
    private Player player;
    private Game game;

    @BeforeEach
    void setup() {
        player = new HumanPlayer(name);
        game = new Game(BOARD_SIZE, player, cpuPlayer);
        game.start();
    }

    @ParameterizedTest
    @ValueSource(strings = {"Chan"})
    void createNewInstanceWithName(String playerName) {
        player = new HumanPlayer(playerName);
        assertEquals(playerName, player.getName());
    }

    @Test
    void assertCurrentGameNullAtStart()
            throws NoSuchFieldException, IllegalAccessException {
        assertNull(TestUtility.getFieldValue("currentGame", player));
    }

    @Test
    void assertCurrentGameIsCorrectlySet()
            throws NoSuchFieldException, IllegalAccessException {
        player.setCurrentGame(game);
        assertEquals(game, TestUtility.getFieldValue("currentGame", player));
    }


    @ParameterizedTest
    @ValueSource(strings = {"massimiliano", "matteo", "giovanni", "Travis Scott"})
    void testToString(String name) {
        Player player = new HumanPlayer(name);
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
        Player player2 = new HumanPlayer(name);
        assertEquals(player, player2);
    }

    @Test
    void testEquality() {
        Player player1 = new HumanPlayer("player");
        Player player2 = new HumanPlayer("player");
        assertNotSame(player1, player2);
    }

    @Test
    void testHashCode() {
        assertEquals(name.hashCode(), player.hashCode());
    }
}