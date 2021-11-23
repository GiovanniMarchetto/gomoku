package it.units.sdm.gomoku.utils;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UtilityTest {

    @ParameterizedTest
    @CsvSource({"-10, true", "-11, false", "-2,true", "-1, false", "0, true", "1, false", "2, true", "10, true", "11, false"})
    void isEvenNumber(int numberToTest, boolean isEven) {
        assertEquals(isEven, Utility.isEvenNumber(numberToTest));
    }

}