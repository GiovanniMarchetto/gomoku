package it.units.sdm.gomoku.ui.cli;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

class UtilityTest {

    @ParameterizedTest
    @CsvSource({"true, a, a", "false, a, b", "true, a,A"})
    void isValidCharInsertedCaseInsensitive(boolean expected, char insertedInput, char validInput) {
        assertEquals(expected, Utility.isValidCharInsertedCaseInsensitive(insertedInput,validInput));
    }
}