package it.units.sdm.gomoku.utils;

import it.units.sdm.gomoku.EnvVariables;
import it.units.sdm.gomoku.entities.Board;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.lang.reflect.Method;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static it.units.sdm.gomoku.EnvVariables.CSV_NEW_LINE;
import static it.units.sdm.gomoku.EnvVariables.CSV_SEPARATOR;
import static it.units.sdm.gomoku.utils.TestUtility.*;
import static it.units.sdm.gomoku.utils.Predicates.isNonEmptyString;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestUtility {

    @NotNull
    static String createNxNRandomBoardToString(int N, int randomSeed) {
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
    public static String createNxNRandomBoardToString(int N) {
        final int RANDOM_SEED = 0;
        return createNxNRandomBoardToString(N, RANDOM_SEED);
    }

    @NotNull
    public static Stream<Arguments> provideCoupleOfNonNegativeIntegersTillNExcluded(int N) {
        return IntStream.range(0, N)
                .unordered().parallel()
                .boxed()
                .flatMap(i -> IntStream.range(0, N).mapToObj(j -> Arguments.of(i, j)));
    }

}

class TestUtilityTest {

    private final static int LIMIT_VALUE_FOR_N = 200; // avoid to create too big boards

    @ParameterizedTest
    @CsvFileSource(resources = EnvVariables.INTS_PROVIDER_RESOURCE_LOCATION)
    void createNxNRandomBoardToStringTest_testMethodWithoutSeedParam(int N, TestInfo testInfo) {
        final int DEFAULT_RANDOM_SEED = 0;
        if (Math.abs(N) < LIMIT_VALUE_FOR_N) {
            assertEquals(createNxNRandomBoardToString(N), createNxNRandomBoardToString(N, DEFAULT_RANDOM_SEED));
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
    void createNxNRandomBoardToStringTest_assertHavingNxNStones(int N, int randomSeed) {
        final int expectedNumberOfStonesToBePresent = N * N;
        String boardAsString = createNxNRandomBoardToString(N, randomSeed);
        Map<Board.Stone, Integer> countStonesPerType =
                Arrays.stream(Board.Stone.values())
                        .unordered().parallel()
                        .map(stoneType -> new AbstractMap.SimpleEntry<>(
                                        stoneType,
                                        (int) Arrays.stream(boardAsString.split(CSV_NEW_LINE))
                                                .flatMap(aLine -> Arrays.stream(aLine.split(CSV_SEPARATOR)))
                                                .filter(isNonEmptyString)
                                                .map(Board.Stone::valueOf)
                                                .filter(aCell -> aCell == stoneType)
                                                .count()
                                )
                        )
                        .collect(Collectors.toConcurrentMap(Map.Entry::getKey, Map.Entry::getValue));
        assertEquals(expectedNumberOfStonesToBePresent, countStonesPerType.values().stream().mapToInt(Integer::intValue).sum());
    }

    @ParameterizedTest
    @MethodSource("provideCoupleOfNonNegativeIntegersTill19Excluded")
    void createNxNRandomBoardToStringTest_assertMatrixToBeSquaredOfSizeN(int N, int randomSeed) {
        String boardAsString = createNxNRandomBoardToString(N, randomSeed);
        List<String> lines = Arrays.stream(boardAsString.split(CSV_NEW_LINE))
                .filter(isNonEmptyString)
                .collect(Collectors.toList());
        assertEquals(lines.size(), N);
    }

    private static Stream<Arguments> provideCoupleOfNonNegativeIntegersTill19Excluded() {
        return provideCoupleOfNonNegativeIntegersTillNExcluded(19);
    }
}