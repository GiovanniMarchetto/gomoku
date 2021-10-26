package it.units.sdm.gomoku.ui.cli;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UtilityTest {

    @ParameterizedTest
    @CsvSource({"a, a, a", "_, a, b", "a, a, A", "a, a, b#a#c","a, a, b#A#c", "_, a, b#d#c"})
    void getLowercaseCharIfValidLowerCaseOr0(char expected, char insertedInput, String validInputs) {
        if(expected=='_') {
            expected = 0;
        }
        String[] validInputStringArray = validInputs.split("#");
        char[] validInputCharArray = new char[validInputStringArray.length];
        for (int i = 0; i < validInputStringArray.length; i++) {
            validInputCharArray[i] = validInputStringArray[i].charAt(0);
        }

        try (ByteArrayInputStream fakeStdIn = new ByteArrayInputStream(new byte[]{(byte) insertedInput})) {
            System.setIn(fakeStdIn);
            assertEquals(expected, Utility.getLowercaseCharIfValidCaseInsensitiveOr0(validInputCharArray));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}