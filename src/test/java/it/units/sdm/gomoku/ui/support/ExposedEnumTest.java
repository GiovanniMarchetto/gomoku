package it.units.sdm.gomoku.ui.support;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class ExposedEnumTest {

    @ParameterizedTest
    @ValueSource(strings = {"1", "2", "3", "4", "5"})
    void isValidExposedValueOfTrueBoardSize(String boardSizeIndex) {
        assertTrue(ExposedEnum.isValidExposedValueOf(BoardSizes.class, boardSizeIndex));
    }

    @ParameterizedTest
    @ValueSource(strings = {"0", "1", "2"})
    void isValidExposedValueOfTrueMatchTypes(String matchTypeIndex) {
        assertTrue(ExposedEnum.isValidExposedValueOf(MatchTypes.class, matchTypeIndex));
    }

    @ParameterizedTest
    @ValueSource(strings = {"0", "-1", "6", "24"})
    void isValidExposedValueOfFalseBoardSize(String boardSizeIndex) {
        assertFalse(ExposedEnum.isValidExposedValueOf(BoardSizes.class, boardSizeIndex));
    }

    @ParameterizedTest
    @ValueSource(strings = {"3", "-1", "6", "24"})
    void isValidExposedValueOfFalseMatchTypes(String matchTypeIndex) {
        assertFalse(ExposedEnum.isValidExposedValueOf(MatchTypes.class, matchTypeIndex));
    }

    @Test
    void getEnumDescriptionOf() {
        String expected = "1 for VERY SMALL, " +
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