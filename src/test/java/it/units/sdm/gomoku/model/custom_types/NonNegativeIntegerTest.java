package it.units.sdm.gomoku.model.custom_types;

import it.units.sdm.gomoku.EnvVariables;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class NonNegativeIntegerTest {

    private NonNegativeInteger nonNegativeInteger1;
    private NonNegativeInteger nonNegativeInteger2;

    public static Stream<Arguments> nonNegativeIntegerAndValidFlagSupplier() {
        return IntStream.rangeClosed(0, 1)
                .mapToObj(i -> Arguments.of(
                        i == 0 ? null : new NonNegativeInteger(i),
                        i != 0
                ));
    }

    @ParameterizedTest
    @CsvFileSource(resources = EnvVariables.INTS_PROVIDER_RESOURCE_LOCATION)
    void constructor(int value) {
        try {
            new NonNegativeInteger(value);
        } catch (Exception e) {
            if (value >= 0) {
                fail(e);
            }
        }
    }

    @ParameterizedTest
    @MethodSource("nonNegativeIntegerAndValidFlagSupplier")
    void doNotCreateIfNullInput(NonNegativeInteger n, boolean valid) {
        try {
            nonNegativeInteger1 = new NonNegativeInteger(n);
            assertTrue(valid);
        } catch (Exception e) {
            assertFalse(valid);
        }
    }

    @ParameterizedTest
    @CsvFileSource(resources = EnvVariables.NON_NEGATIVE_INTS_PROVIDER_RESOURCE_LOCATION)
    void incrementAndGet(int value) {
        nonNegativeInteger1 = new NonNegativeInteger(value);
        nonNegativeInteger2 = new NonNegativeInteger(value + 1);
        assertEquals(nonNegativeInteger2, nonNegativeInteger1.incrementAndGet());
    }

    @ParameterizedTest
    @CsvFileSource(resources = EnvVariables.NON_NEGATIVE_INTS_PROVIDER_RESOURCE_LOCATION)
    void intValue(int value) {
        nonNegativeInteger1 = new NonNegativeInteger(value);
        assertEquals(value, nonNegativeInteger1.intValue());
    }

    @ParameterizedTest
    @CsvFileSource(resources = EnvVariables.NON_NEGATIVE_INTS_PROVIDER_RESOURCE_LOCATION)
    void longValue(int value) {
        nonNegativeInteger1 = new NonNegativeInteger(value);
        assertEquals(Long.valueOf(value).longValue(), nonNegativeInteger1.longValue());
    }

    @ParameterizedTest
    @CsvFileSource(resources = EnvVariables.NON_NEGATIVE_INTS_PROVIDER_RESOURCE_LOCATION)
    void floatValue(int value) {
        nonNegativeInteger1 = new NonNegativeInteger(value);
        assertEquals(Float.valueOf(value).floatValue(), nonNegativeInteger1.floatValue());
    }

    @ParameterizedTest
    @CsvFileSource(resources = EnvVariables.NON_NEGATIVE_INTS_PROVIDER_RESOURCE_LOCATION)
    void doubleValue(int value) {
        nonNegativeInteger1 = new NonNegativeInteger(value);
        assertEquals(Double.valueOf(value).doubleValue(), nonNegativeInteger1.doubleValue());
    }

    @ParameterizedTest
    @CsvFileSource(resources = EnvVariables.NON_NEGATIVE_INTS_PROVIDER_RESOURCE_LOCATION)
    void toString(int value) {
        nonNegativeInteger1 = new NonNegativeInteger(value);
        assertEquals(String.valueOf(value), nonNegativeInteger1.toString());
    }

    @ParameterizedTest
    @CsvFileSource(resources = EnvVariables.COUPLE_OF_NON_NEGATIVE_INTS_PROVIDER_RESOURCE_LOCATION)
    void equals(int value1, int value2) {
        nonNegativeInteger1 = new NonNegativeInteger(value1);
        nonNegativeInteger2 = new NonNegativeInteger(value2);
        assertEquals(value1 == value2, nonNegativeInteger1.equals(nonNegativeInteger2));
    }

    @ParameterizedTest
    @CsvFileSource(resources = EnvVariables.COUPLE_OF_NON_NEGATIVE_INTS_PROVIDER_RESOURCE_LOCATION)
    void compareTo(int value1, int value2) {
        nonNegativeInteger1 = new NonNegativeInteger(value1);
        nonNegativeInteger2 = new NonNegativeInteger(value2);
        assertEquals(value1 >= value2, nonNegativeInteger1.compareTo(nonNegativeInteger2) >= 0);
    }

    @ParameterizedTest
    @CsvFileSource(resources = EnvVariables.INTS_PROVIDER_RESOURCE_LOCATION)
    void checkValidity(int valueToCheckIfIsNonNegativeInteger) {
        assertEquals(
                valueToCheckIfIsNonNegativeInteger >= 0,
                NonNegativeInteger.isValid(valueToCheckIfIsNonNegativeInteger));
    }
}