package it.units.sdm.gomoku.model.entities;

import it.units.sdm.gomoku.utils.TestUtility;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StoneTest {
    private Stone stone;

    @ParameterizedTest
    @EnumSource(Stone.Color.class)
    void isConstructorSetTheRightColor(Stone.Color color) throws NoSuchFieldException, IllegalAccessException {
        stone = new Stone(color);
        assertEquals(color, TestUtility.getFieldValue("color", stone));
    }

    @ParameterizedTest
    @EnumSource(Stone.Color.class)
    void isGetTheRightColor(Stone.Color color) {
        stone = new Stone(color);
        assertEquals(color, stone.getColor());
    }
}
