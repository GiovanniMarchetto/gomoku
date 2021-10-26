package it.units.sdm.gomoku.ui.cli;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UtilityTest {

    @ParameterizedTest
    @CsvSource({"true, a, a", "false, a, b", "true, a,A"})
    void isValidCharInsertedFromStdIn(boolean expected, char insertedInput, char validInput) {
        try (ByteArrayInputStream fakeStdIn = new ByteArrayInputStream(new byte[]{(byte) insertedInput})) {
            System.setIn(fakeStdIn);
            assertEquals(expected, Utility.isValidCharInsertedFromStdInCaseInsensitive(validInput));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}