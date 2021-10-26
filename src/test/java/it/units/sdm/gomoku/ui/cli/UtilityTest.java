package it.units.sdm.gomoku.ui.cli;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

class UtilityTest {

    @Test
    void isY() {
        assertTrue(Utility.isY('Y'));
    }

    @ParameterizedTest
    @CsvSource({"true, a, a", "false, a, b", "false, a,C"})
    void isValidCharInserted(boolean expected, char insertedInput, char validInput) {
        assertEquals(expected, Utility.isValidCharInserted(insertedInput,validInput));
    }
}