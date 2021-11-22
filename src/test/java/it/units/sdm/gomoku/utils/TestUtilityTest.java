package it.units.sdm.gomoku.utils;

import it.units.sdm.gomoku.EnvVariables;
import it.units.sdm.gomoku.model.entities.Cell;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public
class TestUtilityTest {

    @Test
    void readBoardStoneFromCSVFile_testIfMatrixOfCorrectSizeWasRead() {
        final int SIZE = 19;
        Cell[][] board = TestUtility.readBoardOfCellsFromCSVFile(EnvVariables.BOARD_19X19_PROVIDER_RESOURCE_LOCATION);
        Assertions.assertTrue(Predicates.isSquareMatrixOfGivenSize.test(board, SIZE));
    }

    @Test
    void readBoardStoneFromCSVFile_testIfMatrixContainsOnlyStones() {
        final int SIZE = 19;
        String board = Arrays.stream(TestUtility.readBoardOfCellsFromCSVFile(EnvVariables.BOARD_19X19_PROVIDER_RESOURCE_LOCATION))
                .map(aRow -> Arrays.stream(aRow).map(String::valueOf).collect(Collectors.joining(TestUtility.CSV_SEPARATOR)))
                .collect(Collectors.joining(TestUtility.CSV_NEW_LINE));
        int totalNumberOfValidStonesFound = TestUtility.getTotalNumberOfValidStoneInTheGivenBoardAsStringInCSVFormat(board);
        int expectedTotalNumberOfValidStones = SIZE * SIZE;
        assertEquals(expectedTotalNumberOfValidStones, totalNumberOfValidStonesFound);
    }

    @ParameterizedTest
    @ValueSource(strings = "1,2_3,4_5,6,7")
    void readIntegersFromCsvToStringMatrix(String values) {
        try {
            String[][] read = TestUtility.readFromCsvToStringMatrix(EnvVariables.CSV_SAMPLE_FILE_2X2_INT_MATRIX_PROVIDER_RESOURCE_LOCATION);
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
            String[][] read = TestUtility.readFromCsvToStringMatrix(EnvVariables.CSV_SAMPLE_FILE_2X2_STRING_MATRIX_PROVIDER_RESOURCE_LOCATION);
            String[][] expected = Arrays.stream(values.split("_"))
                    .map(aRow -> aRow.split(","))
                    .toArray(String[][]::new);
            assertTrue(Arrays.deepEquals(expected, read));
        } catch (IOException | URISyntaxException e) {
            fail(e);
        }
    }
}