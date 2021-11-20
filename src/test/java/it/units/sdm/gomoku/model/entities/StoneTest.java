package it.units.sdm.gomoku.model.entities;

import it.units.sdm.gomoku.model.custom_types.Color;
import it.units.sdm.gomoku.utils.TestUtility;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class StoneTest {
    private Stone stone;

    @ParameterizedTest
    @EnumSource(Color.class)
    void isConstructorSetTheRightColor(Color color) throws NoSuchFieldException, IllegalAccessException {
        stone = new Stone(color);
        assertEquals(color, TestUtility.getFieldValue("color", stone));
    }

    @ParameterizedTest
    @EnumSource(Color.class)
    void isTheColorReturnedFromTheGetterTheSameSetFromTheConstructor(Color color) {
        stone = new Stone(color);
        assertEquals(color, stone.getColor());
    }

    @ParameterizedTest
    @EnumSource(Color.class)
    void areEqualsTwoDistinctStonesWithTheSameColor(Color color) {
        stone = new Stone(color);
        Stone stone2 = new Stone(color);
        assertEquals(stone, stone2);
    }


    @ParameterizedTest
    @EnumSource(Color.class)
    void areNotEqualsTwoDistinctStonesWithDifferentColor(Color color) {
        stone = new Stone(color);
        Optional<Color> colorOptional =
                Arrays.stream(Color.values()).filter(color1 -> color1 != color).findAny();
        if (colorOptional.isPresent()) {
            Stone stone2 = new Stone(colorOptional.get());
            assertNotEquals(stone, stone2);
        } else {
            fail("There are no others color, impossible!");
        }
    }
}
