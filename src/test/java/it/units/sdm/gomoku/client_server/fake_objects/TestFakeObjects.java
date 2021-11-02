package it.units.sdm.gomoku.client_server.fake_objects;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestFakeObjects {

    @ParameterizedTest
    @ValueSource(strings = {"Hello", "World", "", "?", "^", "\n\t\r"})
    void testEchoProtocol(String input) {
        assertEquals(input, new EchoProtocol().processInput(input));
    }

    // TODO : missing tests of fake server and client
}