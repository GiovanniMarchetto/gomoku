package it.units.sdm.gomoku.model.custom_types;

import it.units.sdm.gomoku.EnvVariables;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CoordinatesTest {

    private Coordinates coordinates;

    @ParameterizedTest
    @CsvFileSource(resources = EnvVariables.COUPLE_OF_NON_NEGATIVE_INTS_PROVIDER_RESOURCE_LOCATION)
    void getX(int x, int y) {
        coordinates = new Coordinates(x, y);
        assertEquals(x, coordinates.getX());
    }

    @ParameterizedTest
    @CsvFileSource(resources = EnvVariables.COUPLE_OF_NON_NEGATIVE_INTS_PROVIDER_RESOURCE_LOCATION)
    void getY(int x, int y) {
        coordinates = new Coordinates(x, y);
        assertEquals(y, coordinates.getY());
    }

    @ParameterizedTest
    @CsvFileSource(resources = EnvVariables.COUPLE_OF_NON_NEGATIVE_INTS_PROVIDER_RESOURCE_LOCATION)
    void testToStringNotEmpty(int x, int y, String expected) {
        coordinates = new Coordinates(x, y);
        assertEquals(coordinates.toString(), expected);
    }
}