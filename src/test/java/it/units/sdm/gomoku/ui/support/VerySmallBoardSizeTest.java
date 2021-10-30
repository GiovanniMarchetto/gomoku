package it.units.sdm.gomoku.ui.support;

import it.units.sdm.gomoku.model.custom_types.PositiveOddInteger;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class VerySmallBoardSizeTest {

    private final BoardSizes verySmall = BoardSizes.VERY_SMALL;
    private final String verySmallString = "VERY SMALL";
    private final PositiveOddInteger verySmallValue = new PositiveOddInteger(9);

    @Test
    void fromStringVerySmall() {
        assertEquals(verySmall, BoardSizes.fromString(verySmallString));
    }

    @Test
    void getBoardSizeVerySmall() {
        assertEquals(verySmallValue, verySmall.getBoardSize());
    }

    @Test
    void getExposedValueOfVerySmall() {
        String expectedVerySmallOrdinalValueString = "1";
        assertEquals(expectedVerySmallOrdinalValueString, verySmall.getExposedValueOf());
    }

    @Test
    void testToStringVerySmall() {
        assertEquals(verySmallString, verySmall.toString());
    }
}