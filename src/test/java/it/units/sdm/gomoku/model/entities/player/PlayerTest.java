package it.units.sdm.gomoku.model.entities.player;

import it.units.sdm.gomoku.model.actors.HumanPlayer;
import it.units.sdm.gomoku.model.actors.Player;
import it.units.sdm.gomoku.property_change_handlers.observable_properties.ObservablePropertyThatCanSetPropertyValueAndFireEvents;
import it.units.sdm.gomoku.utils.TestUtility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {
    String name = "player";
    Player player;

    @BeforeEach
    void setup() {
        player = new HumanPlayer(name);
    }

    @Test
    void getName() {
        assertEquals(name, player.getName());
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
            ObservablePropertyThatCanSetPropertyValueAndFireEvents<Boolean> coordinatesRequiredToContinueProperty = (ObservablePropertyThatCanSetPropertyValueAndFireEvents<Boolean>)
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