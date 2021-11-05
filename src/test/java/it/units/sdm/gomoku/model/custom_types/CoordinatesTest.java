package it.units.sdm.gomoku.model.custom_types;

import it.units.sdm.gomoku.EnvVariables;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class CoordinatesTest {

    private Coordinates coordinates;

    private static Stream<Arguments> nonNegativeIntegerPairAndValidFlagSupplier() {
        return IntStream.rangeClosed(0, 1).boxed()
                .flatMap(i -> IntStream.rangeClosed(0, 1)
                        .mapToObj(j -> Arguments.of(
                                i == 0 ? null : new NonNegativeInteger(i),
                                j == 0 ? null : new NonNegativeInteger(j),
                                i == 1 && j == 1)));
    }

    @ParameterizedTest
    @CsvSource({"0,0,true", "-1,0,false", "0,-1,false", "-1,-1,false", "1,1,true"})
    void doNotCreateIfInvalidInput(int x, int y, boolean valid) {
        try {
            coordinates = new Coordinates(x, y);
            assertTrue(valid);
        } catch (Exception e) {
            assertFalse(valid);
        }
    }

    @ParameterizedTest
    @MethodSource("nonNegativeIntegerPairAndValidFlagSupplier")
    void doNotCreateIfNullInput(NonNegativeInteger x, NonNegativeInteger y, boolean valid) {
        try {
            coordinates = new Coordinates(x, y);
            assertTrue(valid);
        } catch (Exception e) {
            assertFalse(valid);
        }
    }

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
    void toString(int x, int y, String expected) {
        coordinates = new Coordinates(x, y);
        assertEquals(coordinates.toString(), expected);
    }
}