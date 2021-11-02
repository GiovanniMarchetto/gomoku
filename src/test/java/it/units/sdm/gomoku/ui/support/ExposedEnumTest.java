package it.units.sdm.gomoku.ui.support;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class ExposedEnumTest {

    @ParameterizedTest
    @ValueSource(strings = {"VERY_SMALL", "SMALL", "NORMAL","BIG","VERY_BIG"})
    void isValidExposedValueOfTrue(String boardSize) {
        assertTrue(ExposedEnum.isValidExposedValueOf(BoardSizes.class, boardSize));
    }

    @ParameterizedTest
    @ValueSource(strings = {"VERY SMALL", "SMALLING", "normal"})
    void isValidExposedValueOfFalse(String boardSize) {
        assertFalse(ExposedEnum.isValidExposedValueOf(BoardSizes.class, boardSize));
    }

    @Test
    void getEnumDescriptionOf() {
        String expected = "1 for VERY SMALL," +
                "2 for SMALL, " +
                "3 for NORMAL, " +
                "4 for BIG, " +
                "5 for VERY BIG";
        assertEquals(expected, ExposedEnum.getEnumDescriptionOf(BoardSizes.class));
    }

    @ParameterizedTest
    @CsvSource({"1,VERY_SMALL", "2,SMALL", "3,NORMAL", "4,BIG", "5,VERY_BIG"})
    void getEnumValueFromExposedValueOrNullBordSizes(String value, BoardSizes expected) {
        assertEquals(
                expected,
                ExposedEnum.getEnumValueFromExposedValueOrNull(BoardSizes.class, value)
        );
    }

    @ParameterizedTest
    @CsvSource({"0", "6", "22", "-1"})
    void getEnumValueFromExposedValueOrNullAssertNull(String value) {
        assertNull(ExposedEnum.getEnumValueFromExposedValueOrNull(BoardSizes.class, value));
    }
}