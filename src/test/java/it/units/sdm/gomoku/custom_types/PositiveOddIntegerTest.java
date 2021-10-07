package it.units.sdm.gomoku.custom_types;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import static org.junit.jupiter.api.Assertions.*;

class PositiveOddIntegerTest {

    private final static String INTS_PROVIDER_RESOURCE_LOCATION = "/ints.csv";

    @ParameterizedTest
    @CsvFileSource(resources = INTS_PROVIDER_RESOURCE_LOCATION)
    void constructorTest(int value) {
        try {
            new PositiveOddInteger(value);
        } catch (Exception e) {
            if (value % 2 == 1) { // condition value >= 1 is implicit, e.g. -3 % 2 = -1
                fail(e);
            }
        }
    }
}