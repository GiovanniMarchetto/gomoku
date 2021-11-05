package it.units.sdm.gomoku.utils;

import it.units.sdm.gomoku.EnvVariables;
import it.units.sdm.gomoku.model.custom_types.Coordinates;
import it.units.sdm.gomoku.model.custom_types.PositiveInteger;
import it.units.sdm.gomoku.model.entities.Board;
import it.units.sdm.gomoku.model.entities.Stone;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.params.provider.Arguments;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static it.units.sdm.gomoku.utils.IOUtility.CSV_NEW_LINE;
import static it.units.sdm.gomoku.utils.IOUtility.CSV_SEPARATOR;
import static it.units.sdm.gomoku.utils.Predicates.isNonEmptyString;
import static org.junit.jupiter.api.Assertions.fail;

public class TestUtility {

    public static final Stone[][] boardStoneFromCsv = TestUtility.readBoardStoneFromCSVFile(EnvVariables.BOARD_19X19_PROVIDER_RESOURCE_LOCATION);

    @NotNull
    public static Board createBoardWithCsvBoardStone() {
        return createBoardFromBoardStone(boardStoneFromCsv, EnvVariables.BOARD_SIZE);
    }

    @NotNull
    public static Board createBoardFromBoardStone(Stone[][] boardStone, PositiveInteger boardSize) {
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
    public static Board createBoardFromBoardStone(Stone[][] boardStone, int boardSize) {
        return createBoardFromBoardStone(boardStone, new PositiveInteger(boardSize));
    }

    @NotNull
    public static Stone[][] readBoardStoneFromCSVFile(@NotNull String filePath) {

        Function<String[][], Stone[][]> convertStringMatrixToStoneMatrix = stringMatrix ->
                Arrays.stream(stringMatrix).sequential()
                        .map(aLine -> Arrays.stream(aLine)
                                .map(Stone::valueOf)
                                .toArray(Stone[]::new))
                        .toArray(Stone[][]::new);

        try {
            String[][] boardAsMatrixOfStrings = IOUtility.readFromCsvToStringMatrix(Objects.requireNonNull(filePath));
            return convertStringMatrixToStoneMatrix.apply(boardAsMatrixOfStrings);
        } catch (IOException | URISyntaxException e) {
            fail(e);
            return new Stone[0][0];
        }
    }

    @NotNull
    public static String createNxNRandomBoardToStringInCSVFormat(int N, int randomSeed) {
        Random random = new Random(randomSeed);
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                switch (random.nextInt(3)) {
                    case 0 -> s.append(Stone.BLACK);
                    case 1 -> s.append(Stone.WHITE);
                    default -> s.append(Stone.NONE);
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

                        Stone[][] matrix = ((List<?>) (argsMap).get("matrix"))
                                .stream().sequential()
                                .map(row -> ((List<?>) row)
                                        .stream()
                                        .map(cell -> Stone.valueOf((String) cell))
                                        .toArray(Stone[]::new))
                                .toArray(Stone[][]::new);

                        List<Integer> ints = ((List<?>) argsMap.get("coordinates")).stream().map(x -> (int) x).collect(Collectors.toList());
                        Coordinates coords = new Coordinates(ints.get(0), ints.get(1));

                        boolean expected = (boolean) argsMap.get("expected");

                        boolean finishedGame = (boolean) argsMap.get("finishedGame");

                        return Arguments.of(matrix, coords, expected, finishedGame);
                    });
        } catch (IOException | URISyntaxException e) {
            fail(e);
            return Stream.empty();
        }
    }

    public static int getTotalNumberOfValidStoneInTheGivenBoarsAsStringInCSVFormat(@NotNull final String boardAsCSVString) {
        Map<Stone, Integer> countStonesPerType = getHowManyStonesPerTypeAsMapStartingFromStringRepresentingTheMatrixInCSVFormat(Objects.requireNonNull(boardAsCSVString));
        return countStonesPerType.values().stream().mapToInt(Integer::intValue).sum();
    }

    @NotNull
    private static Map<Stone, Integer> getHowManyStonesPerTypeAsMapStartingFromStringRepresentingTheMatrixInCSVFormat(@NotNull final String boardAsStringMatrix) {
        return Arrays.stream(Stone.values())
                .unordered().parallel()
                .map(stoneType -> new AbstractMap.SimpleEntry<>(
                                stoneType,
                                (int) getRowsAsStreamOfStringFromBoarsProvidedAsStringRepresentingTheMatrixInCSVFormat(Objects.requireNonNull(boardAsStringMatrix))
                                        .flatMap(aCell -> aCell)
                                        .map(Stone::valueOf)
                                        .filter(aCell -> aCell == stoneType)
                                        .count()
                        )
                )
                .collect(Collectors.toConcurrentMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @NotNull
    public static Stream<Stream<String>> getRowsAsStreamOfStringFromBoarsProvidedAsStringRepresentingTheMatrixInCSVFormat(@NotNull final String boardAsStringInCSVFormat) {
        return Arrays.stream(Objects.requireNonNull(boardAsStringInCSVFormat).split(CSV_NEW_LINE))
                .filter(isNonEmptyString)
                .map(aLine -> Arrays.stream(aLine.split(CSV_SEPARATOR)));
    }

}