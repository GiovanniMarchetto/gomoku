package it.units.sdm.gomoku.utils.test;

import it.units.sdm.gomoku.EnvVariables;
import it.units.sdm.gomoku.model.entities.Stone;
import it.units.sdm.gomoku.utils.IOUtility;
import it.units.sdm.gomoku.utils.Predicates;
import it.units.sdm.gomoku.utils.TestUtility;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static it.units.sdm.gomoku.utils.TestUtility.createNxNRandomBoardToStringInCSVFormat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public
class TestIOUtilityTest {

    private final static int LIMIT_VALUE_FOR_N = 200; // avoid creating too big boards

    @NotNull
    static Stream<Arguments> provideCoupleOfNonNegativeIntegersTill19Excluded() {
        return TestUtility.provideCoupleOfNonNegativeIntegersTillNExcluded(19);
    }

    @Test
    void readBoardStoneFromCSVFile_testIfMatrixOfCorrectSizeWasRead() {
        final int SIZE = 19;
        Stone[][] board = TestUtility.readBoardStoneFromCSVFile(EnvVariables.BOARD_19X19_PROVIDER_RESOURCE_LOCATION);
        Assertions.assertTrue(Predicates.isSquareMatrixOfGivenSize.test(board, SIZE));
    }

    @Test
    void readBoardStoneFromCSVFile_testIfMatrixContainsOnlyStones() {
        final int SIZE = 19;
        String board = Arrays.stream(TestUtility.readBoardStoneFromCSVFile(EnvVariables.BOARD_19X19_PROVIDER_RESOURCE_LOCATION))
                .map(aRow -> Arrays.stream(aRow).map(String::valueOf).collect(Collectors.joining(IOUtility.CSV_SEPARATOR)))
                .collect(Collectors.joining(IOUtility.CSV_NEW_LINE));
        int totalNumberOfValidStonesFound = TestUtility.getTotalNumberOfValidStoneInTheGivenBoarsAsStringInCSVFormat(board);
        int expectedTotalNumberOfValidStones = SIZE * SIZE;
        assertEquals(expectedTotalNumberOfValidStones, totalNumberOfValidStonesFound);
    }

    @ParameterizedTest
    @CsvFileSource(resources = EnvVariables.INTS_PROVIDER_RESOURCE_LOCATION)
    void createNxNRandomBoardToStringTest_testMethodWithoutSeedParam(int N, TestInfo testInfo) {
        final int DEFAULT_RANDOM_SEED = 0;
        if (Math.abs(N) < LIMIT_VALUE_FOR_N) {
            assertEquals(createNxNRandomBoardToStringInCSVFormat(N), TestUtility.createNxNRandomBoardToStringInCSVFormat(N, DEFAULT_RANDOM_SEED));
        } else {
            Optional<Method> thisMethod = testInfo.getTestMethod();
            Logger.getLogger(this.getClass().getCanonicalName())
                    .info("Test " + (thisMethod.isPresent() ? thisMethod.get() : "") +
                            " in class " + this.getClass().getCanonicalName() + "ignored for N=" + N +
                            " (value for N is too big)\n");
        }
    }

    @ParameterizedTest
    @MethodSource("provideCoupleOfNonNegativeIntegersTill19Excluded")
    void createNxNRandomBoardToStringWithGivenRandomSeedTest_assertMatrixToBeSquaredOfSizeNxN(int N, int randomSeed) {
        String boardAsCSVString = TestUtility.createNxNRandomBoardToStringInCSVFormat(N, randomSeed);
        String[][] matrix = TestUtility.getRowsAsStreamOfStringFromBoarsProvidedAsStringRepresentingTheMatrixInCSVFormat(boardAsCSVString)
                .map(aRow -> aRow.toArray(String[]::new))
                .toArray(String[][]::new);
        assertTrue(Predicates.isSquareMatrixOfGivenSize.test(matrix, N));
    }

    @ParameterizedTest
    @MethodSource("provideCoupleOfNonNegativeIntegersTill19Excluded")
    void createNxNRandomBoardToStringTest_assertHavingNxNStones(int N, int randomSeed) {
        final int expectedNumberOfStonesToBePresent = N * N;
        String boardAsCSVString = TestUtility.createNxNRandomBoardToStringInCSVFormat(N, randomSeed);
        int totalNumberOfValidStones = TestUtility.getTotalNumberOfValidStoneInTheGivenBoarsAsStringInCSVFormat(boardAsCSVString);
        assertEquals(expectedNumberOfStonesToBePresent, totalNumberOfValidStones);
    }
}