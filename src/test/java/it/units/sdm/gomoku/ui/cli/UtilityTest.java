package it.units.sdm.gomoku.ui.cli;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UtilityTest {

    @ParameterizedTest
    @CsvSource({"true, a, a", "false, a, b", "true, a, A", "true, a, b#a#c","true, a, b#A#c", "false, a, b#d#c"})
    void isValidCharInsertedFromStdIn(boolean expected, char insertedInput, String validInputs) {
        String[] validInputStringArray = validInputs.split("#");
        char[] validInputCharArray = new char[validInputStringArray.length];
        for (int i = 0; i < validInputStringArray.length; i++) {
            validInputCharArray[i] = validInputStringArray[i].charAt(0);
        }

        try (ByteArrayInputStream fakeStdIn = new ByteArrayInputStream(new byte[]{(byte) insertedInput})) {
            System.setIn(fakeStdIn);
            assertEquals(expected, Utility.isValidCharInsertedFromStdInCaseInsensitive(validInputCharArray));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}