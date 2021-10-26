package it.units.sdm.gomoku.ui.cli;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UtilityTest {

    @ParameterizedTest
    @CsvSource({"true, a, a", "false, a, b", "true, a,A"})
    void isValidCharInsertedCaseInsensitive(boolean expected, char insertedInput, char validInput) {
        assertEquals(expected, Utility.isValidCharInsertedCaseInsensitive(insertedInput, validInput));
    }

    @Test
    void isYInsertedFromStdIn() {
        try (ByteArrayInputStream fakeStdIn = new ByteArrayInputStream(new byte[]{'Y'})) {
            System.setIn(fakeStdIn);
            assertTrue(Utility.isYInsertedFromStdIn());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @ParameterizedTest
    @CsvSource({"true, a, a", "false, a, b", "true, a,A"})
    void isValidCharInsertedFromStdIn(boolean expected, char insertedInput, char validInput) {
        try (ByteArrayInputStream fakeStdIn = new ByteArrayInputStream(new byte[]{(byte) insertedInput})) {
            System.setIn(fakeStdIn);
            assertEquals(expected, Utility.isValidCharInsertedFromStdIn(validInput));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}