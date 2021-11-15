package it.units.sdm.gomoku.model.custom_types;

import it.units.sdm.gomoku.EnvVariables;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class PositiveIntegerTest {

    @ParameterizedTest
    @CsvFileSource(resources = EnvVariables.INTS_PROVIDER_RESOURCE_LOCATION)
    void constructor(int value) {
        try {
            new PositiveInteger(value);
        } catch (Exception e) {
            if (value >= 1) {
                fail(e);
            }
        }
    }

    @ParameterizedTest
    @CsvFileSource(resources = EnvVariables.INTS_PROVIDER_RESOURCE_LOCATION)
    void copyConstructor(int value) {
        if (value >= 1) {
            PositiveInteger p1 = new PositiveInteger(value);
            PositiveInteger p2 = new PositiveInteger(p1);
            assertEquals(p1, p2);
        }
    }

    @ParameterizedTest
    @CsvSource({"0,false", "-1,false", "1,true", "2,true", "nonNumber,false"})
    void isPositiveIntegerFromString(String s, boolean expected) {
        assertEquals(expected, PositiveInteger.isPositiveIntegerFromString(s));
    }
}
