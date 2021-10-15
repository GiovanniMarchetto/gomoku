package it.units.sdm.gomoku.model.utils;

import it.units.sdm.gomoku.EnvVariables;
import it.units.sdm.gomoku.model.custom_types.Coordinates;
import it.units.sdm.gomoku.model.custom_types.PositiveInteger;
import it.units.sdm.gomoku.model.entities.Board;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static it.units.sdm.gomoku.model.utils.IOUtility.CSV_NEW_LINE;
import static it.units.sdm.gomoku.model.utils.IOUtility.CSV_SEPARATOR;
import static it.units.sdm.gomoku.model.utils.Predicates.isNonEmptyString;
import static it.units.sdm.gomoku.model.utils.TestUtility.createNxNRandomBoardToStringInCSVFormat;
import static org.junit.jupiter.api.Assertions.*;

public class TestUtility {

    public static final Board.Stone[][] boardStoneFromCsv = TestUtility.readBoardStoneFromCSVFile(EnvVariables.BOARD_19X19_PROVIDER_RESOURCE_LOCATION);

    @NotNull
    public static Board createBoardWithCsvBoardStone() {
        return createBoardFromBoardStone(boardStoneFromCsv, EnvVariables.BOARD_SIZE);
    }

    @NotNull
    public static Board createBoardFromBoardStone(Board.Stone[][] boardStone, PositiveInteger boardSize) {
        Board board = new Board(boardSize);
        try {
            for (int x = 0; x < boardSize.intValue(); x++) {
                for (int y = 0; y < boardSize.intValue(); y++) {
                    if (!boardStone[x][y].isNone())
                        board.occupyPosition(boardStone[x][y], new Coordinates(x, y));
                }
            }
        } catch (Board.NoMoreEmptyPositionAvailableException | Board.PositionAlreadyOccupiedException e) {
            System.err.println(e.getMessage());
        }
        return board;
    }

    @NotNull
    public static Board createBoardFromBoardStone(Board.Stone[][] boardStone, int boardSize) {
        return createBoardFromBoardStone(boardStone, new PositiveInteger(boardSize));
    }

    @NotNull
    public static Board.Stone[][] readBoardStoneFromCSVFile(@NotNull String filePath) {

        Function<String[][], Board.Stone[][]> convertStringMatrixToStoneMatrix = stringMatrix ->
                Arrays.stream(stringMatrix).sequential()
                        .map(aLine -> Arrays.stream(aLine)
                                .map(Board.Stone::valueOf)
                                .toArray(Board.Stone[]::new))
                        .toArray(Board.Stone[][]::new);

        try {
            String[][] boardAsMatrixOfStrings = IOUtility.readFromCsvToStringMatrix(Objects.requireNonNull(filePath));
            return convertStringMatrixToStoneMatrix.apply(boardAsMatrixOfStrings);
        } catch (IOException | URISyntaxException e) {
            fail(e);
            return new Board.Stone[0][0];
        }
    }

    @NotNull
    static String createNxNRandomBoardToStringInCSVFormat(int N, int randomSeed) {
        Random random = new Random(randomSeed);
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                switch (random.nextInt(3)) {
                    case 0 -> s.append(Board.Stone.BLACK);
                    case 1 -> s.append(Board.Stone.WHITE);
                    default -> s.append(Board.Stone.NONE);
                }
                if (j < N - 1) {
                    s.append(CSV_SEPARATOR);
                } else if (i < N - 1) {
                    s.append(CSV_NEW_LINE);
                }
            }
        }

        return s.toString();
    }

    @NotNull
    public static String createNxNRandomBoardToStringInCSVFormat(int N) {
        final int RANDOM_SEED = 0;
        return createNxNRandomBoardToStringInCSVFormat(N, RANDOM_SEED);
    }

    @NotNull
    public static Stream<Arguments> provideCoupleOfNonNegativeIntegersTillNExcluded(int N) {
        return IntStream.range(0, N)
                .unordered().parallel()
                .boxed()
                .flatMap(i -> IntStream.range(0, N).mapToObj(j -> Arguments.of(i, j)));
    }

    @NotNull
    public static Stream<Arguments> readBoardsWithWinCoordsAndResultsFromCSV(@NotNull String filePath) {
        try {
            String json = Files.readString(Paths.get(Objects.requireNonNull(TestUtility.class.getResource(filePath)).toURI()));
            JSONObject jsonObject = new JSONObject(json);

            return ((JSONArray) jsonObject.get("data")).toList()
                    .stream().unordered().parallel()
                    .map(argsForOneTest -> {
                        Map<?, ?> argsMap = (HashMap<?, ?>) argsForOneTest;

                        Board.Stone[][] matrix = ((List<?>) (argsMap).get("matrix"))
                                .stream().sequential()
                                .map(row -> ((List<?>) row)
                                        .stream()
                                        .map(cell -> Board.Stone.valueOf((String) cell))
                                        .toArray(Board.Stone[]::new))
                                .toArray(Board.Stone[][]::new);

                        List<Integer> ints = ((List<?>) argsMap.get("coordinates")).stream().map(x -> (int) x).collect(Collectors.toList());
                        Coordinates coords = new Coordinates(ints.get(0), ints.get(1));

                        boolean expected = (boolean) argsMap.get("expected");

                        return Arguments.of(matrix, coords, expected);
                    });
        } catch (IOException | URISyntaxException e) {
            fail(e);
            return Stream.empty();
        }
    }

    public static int getTotalNumberOfValidStoneInTheGivenBoarsAsStringInCSVFormat(@NotNull final String boardAsCSVString) {
        Map<Board.Stone, Integer> countStonesPerType = getHowManyStonesPerTypeAsMapStartingFromStringRepresentingTheMatrixInCSVFormat(Objects.requireNonNull(boardAsCSVString));
        return countStonesPerType.values().stream().mapToInt(Integer::intValue).sum();
    }

    @NotNull
    private static Map<Board.Stone, Integer> getHowManyStonesPerTypeAsMapStartingFromStringRepresentingTheMatrixInCSVFormat(@NotNull final String boardAsStringMatrix) {
        return Arrays.stream(Board.Stone.values())
                .unordered().parallel()
                .map(stoneType -> new AbstractMap.SimpleEntry<>(
                                stoneType,
                                (int) getRowsAsStreamOfStringFromBoarsProvidedAsStringRepresentingTheMatrixInCSVFormat(Objects.requireNonNull(boardAsStringMatrix))
                                        .flatMap(aCell -> aCell)
                                        .map(Board.Stone::valueOf)
                                        .filter(aCell -> aCell == stoneType)
                                        .count()
                        )
                )
                .collect(Collectors.toConcurrentMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @NotNull
    static Stream<Stream<String>> getRowsAsStreamOfStringFromBoarsProvidedAsStringRepresentingTheMatrixInCSVFormat(@NotNull final String boardAsStringInCSVFormat) {
        return Arrays.stream(Objects.requireNonNull(boardAsStringInCSVFormat).split(CSV_NEW_LINE))
                .filter(isNonEmptyString)
                .map(aLine -> Arrays.stream(aLine.split(CSV_SEPARATOR)));
    }

}

class TestUtilityTest {

    private final static int LIMIT_VALUE_FOR_N = 200; // avoid creating too big boards

    @NotNull
    static Stream<Arguments> provideCoupleOfNonNegativeIntegersTill19Excluded() {
        return TestUtility.provideCoupleOfNonNegativeIntegersTillNExcluded(19);
    }

    @Test
    void readBoardStoneFromCSVFile_testIfMatrixOfCorrectSizeWasRead() {
        final int SIZE = 19;
        Board.Stone[][] board = TestUtility.readBoardStoneFromCSVFile(EnvVariables.BOARD_19X19_PROVIDER_RESOURCE_LOCATION);
        assertTrue(Predicates.isSquareMatrixOfGivenSize.test(board, SIZE));
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
            assertEquals(createNxNRandomBoardToStringInCSVFormat(N), createNxNRandomBoardToStringInCSVFormat(N, DEFAULT_RANDOM_SEED));
        } else {
            Optional<Method> thisMethod = testInfo.getTestMethod();
            Logger.getLogger(this.getClass().getCanonicalName())
                    .warning("Test " + (thisMethod.isPresent() ? thisMethod.get() : "") +
                            " in class " + this.getClass().getCanonicalName() + "ignored for N=" + N +
                            " (value for N is too big)");
        }
    }

    @ParameterizedTest
    @MethodSource("provideCoupleOfNonNegativeIntegersTill19Excluded")
    void createNxNRandomBoardToStringWithGivenRandomSeedTest_assertMatrixToBeSquaredOfSizeNxN(int N, int randomSeed) {
        String boardAsCSVString = createNxNRandomBoardToStringInCSVFormat(N, randomSeed);
        String[][] matrix = TestUtility.getRowsAsStreamOfStringFromBoarsProvidedAsStringRepresentingTheMatrixInCSVFormat(boardAsCSVString)
                .map(aRow -> aRow.toArray(String[]::new))
                .toArray(String[][]::new);
        assertTrue(Predicates.isSquareMatrixOfGivenSize.test(matrix, N));
    }

    @ParameterizedTest
    @MethodSource("provideCoupleOfNonNegativeIntegersTill19Excluded")
    void createNxNRandomBoardToStringTest_assertHavingNxNStones(int N, int randomSeed) {
        final int expectedNumberOfStonesToBePresent = N * N;
        String boardAsCSVString = createNxNRandomBoardToStringInCSVFormat(N, randomSeed);
        int totalNumberOfValidStones = TestUtility.getTotalNumberOfValidStoneInTheGivenBoarsAsStringInCSVFormat(boardAsCSVString);
        assertEquals(expectedNumberOfStonesToBePresent, totalNumberOfValidStones);
    }
}