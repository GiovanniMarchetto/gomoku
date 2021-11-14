package it.units.sdm.gomoku.model.custom_types;

import it.units.sdm.gomoku.EnvVariables;
import it.units.sdm.gomoku.Utility;
import it.units.sdm.gomoku.utils.TestUtility;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class BufferTest {

    final static int MAX_BUFFER_SIZE_USED_IN_TEST = 10000;

    @ParameterizedTest
    @CsvFileSource(resources = EnvVariables.INTS_PROVIDER_RESOURCE_LOCATION)
    void constructorValidateSize(int size) {
        if (size <= MAX_BUFFER_SIZE_USED_IN_TEST) {
            try {
                new Buffer<>(size);
                assertTrue(size > 0);
            } catch (Exception e) {
                assertTrue(size <= 0);
            }
        } else {
            Utility.getLoggerOfClass(getClass()).info(
                    "Parameter size=" + size + " is greater than " + MAX_BUFFER_SIZE_USED_IN_TEST + ", " +
                            "hence the test is considered passed");
        }
    }

    @ParameterizedTest
    @CsvFileSource(resources = EnvVariables.POSITIVE_INTS_PROVIDER_RESOURCE_LOCATION)
    void constructorCreatesObjectWithCorrectSize(int size) {
        Buffer<Integer> buffer = new Buffer<>(size);
        try {
            assertEquals(TestUtility.getFieldValue("size", buffer), size);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            fail(e);
        }
    }

    @ParameterizedTest
    @CsvFileSource(resources = EnvVariables.POSITIVE_INTS_PROVIDER_RESOURCE_LOCATION)
    void constructorCreatesObjectWithCorrectlyInitializedBuffer(int size) {
        Buffer<Integer> buffer = new Buffer<>(size);
        try {
            assertEquals(TestUtility.getFieldValue("buffer", buffer), new ArrayList<Integer>(size));
        } catch (NoSuchFieldException | IllegalAccessException e) {
            fail(e);
        }
    }
}