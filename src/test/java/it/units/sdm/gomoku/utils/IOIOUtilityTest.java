package it.units.sdm.gomoku.utils;

import it.units.sdm.gomoku.EnvVariables;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;


class IOIOUtilityTest {

    @ParameterizedTest
    @ValueSource(strings = "1,2_3,4_5,6,7")
    void readIntegersFromCsvToStringMatrix(String values) {
        try {
            String[][] read = IOUtility.readFromCsvToStringMatrix(EnvVariables.CSV_SAMPLE_FILE_2X2_INT_MATRIX_PROVIDER_RESOURCE_LOCATION);
            String[][] expected = Arrays.stream(values.split("_"))
                    .map(aRow -> aRow.split(","))
                    .toArray(String[][]::new);
            assertTrue(Arrays.deepEquals(expected, read));
        } catch (IOException | URISyntaxException e) {
            fail(e);
        }
    }

    @ParameterizedTest
    @ValueSource(strings = "A,B_C,\"D_EF G,H,'I")
    void readStringsFromCsvToStringMatrix(String values) {
        try {
            String[][] read = IOUtility.readFromCsvToStringMatrix(EnvVariables.CSV_SAMPLE_FILE_2X2_STRING_MATRIX_PROVIDER_RESOURCE_LOCATION);
            String[][] expected = Arrays.stream(values.split("_"))
                    .map(aRow -> aRow.split(","))
                    .toArray(String[][]::new);
            assertTrue(Arrays.deepEquals(expected, read));
        } catch (IOException | URISyntaxException e) {
            fail(e);
        }
    }
}