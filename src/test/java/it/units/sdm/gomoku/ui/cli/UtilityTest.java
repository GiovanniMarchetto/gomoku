package it.units.sdm.gomoku.ui.cli;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UtilityTest {

    @Test
    void isY() {
        assertTrue(Utility.isY('Y'));
    }

    @Test
    void isValidCharInserted() {
        char insertedInput = 'Y';
        char validInput = 'Y';
        assertEquals(insertedInput==validInput,
                Utility.isValidCharInserted(insertedInput,validInput));
    }
}