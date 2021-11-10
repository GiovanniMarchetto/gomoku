package it.units.sdm.gomoku.model.entities.player;

import it.units.sdm.gomoku.EnvVariables;
import it.units.sdm.gomoku.model.entities.HumanPlayer;
import it.units.sdm.gomoku.model.entities.Player;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PlayerTest {

    @ParameterizedTest
    @CsvFileSource(resources = EnvVariables.PLAYERS_NAME_PROVIDER_RESOURCE_LOCATION)
    void testToString(String name) {
        Player player = new HumanPlayer(name);
        assertEquals(player.toString(), name);
    }
}