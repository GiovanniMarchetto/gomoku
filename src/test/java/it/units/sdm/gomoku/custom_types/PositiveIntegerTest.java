package it.units.sdm.gomoku.custom_types;

import it.units.sdm.gomoku.EnvVariables;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import static org.junit.jupiter.api.Assertions.fail;

public class PositiveIntegerTest {

    @ParameterizedTest
    @CsvFileSource(resources = EnvVariables.INTS_PROVIDER_RESOURCE_LOCATION)
    void constructorTest(int value) {
        try {
            new PositiveInteger(value);
        } catch (Exception e) {
            if (value >= 1) {
                fail(e);
            }
        }
    }

}
